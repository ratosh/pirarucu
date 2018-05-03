package pirarucu.tuning.pbil

import pirarucu.board.Piece
import pirarucu.tuning.ErrorCalculator
import pirarucu.tuning.TunableConstants
import pirarucu.util.EpdFileLoader
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object PbilTunningApplication {

    private const val INTERACTIONS = 300

    private const val numberOfThreads = 2
    private val workers = arrayOfNulls<ErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("/mnt/d/chess/epds/quiet_labeled_v6.epd")

    private val tuningObjects: List<PbilTunningObject>
        get() {
            val tuningObjects = ArrayList<PbilTunningObject>()

            /*
            tuningObjects.add(PbilTunningObject(
                "PHASE",
                TunableConstants.PHASE_PIECE_VALUE,
                intArrayOf(0, 4, 5, 5, 6, 7, 0),
                false, 0, 6))
                */

            /*

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_SCORE_MG",
                TunableConstants.MATERIAL_SCORE_MG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, 0))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_SCORE_EG",
                TunableConstants.MATERIAL_SCORE_EG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, 0))
                */

            tuningObjects.add(PbilTunningObject(
                "MG[PAWN]",
                TunableConstants.MG_PSQT[Piece.PAWN],
                intArrayOf(0, 0, 0, 0,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    0, 0, 0, 0
                ),
                true, 0, 1, 2, 3, 28, 29, 30, 31))

            /*
            tuningObjects.add(PbilTunningObject(
                "EG[PAWN]",
                TunableConstants.EG_PSQT[Piece.PAWN],
                intArrayOf(0, 0, 0, 0,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    0, 0, 0, 0
                ),
                true, 0, 1, 2, 3, 28, 29, 30, 31))
                */

            /*
            tuningObjects.add(PbilTunningObject(
                "MG[KNIGHT]",
                TunableConstants.MG_PSQT[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "EG[KNIGHT]",
                TunableConstants.EG_PSQT[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "MG[BISHOP]",
                TunableConstants.MG_PSQT[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "EG[BISHOP]",
                TunableConstants.EG_PSQT[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "MG[ROOK]",
                TunableConstants.MG_PSQT[Piece.ROOK],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "EG[ROOK]",
                TunableConstants.EG_PSQT[Piece.ROOK],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "MG[QUEEN]",
                TunableConstants.MG_PSQT[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "EG[QUEEN]",
                TunableConstants.EG_PSQT[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "MG[KING]",
                TunableConstants.MG_PSQT[Piece.KING],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "EG[KING]",
                TunableConstants.EG_PSQT[Piece.KING],
                intArrayOf(6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6,
                    6, 6, 6, 6
                ),
                true))
            */

            /*
            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[PAWN]",
                TunableConstants.MATERIAL_IMBALANCE_OURS[Piece.PAWN],
                intArrayOf(0, 3, 0, 0, 0, 0),
                true, 0, 2, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[KNIGHT]",
                TunableConstants.MATERIAL_IMBALANCE_OURS[Piece.KNIGHT],
                intArrayOf(0, 3, 3, 0, 0, 0),
                true, 0, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[BISHOP]",
                TunableConstants.MATERIAL_IMBALANCE_OURS[Piece.BISHOP],
                intArrayOf(0, 3, 3, 3, 0, 0),
                true, 0, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[ROOK]",
                TunableConstants.MATERIAL_IMBALANCE_OURS[Piece.ROOK],
                intArrayOf(0, 3, 3, 3, 3, 0),
                true, 0, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_OURS[QUEEN]",
                TunableConstants.MATERIAL_IMBALANCE_OURS[Piece.QUEEN],
                intArrayOf(0, 3, 3, 3, 3, 0),
                true, 0, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[PAWN]",
                TunableConstants.MATERIAL_IMBALANCE_THEIRS[Piece.PAWN],
                intArrayOf(0, 3, 0, 0, 0, 0),
                true, 0, 2, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[KNIGHT]",
                TunableConstants.MATERIAL_IMBALANCE_THEIRS[Piece.KNIGHT],
                intArrayOf(0, 3, 3, 0, 0, 0),
                true, 0, 3, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[BISHOP]",
                TunableConstants.MATERIAL_IMBALANCE_THEIRS[Piece.BISHOP],
                intArrayOf(0, 3, 3, 3, 0, 0),
                true, 0, 4, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[ROOK]",
                TunableConstants.MATERIAL_IMBALANCE_THEIRS[Piece.ROOK],
                intArrayOf(0, 3, 3, 3, 3, 0),
                true, 0, 5))

            tuningObjects.add(PbilTunningObject(
                "MATERIAL_IMBALANCE_THEIRS[QUEEN]",
                TunableConstants.MATERIAL_IMBALANCE_THEIRS[Piece.QUEEN],
                intArrayOf(0, 3, 3, 3, 3, 0),
                true, 0, 5))
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
    private fun optimize(tuningObjects: List<PbilTunningObject>) {
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
                TunableConstants.update()
                val error = executeTest()
                tuningObject.reportOriginal(error)
                println("Starting " + tuningObject.name + " error " + error)
                for (j in 0 until tuningObject.population) {
                    println("Population $j")
                    val population = tuningObject.nextPopulation()
                    TunableConstants.update()
                    if (population != null) {
                        tuningObject.reportCurrent(population, executeTest())
                    } else {
                        skipped++
                        println("Skipped")
                    }
                }
                if (!improving &&
                    tuningObject.population - skipped > 2 &&
                    skipped.toDouble() / (tuningObject.population - 1) < 0.5) {
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
