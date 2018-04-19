package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.Evaluator
import java.util.concurrent.Callable

class HighestErrorCalculator(size: Int) : Callable<Double> {
    private var constantCalculated: Boolean = false
    private var constant: Double = 0.toDouble()

    private val fens = HashMap<String, Double>()
    private var board: Board = Board()

    private val largestError = DoubleArray(size)
    private val largestErrorFen = Array(size, { "" })

    fun addFenWithScore(fen: String, score: Double) {
        fens[fen] = score
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
        for ((key, value) in fens) {
            BoardFactory.setBoard(key, board)
            try {
                val entryError = Math.pow(value - calculateSigmoid(Evaluator.evaluate(board),
                    currentConstant), 2.0)

                if (error < entryError) {
                    error = entryError
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
                    largestErrorFen[replaceIndex] = key
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return highestError
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
