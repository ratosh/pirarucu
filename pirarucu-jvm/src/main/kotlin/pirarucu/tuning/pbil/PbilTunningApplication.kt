package pirarucu.tuning.pbil

import pirarucu.board.Piece
import pirarucu.eval.EvalConstants
import pirarucu.tuning.ErrorCalculator
import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object PbilTunningApplication {

    private const val INTERACTIONS = 300

    private const val numberOfThreads = 1
    private val workers = arrayOfNulls<ErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("d:\\chess\\epds\\quiet_labeled_v6.epd")

    private val tuningObjects: List<PbilTunningObject>
        get() {
            val tuningObjects = ArrayList<PbilTunningObject>()
            /*
            tuningObjects.add(PbilTunningObject(
                "PHASE",
                EvalConstants.PHASE_PIECE_VALUE,
                intArrayOf(0, 8, 9, 9, 10, 11, 0),
                false, 0, 6))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[PAWN]",
                EvalConstants.MATERIAL_IMBALANCE_OURS[Piece.PAWN],
                intArrayOf(4, 4, 0, 0, 0, 0),
                true, 2, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[KNIGHT]",
                EvalConstants.MATERIAL_IMBALANCE_OURS[Piece.KNIGHT],
                intArrayOf(4, 4, 4, 0, 0, 0),
                true, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[BISHOP]",
                EvalConstants.MATERIAL_IMBALANCE_OURS[Piece.BISHOP],
                intArrayOf(4, 4, 4, 4, 0, 0),
                true, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[ROOK]",
                EvalConstants.MATERIAL_IMBALANCE_OURS[Piece.ROOK],
                intArrayOf(4, 4, 4, 4, 4, 0),
                true, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[QUEEN]",
                EvalConstants.MATERIAL_IMBALANCE_OURS[Piece.QUEEN],
                intArrayOf(4, 4, 4, 4, 4, 0),
                true, 5))
                */

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[PAWN]",
                EvalConstants.MATERIAL_IMBALANCE_THEIRS[Piece.PAWN],
                intArrayOf(4, 4, 0, 0, 0, 0),
                true, 2, 3, 4, 5))

            /*
            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[KNIGHT]",
                EvalConstants.MATERIAL_IMBALANCE_THEIRS[Piece.KNIGHT],
                intArrayOf(4, 4, 4, 0, 0, 0),
                true, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[BISHOP]",
                EvalConstants.MATERIAL_IMBALANCE_THEIRS[Piece.BISHOP],
                intArrayOf(4, 4, 4, 4, 0, 0),
                true, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[ROOK]",
                EvalConstants.MATERIAL_IMBALANCE_THEIRS[Piece.ROOK],
                intArrayOf(4, 4, 4, 4, 4, 0),
                true, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[QUEEN]",
                EvalConstants.MATERIAL_IMBALANCE_THEIRS[Piece.QUEEN],
                intArrayOf(4, 4, 4, 4, 4, 0),
                true, 5))
                */

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
        println("Starting error $bestError")
        for (tuningObject in tuningObjects) {
            tuningObject.reportOriginal(bestError)
        }

        for (i in 0 until INTERACTIONS) {
            println("Starting interaction $i")
            var improving = false
            for (tuningObject in tuningObjects) {
                var skipped = 0
                val error = executeTest()
                tuningObject.reportOriginal(error)
                println("Starting " + tuningObject.name + " error " + error)
                for (j in 0 until tuningObject.population) {
                    println("Population $j")
                    val population = tuningObject.nextPopulation()
                    EvalConstants.update()
                    if (population != null) {
                        tuningObject.reportCurrent(population, executeTest())
                    } else {
                        skipped++
                        println("Skipped")
                    }
                }
                if (!improving &&
                    tuningObject.population - skipped > 2 &&
                    skipped.toDouble() / (tuningObject.population - 1) < 0.9) {
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
