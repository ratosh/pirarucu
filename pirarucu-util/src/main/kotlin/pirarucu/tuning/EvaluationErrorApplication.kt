package pirarucu.tuning

import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object EvaluationErrorApplication {

    private const val THREADS = 2

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val list = mutableListOf<EpdInfo>()
        val zurichess = EpdFileLoader("g:\\chess\\epds\\quiet_labeled.epd")
        list.addAll(zurichess.getEpdInfoList())
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        println("Using ${epdList.size} positions")
        val evaluator = EvaluationErrorEvaluator(THREADS)
        val timeTaken = measureTimeMillis {
            evaluator.evaluate(epdList)
        }
        println("Evaluation error " + ErrorUtil.calculate(epdList))
        println("Eval time taken $timeTaken")
    }
}
