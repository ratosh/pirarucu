package pirarucu.tuning.pbil

import pirarucu.eval.EvalConstants
import pirarucu.tuning.ErrorCalculator
import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object PbilTunningApplication {

    private const val INTERACTIONS = 300

    const val numberOfThreads = 1
    val workers = arrayOfNulls<ErrorCalculator>(numberOfThreads)
    val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    val epdFileLoader = EpdFileLoader("d:\\chess\\epds\\quiet_labeled_v6.epd")

    val tuningObjects: List<PbilTunningObject>
        get() {
            val tuningObjects = ArrayList<PbilTunningObject>()

            tuningObjects.add(PbilTunningObject(
                "PHASE",
                EvalConstants.PHASE_PIECE_VALUE,
                intArrayOf(1, 8, 9, 9, 10, 11, 1),
                false, 0, 6))

            return tuningObjects
        }

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // setup
        for (i in workers.indices) {
            workers[i] = ErrorCalculator()
        }
        var workerIndex = 0
        val iterator = epdFileLoader.getEpdInfoList()
        for (epdInfo in iterator) {
            workers[workerIndex]!!.addFenWithScore(epdInfo.fenPosition, epdInfo.averageResult)
            workerIndex = if (workerIndex == numberOfThreads - 1) 0 else workerIndex + 1
        }
        optimize(tuningObjects)
        executor.shutdown()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun optimize(tuningObjects: List<PbilTunningObject>) {
        val bestError = executeTest()
        println("Original error $bestError")
        for (tuningObject in tuningObjects) {
            tuningObject.reportOriginal(bestError)
        }

        for (i in 0 until INTERACTIONS) {
            println("Starting interaction $i")
            var improving = false
            for (tuningObject in tuningObjects) {
                var skipped = 0
                println("Starting " + tuningObject.name)
                for (j in 0 until tuningObject.population) {
                    println("Population $j")
                    val population = tuningObject.nextPopulation()
                    EvalConstants.update()
                    if (population != null) {
                        val error = executeTest()
                        tuningObject.reportCurrent(population, error)
                    } else {
                        skipped++
                        println("Skipped")
                    }
                }
                if (!improving && skipped.toDouble() / tuningObject.population < 0.9) {
                    improving = true
                }
                println("Skip proportion " + (skipped.toDouble() / tuningObject.population))
                tuningObject.finishInteraction()
            }
            if (!improving) {
                println("Seems like we are not improving")
                break
            }
        }

        println("Optimization done.")
        for (tuningObject in tuningObjects) {
            tuningObject.printBestElements()
        }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun executeTest(): Double {
        val list = ArrayList<Future<Double>>()
        for (i in workers.indices) {
            val submit = executor.submit(workers[i])
            list.add(submit)
        }
        var totalError = 0.0
        for (future in list) {
            val value = future.get()
            totalError += value!!
        }
        return totalError / numberOfThreads
    }
}
