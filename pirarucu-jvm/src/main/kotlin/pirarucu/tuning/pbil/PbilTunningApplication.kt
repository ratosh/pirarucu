package pirarucu.tuning.pbil

import pirarucu.eval.EvalConstants
import pirarucu.tuning.ErrorCalculator
import pirarucu.tuning.TunableConstants
import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object PbilTunningApplication {

    private const val INTERACTIONS = 1000

    private const val numberOfThreads = 2
    private val workers = arrayOfNulls<ErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("/mnt/d/chess/epds/quiet-labeled.epd")

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

            /*
            tuningObjects.add(PbilTunningObject(
                "PSQT_MG[PAWN]",
                TunableConstants.PSQT_MG[Piece.PAWN],
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

            tuningObjects.add(PbilTunningObject(
                "PSQT_EG[PAWN]",
                TunableConstants.PSQT_EG[Piece.PAWN],
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

            tuningObjects.add(PbilTunningObject(
                "PSQT_MG[KNIGHT]",
                TunableConstants.PSQT_MG[Piece.KNIGHT],
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
                "PSQT_EG[KNIGHT]",
                TunableConstants.PSQT_EG[Piece.KNIGHT],
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
                "PSQT_MG[BISHOP]",
                TunableConstants.PSQT_MG[Piece.BISHOP],
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
                "PSQT_EG[BISHOP]",
                TunableConstants.PSQT_EG[Piece.BISHOP],
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
                "PSQT_MG[ROOK]",
                TunableConstants.PSQT_MG[Piece.ROOK],
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
                "PSQT_EG[ROOK]",
                TunableConstants.PSQT_EG[Piece.ROOK],
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
                "PSQT_MG[QUEEN]",
                TunableConstants.PSQT_MG[Piece.QUEEN],
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
                "PSQT_EG[QUEEN]",
                TunableConstants.PSQT_EG[Piece.QUEEN],
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
                "PSQT_MG[KING]",
                TunableConstants.PSQT_MG[Piece.KING],
                intArrayOf(8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8
                ),
                true))

            tuningObjects.add(PbilTunningObject(
                "PSQT_EG[KING]",
                TunableConstants.PSQT_EG[Piece.KING],
                intArrayOf(8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8
                ),
                true))
            */

            /*
            tuningObjects.add(PbilTunningObject(
                "MOBILITY_MG[KNIGHT]",
                TunableConstants.MOBILITY_MG[Piece.KNIGHT],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_EG[KNIGHT]",
                TunableConstants.MOBILITY_EG[Piece.KNIGHT],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_MG[BISHOP]",
                TunableConstants.MOBILITY_MG[Piece.BISHOP],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_EG[BISHOP]",
                TunableConstants.MOBILITY_EG[Piece.BISHOP],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_MG[ROOK]",
                TunableConstants.MOBILITY_MG[Piece.ROOK],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_EG[ROOK]",
                TunableConstants.MOBILITY_EG[Piece.ROOK],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_MG[QUEEN]",
                TunableConstants.MOBILITY_MG[Piece.QUEEN],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))

            tuningObjects.add(PbilTunningObject(
                "MOBILITY_EG[QUEEN]",
                TunableConstants.MOBILITY_EG[Piece.QUEEN],
                intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7),
                true))
                */

            /*
            tuningObjects.add(PbilTunningObject(
                "OUTPOST_MG[0]",
                TunableConstants.OUTPOST_MG[0],
                intArrayOf(0, 0, 7, 7, 0, 0, 0),
                true, 0, 1, 5, 6))

            tuningObjects.add(PbilTunningObject(
                "OUTPOST_MG[1]",
                TunableConstants.OUTPOST_MG[1],
                intArrayOf(0, 0, 7, 7, 0, 0, 0),
                true, 0, 1, 5, 6))

            tuningObjects.add(PbilTunningObject(
                "OUTPOST_EG[0]",
                TunableConstants.OUTPOST_EG[0],
                intArrayOf(0, 0, 7, 7, 0, 0, 0),
                true, 0, 1, 5, 6))

            tuningObjects.add(PbilTunningObject(
                "OUTPOST_EG[1]",
                TunableConstants.OUTPOST_EG[1],
                intArrayOf(0, 0, 7, 7, 0, 0, 0),
                true, 0, 1, 5, 6))
                */

            tuningObjects.add(PbilTunningObject(
                "PASSED_PAWN_MG",
                TunableConstants.PASSED_PAWN_MG,
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, 0, 7))

            tuningObjects.add(PbilTunningObject(
                "PASSED_PAWN_EG",
                TunableConstants.PASSED_PAWN_EG,
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, 0, 7))

            return tuningObjects
        }

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false

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
