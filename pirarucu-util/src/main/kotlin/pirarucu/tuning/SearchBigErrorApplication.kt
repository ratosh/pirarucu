package pirarucu.tuning

import pirarucu.eval.EvalConstants
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.lang.Math.*
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object SearchBigErrorApplication {

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\zuri_quiet_labeled.epd"
    private const val START_DEPTH = 1
    private const val FINISH_DEPTH = 16
    private const val DEPTH_INCREMENT = 1
    private const val THREADS = 5

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val epdFileLoader = EpdFileLoader(FILE_NAME)
        var epdList = InvalidPositionFilter(THREADS).filter(epdFileLoader.epdList)
        println("Using ${epdList.size} positions")
        val evaluator = SearchErrorEvaluator(THREADS)
        var currentDepth = START_DEPTH
        val mateList = mutableListOf<EpdInfo>()
        while (currentDepth < FINISH_DEPTH && epdList.isNotEmpty()) {
            println("Checking ${epdList.size} entries depth $currentDepth")
            val timeTaken = measureTimeMillis {
                evaluator.evaluate(epdList, currentDepth)
            }
            println("Search depth $currentDepth error " + ErrorUtil.calculate(epdList))
            val explodedSearch = epdList
                    .sortedByDescending { it.nodes }
                    .subList(0, min(epdList.size, min(100, max(10, epdList.size / 10))))
            explodedSearch.forEach {
                println("Slow search (${it.time}|${it.nodes}) ${it.eval} = ${it.fenPosition}")
            }
            println("Time taken $timeTaken")
            ErrorUtil.setError(epdList)
            mateList.addAll(epdList
                    .filter {
                        it.error >= 0.1 &&
                                abs(it.eval) > EvalConstants.SCORE_MATE
                    })
            epdList = epdList
                    .filter {
                        it.error >= 0.2 + currentDepth.toDouble() / 50 &&
                                !mateList.contains(it)
                    }
            currentDepth += DEPTH_INCREMENT
        }
        println("Found ${mateList.size} disagree mates")
        val list = mutableListOf<EpdInfo>()
        list.addAll(epdList.filter { it.error >= 0.8 })
        println("Result disagree on ${list.size} entries")
        list.sortedByDescending { it.eval }.forEach {
            println("Disagree (${it.result}|${it.eval}) -> ${it.fenPosition}")
        }
        list.addAll(mateList)

        /*
        val writer = EpdFileUpdater(FILE_NAME)
        writer.flush(list)
         */
    }
}
