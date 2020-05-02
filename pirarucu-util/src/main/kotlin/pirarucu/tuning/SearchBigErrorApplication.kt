package pirarucu.tuning

import pirarucu.epd.EpdFileWriter
import pirarucu.eval.EvalConstants
import pirarucu.tuning.evaluator.MainSearchEvaluator
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.lang.Math.*
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object SearchBigErrorApplication {

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\zuri_quiet_labeled.epd"
    private const val RESULT_FILE_NAME = FILE_NAME + "e"
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
        var currentDepth = START_DEPTH
        val mateList = mutableListOf<EpdInfo>()
        while (currentDepth < FINISH_DEPTH && epdList.isNotEmpty()) {
            val evaluator = MainSearchEvaluator(THREADS, currentDepth)
            println("Checking ${epdList.size} entries depth $currentDepth")
            val timeTaken = measureTimeMillis {
                evaluator.evaluate(epdList)
            }
            println("Search depth $currentDepth error " + ErrorUtil.calculate(epdList))
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

        val writer = EpdFileWriter(RESULT_FILE_NAME)
        writer.flush(list)
    }
}
