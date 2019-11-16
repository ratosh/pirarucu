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

object SearchLowDepthErrorApplication {

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\psf.epd"
    private const val RESULT_FILE_NAME = FILE_NAME + "r"
    private const val START_DEPTH = 1
    private const val FINISH_DEPTH = 12
    private const val DEPTH_INCREMENT = 5
    private const val THREADS = 6
    private const val WANTED_ERROR = 0.2

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val epdFileLoader = EpdFileLoader(FILE_NAME)
        var epdList = InvalidPositionFilter(THREADS).filter(epdFileLoader.epdList)
        println("Using ${epdList.size} positions")
        var currentDepth = START_DEPTH
        val error = mutableListOf<EpdInfo>()
        while (currentDepth < FINISH_DEPTH && epdList.isNotEmpty()) {
            val evaluator = MainSearchEvaluator(THREADS, currentDepth)
            println("Checking ${epdList.size} entries depth $currentDepth")
            val timeTaken = measureTimeMillis {
                evaluator.evaluate(epdList)
            }
            println("Search depth $currentDepth error " + ErrorUtil.calculate(epdList))

            val previousDepthError = epdList.filter {
                it.error <= WANTED_ERROR && abs(it.eval) < EvalConstants.SCORE_MATE
            }
            println("Agree ${previousDepthError.size}")
            error.addAll(previousDepthError)

            println("Time taken $timeTaken")
            ErrorUtil.setError(epdList)
            epdList = epdList.filter {
                        it.error > WANTED_ERROR && abs(it.eval) < EvalConstants.SCORE_MATE
                    }
            currentDepth += DEPTH_INCREMENT
        }

        val writer = EpdFileWriter(RESULT_FILE_NAME)
        writer.flush(error)
    }
}
