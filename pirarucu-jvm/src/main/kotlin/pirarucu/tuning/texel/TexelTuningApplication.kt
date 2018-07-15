package pirarucu.tuning.texel

import pirarucu.eval.EvalConstants
import pirarucu.tuning.ErrorCalculator
import pirarucu.tuning.TunableConstants
import pirarucu.util.EpdFileLoader
import pirarucu.util.Utils
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object TexelTuningApplication {

    private const val INTERACTIONS = 10000

    private const val numberOfThreads = 6
    private val workers = arrayOfNulls<ErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("G:/chess/epds/quiet_labeled.epd")

    private val tuningObjects: TexelTuningController
        get() {
            val tuningObject = TexelTuningController()

            /*
            tuningObjects.add(PbilTuningController(
                "PHASE",
                TunableConstants.PHASE_PIECE_VALUE,
                intArrayOf(0, 4, 5, 5, 6, 7, 0),
                false, 0, 6))
                */

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "MATERIAL_SCORE_MG",
                TunableConstants.MATERIAL_SCORE_MG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, intArrayOf(0), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "MATERIAL_SCORE_EG",
                TunableConstants.MATERIAL_SCORE_EG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, intArrayOf(0), 1))
                */

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "PSQT_MG[PAWN]",
                TunableConstants.PSQT_MG[Piece.PAWN],
                intArrayOf(0, 0, 0, 0,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    0, 0, 0, 0
                ),
                true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "PSQT_EG[PAWN]",
                TunableConstants.PSQT_EG[Piece.PAWN],
                intArrayOf(0, 0, 0, 0,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    8, 8, 8, 8,
                    0, 0, 0, 0
                ),
                true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 5))
            */

            /*
            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf()))
            */

            /*
            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
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
                true, intArrayOf(), 5))
                */

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_MG[KNIGHT]",
                TunableConstants.MOBILITY_MG[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_EG[KNIGHT]",
                TunableConstants.MOBILITY_EG[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_MG[BISHOP]",
                TunableConstants.MOBILITY_MG[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_EG[BISHOP]",
                TunableConstants.MOBILITY_EG[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_MG[ROOK]",
                TunableConstants.MOBILITY_MG[Piece.ROOK],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_EG[ROOK]",
                TunableConstants.MOBILITY_EG[Piece.ROOK],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_MG[QUEEN]",
                TunableConstants.MOBILITY_MG[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(TexelTuningData(
                "MOBILITY_EG[QUEEN]",
                TunableConstants.MOBILITY_EG[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))
            */

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SUPPORT_MG",
                TunableConstants.PAWN_SUPPORT_MG,
                intArrayOf(0, 0, 6, 6, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SUPPORT_EG",
                TunableConstants.PAWN_SUPPORT_EG,
                intArrayOf(0, 0, 6, 6, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_THREAT_MG",
                TunableConstants.PAWN_THREAT_MG,
                intArrayOf(0, 0, 6, 6, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_THREAT_EG",
                TunableConstants.PAWN_THREAT_EG,
                intArrayOf(0, 0, 6, 6, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_BONUS_MG",
                TunableConstants.PAWN_BONUS_MG,
                intArrayOf(6, 6, 6, 6, 6),
                true, intArrayOf(), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_BONUS_EG",
                TunableConstants.PAWN_BONUS_EG,
                intArrayOf(6, 6, 6, 6, 6),
                true, intArrayOf(), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_PASSED_MG",
                TunableConstants.PAWN_PASSED_MG,
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_PASSED_EG",
                TunableConstants.PAWN_PASSED_EG,
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))
                */

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "PASSED_PAWN_BONUS_MG",
                TunableConstants.PASSED_PAWN_BONUS_MG,
                intArrayOf(6, 6, 6, 6, 6, 6),
                false, intArrayOf(), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PASSED_PAWN_BONUS_EG",
                TunableConstants.PASSED_PAWN_BONUS_EG,
                intArrayOf(6, 6, 6, 6, 6, 6),
                false, intArrayOf(), 1))
                */

            /*
            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[0][0]",
                TunableConstants.PAWN_SHIELD_MG[0][0],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[0][1]",
                TunableConstants.PAWN_SHIELD_MG[0][1],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[0][2]",
                TunableConstants.PAWN_SHIELD_MG[0][2],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[0][3]",
                TunableConstants.PAWN_SHIELD_MG[0][3],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[1][0]",
                TunableConstants.PAWN_SHIELD_MG[1][0],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[1][1]",
                TunableConstants.PAWN_SHIELD_MG[1][1],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[1][2]",
                TunableConstants.PAWN_SHIELD_MG[1][2],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_MG[1][3]",
                TunableConstants.PAWN_SHIELD_MG[1][3],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[0][0]",
                TunableConstants.PAWN_SHIELD_EG[0][0],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[0][1]",
                TunableConstants.PAWN_SHIELD_EG[0][1],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[0][2]",
                TunableConstants.PAWN_SHIELD_EG[0][2],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[0][3]",
                TunableConstants.PAWN_SHIELD_EG[0][3],
                intArrayOf(0, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(0, 7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[1][0]",
                TunableConstants.PAWN_SHIELD_EG[1][0],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[1][1]",
                TunableConstants.PAWN_SHIELD_EG[1][1],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[1][2]",
                TunableConstants.PAWN_SHIELD_EG[1][2],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "PAWN_SHIELD_EG[1][3]",
                TunableConstants.PAWN_SHIELD_EG[1][3],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 0),
                true, intArrayOf(7), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "KING_THREAT_MG",
                TunableConstants.KING_THREAT_MG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "KING_THREAT_EG",
                TunableConstants.KING_THREAT_EG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "SAFE_CHECK_THREAT_MG",
                TunableConstants.SAFE_CHECK_THREAT_MG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 1))

            tuningObject.registerTuningData(TexelTuningData(
                "SAFE_CHECK_THREAT_EG",
                TunableConstants.SAFE_CHECK_THREAT_EG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 1))
                */

            return tuningObject
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
    private fun optimize(tuningController: TexelTuningController) {
        var bestError = executeTest()
        println("Starting error $bestError")
        val startTime = Utils.specific.currentTimeMillis()
        tuningController.initialResult(bestError)

        for (i in 0 until INTERACTIONS) {
            println("Starting interaction $i")
            var skipped = 0
            while (tuningController.hasNext()) {
                if (tuningController.next()) {
                    TunableConstants.update()
                    val error = executeTest()
                    tuningController.reportCurrent(error)
                    if (error < bestError) {
                        bestError = error
                    }
                } else {
                    skipped++
                    println("Skipped")
                }
            }
            val timeTaken = Utils.specific.currentTimeMillis() - startTime
            println("Current time taken $timeTaken millis")
            if (tuningController.finishInteraction()) {
                println("Seems like we are not improving")
                break
            }
        }

        println("Optimization done.")
        tuningController.printBestElements()
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
