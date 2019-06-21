package pirarucu.tuning

import pirarucu.epd.EpdFileWriter
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object SearchBigErrorApplication {

    private const val FILE_NAME = "g:\\chess\\epds\\texel-sets\\ruy_tune_quiescent_positions_revised.epd"
    private const val START_DEPTH = 1
    private const val FINISH_DEPTH = 26
    private const val THREADS = 6

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val list = mutableListOf<EpdInfo>()
        val zurichess = EpdFileLoader(FILE_NAME)
        list.addAll(zurichess.epdList)
        var epdList = InvalidPositionFilter(THREADS).filter(list)
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
        mateList
            .forEach {
                println("Huge error ${it.eval} -> ${it.fenPosition}")
            }
        epdList = epdList
            .filter { it.error >= 0.8 }

        val writer = EpdFileWriter(FILE_NAME)
        writer.flush(epdList)
        writer.flush(mateList)
    }
}
