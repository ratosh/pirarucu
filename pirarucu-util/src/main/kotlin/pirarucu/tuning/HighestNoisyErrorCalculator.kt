package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.QuiescenceSearch
import pirarucu.search.SearchInfo
import pirarucu.util.EpdInfo
import java.util.concurrent.Callable

class HighestNoisyErrorCalculator(size: Int) : Callable<Double> {
    private var constantCalculated: Boolean = false
    private var constant: Double = 0.toDouble()

    private val searchInfo = SearchInfo(TranspositionTable())
    private val qSearch = QuiescenceSearch(searchInfo)

    private val epdInfoList = mutableListOf<EpdInfo>()
    private var board: Board = Board()

    private val largestError = DoubleArray(size)
    private val largestErrorFen = Array(size) { "" }

    fun addEpdInfo(epdInfo: EpdInfo) {
        epdInfoList.add(epdInfo)
    }

    override fun call(): Double? {
        if (!constantCalculated) {
            constant = BEST_CONSTANT
            constantCalculated = true
        }
        return calculateError(constant)
    }

    private fun calculateError(currentConstant: Double): Double {
        var highestError = 0.0
        var error = 0.0
        for (entry in epdInfoList) {
            BoardFactory.setBoard(entry.fenPosition, board)
            searchInfo.history.reset()
            val searchValue = qSearch.search(board, 0, EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX) *
                GameConstants.COLOR_FACTOR[board.colorToMove]

            val entryError = Math.pow(entry.result - calculateSigmoid(searchValue,
                currentConstant), 2.0)

            error += entryError
            if (highestError < entryError) {
                println(Thread.currentThread().name + " New highest error $entryError ($searchValue) | ${entry.fenPosition}")
                highestError = entryError
            }

            var replaceIndex = -1
            for (index in largestError.size - 1 downTo 0) {
                if (entryError > largestError[index]) {
                    if (replaceIndex >= 0) {
                        largestErrorFen[index + 1] = largestErrorFen[index]
                        largestError[index + 1] = largestError[index]
                    }
                    replaceIndex = index
                } else {
                    break
                }
            }
            if (replaceIndex >= 0) {
                largestError[replaceIndex] = entryError
                largestErrorFen[replaceIndex] = entry.fenPosition
            }
        }

        return error / epdInfoList.size
    }

    override fun toString(): String {
        val buffer = StringBuilder()

        for (index in largestError.indices) {
            buffer.appendln("F: " + largestErrorFen[index] + " | " + largestError[index])
        }

        return buffer.toString()
    }

    companion object {
        const val BEST_CONSTANT = 1.4

        fun calculateSigmoid(score: Int, constant: Double): Double {
            return 1 / (1 + Math.pow(10.0, -constant * score / 400))
        }
    }
}
