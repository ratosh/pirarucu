package pirarucu.tuning

import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object SearchBigErrorApplication {

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\psf.epd"
    private const val START_DEPTH = 1
    private const val FINISH_DEPTH = 11
    private const val DEPTH_INCREMENT = 1
    private const val THREADS = 2

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
            val timeTaken = measureTimeMillis {
                evaluator.evaluate(epdList, currentDepth)
            }
            println("Search $currentDepth error " + ErrorUtil.calculate(epdList))
            println("Entries ${epdList.size} | Time taken $timeTaken")
            ErrorUtil.setError(epdList)
            mateList.addAll(epdList.filter { it.error == 1.0 })
            epdList = epdList
                .filter {
                    it.error >= 0.8 &&
                        !mateList.contains(it)
                }
                .sortedByDescending { it.error }
            epdList.forEach {
                println("Entry error ${it.error} -> ${it.fenPosition}")
            }
            currentDepth++
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
