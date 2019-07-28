package pirarucu.tuning

import pirarucu.epd.EpdFileWriter
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
        val list = mutableListOf<EpdInfo>()
        val epdFileLoader = EpdFileLoader(FILE_NAME)
        list.addAll(epdFileLoader.epdList)
        var epdList = InvalidPositionFilter(THREADS).filter(list)
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
        mateList
                .forEach {
                    println("Mate error ${it.eval} -> ${it.fenPosition}")
                }
        println("Found ${mateList.size} mates")
        list.addAll(mateList)
        list.addAll(epdList.filter { it.error >= 0.8 })
        println("Result disagree on ${epdList.size} entries")

        val writer = EpdFileWriter(FILE_NAME)
        writer.flush(list)
    }
}
