package pirarucu.tuning

import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object SearchErrorApplication {

    private const val WANTED_DEPTH = 4
    private const val THREADS = 2

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val list = mutableListOf<EpdInfo>()
        val zurichess = EpdFileLoader("g:\\chess\\epds\\quiet_labeled.epd")
        list.addAll(zurichess.getEpdInfoList())
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        println("Using ${epdList.size} positions")
        val evaluator = SearchErrorEvaluator(THREADS)
        val timeTaken = measureTimeMillis {
            evaluator.evaluate(epdList, WANTED_DEPTH, 1)
        }
        println("Search $WANTED_DEPTH error " + ErrorUtil.calculate(epdList))
        println("Time taken $timeTaken")
    }
}
