package pirarucu.tuning

import pirarucu.eval.EvalConstants
import pirarucu.uci.UciOutput
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
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
        val list = mutableListOf<EpdInfo>()
        val zurichess = EpdFileLoader("g:\\chess\\epds\\quiet_labeled.epd")
        val alvaro = EpdFileLoader("g:\\chess\\epds\\quiescent_positions_with_results.epd")
        list.addAll(zurichess.getEpdInfoList())
        list.addAll(alvaro.getEpdInfoList())
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        UciOutput.silent = true
        val evaluator = SearchErrorEvaluator(THREADS)
        val timeTaken = measureNanoTime {
            evaluator.evaluate(epdList, WANTED_DEPTH, 1)
            println("Search $WANTED_DEPTH error " + ErrorUtil.calculate(epdList))
        }
        println("Time taken " + timeTaken / 1_000_000)
    }
}
