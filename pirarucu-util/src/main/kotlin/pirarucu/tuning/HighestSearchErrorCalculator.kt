package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.util.EpdInfo
import java.util.concurrent.Callable

class HighestSearchErrorCalculator(size: Int, depth: Int) : Callable<Double> {
    private var constantCalculated: Boolean = false
    private var constant: Double = 0.toDouble()

    private val transpositionTable = TranspositionTable()
    private val searchOptions = SearchOptions()
    private val mainSearch = MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable)

    private val epdInfoList = mutableListOf<EpdInfo>()
    private var board: Board = Board()

    private val largestError = DoubleArray(size)
    private val largestErrorFen = Array(size) { "" }

    init {
        searchOptions.depth = depth
        searchOptions.minSearchTime = 60000L
        searchOptions.maxSearchTime = 60000L

        transpositionTable.resize(1)
    }

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
        var error = 0.0
        println(Thread.currentThread().name)
        for ((index, entry) in epdInfoList.withIndex()) {
            BoardFactory.setBoard(entry.fenPosition, board)
            transpositionTable.reset()
            mainSearch.searchInfo.history.reset()
            searchOptions.startControl()
            searchOptions.stop = false
            mainSearch.search(board)
            val searchValue = mainSearch.searchInfo.bestScore * GameConstants.COLOR_FACTOR[board.colorToMove]

            val entryError = Math.pow(entry.result - calculateSigmoid(searchValue, currentConstant), 2.0)

            error += entryError
            if (0.89 < entryError) {
                println("$index Big error $entryError ($searchValue) | ${entry.fenPosition}")
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
