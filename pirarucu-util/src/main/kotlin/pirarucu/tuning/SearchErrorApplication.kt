package pirarucu.tuning

import pirarucu.eval.EvalConstants
import pirarucu.uci.UciOutput
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureNanoTime

object SearchErrorApplication {

    private const val WANTED_DEPTH = 4
    private const val THREADS = 4
    private val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\big3.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true
        val evaluator = SearchErrorEvaluator(THREADS)
        val epdList = InvalidPositionFilter(THREADS).filter(epdFileLoader.getEpdInfoList())
        val timeTaken = measureNanoTime {
            evaluator.evaluate(epdList, WANTED_DEPTH, 1)
            println("Search $WANTED_DEPTH error " + ErrorUtil.calculate(epdList, 1.4))
        }
        println("Time taken " + timeTaken / 1_000_000)
    }
}
