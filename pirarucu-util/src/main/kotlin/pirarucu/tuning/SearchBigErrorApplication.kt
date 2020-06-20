package pirarucu.tuning

import pirarucu.epd.EpdFileUpdater
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

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\lichess_quiet_v2.epd"
    private const val RESULT_FILE_NAME = FILE_NAME + "e"
    private const val START_DEPTH = 2
    private const val FINISH_DEPTH = 17
    private const val DEPTH_INCREMENT = 1
    private const val THREADS = 4

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
                        it.error >= 0.01 &&
                                abs(it.eval) > EvalConstants.SCORE_MATE
                    })
            epdList = epdList
                    .filter {
                        abs(it.eval) > 200 &&
                        it.error >= 0.1 &&
                                abs(it.eval) < EvalConstants.SCORE_MATE
                    }
            currentDepth += DEPTH_INCREMENT
        }
        val list = mutableListOf<EpdInfo>()
        println("Result disagree on ${epdList.size} entries")
        epdList.filter { abs(it.eval) > 2000 && it.error >= 0.01 }
                .sortedBy { abs(it.eval) }
                .forEach {
            list.add(it)
            println("Big disagree (${it.result}|${it.eval}) -> ${it.fenPosition}")
        }
        println("Found ${mateList.size} disagree mates")
        mateList.forEach {
            list.add(it)
            println("Mate (${it.toPgnResult()}) -> ${it.fenPosition}")
        }

        val writer = EpdFileUpdater(FILE_NAME)
        writer.flush(list)
    }
}
