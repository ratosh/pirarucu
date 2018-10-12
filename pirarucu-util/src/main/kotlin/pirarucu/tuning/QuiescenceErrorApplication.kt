package pirarucu.tuning

import pirarucu.eval.EvalConstants
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureNanoTime

object QuiescenceErrorApplication {

    private const val THREADS = 4
    private val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\big3.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false
        val evaluator = QuiescenceEvaluator(THREADS)
        val epdList = InvalidPositionFilter(THREADS).filter(epdFileLoader.getEpdInfoList())
        val timeTaken = measureNanoTime {
            evaluator.evaluate(epdList)
            println("Quiescence error " + ErrorUtil.calculate(epdList, 1.4))
        }
        println("Time taken " + timeTaken / 1_000_000)
    }
}
