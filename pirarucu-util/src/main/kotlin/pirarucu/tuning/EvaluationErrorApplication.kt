package pirarucu.tuning

import pirarucu.eval.EvalConstants
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureNanoTime

object EvaluationErrorApplication {

    private const val THREADS = 1

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false
        val evaluator = EvaluationErrorEvaluator(THREADS)
        val list = mutableListOf<EpdInfo>()
        val zurichess = EpdFileLoader("g:\\chess\\epds\\quiet_labeled.epd")
        val alvaro = EpdFileLoader("g:\\chess\\epds\\quiescent_positions_with_results.epd")
        list.addAll(zurichess.getEpdInfoList())
        list.addAll(alvaro.getEpdInfoList())
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        println("Using ${epdList.size} positions")
        val timeTaken = measureNanoTime {
            evaluator.evaluate(epdList)
        }
        println("Eval time taken " + timeTaken / 1_000_000)
        val t2 = measureNanoTime {
            println("Evaluation error " + ErrorUtil.calculate(epdList))
        }
        println("Calculate time taken " + t2 / 1_000_000)
    }
}
