package pirarucu.tuning.sts

import pirarucu.benchmark.TestingApplication
import pirarucu.board.Piece
import pirarucu.eval.EvalConstants
import pirarucu.tuning.TunableConstants
import pirarucu.util.EpdFileLoader
import pirarucu.util.Utils
import java.util.concurrent.ExecutionException

object StsTuningApplication {

    private const val INTERACTIONS = 10000
    private val fileLoader = EpdFileLoader("G:/chess/epds/STS/STS.epd")

    private val tuningObjects: StsTuningController
        get() {
            val tuningObject = StsTuningController()

            /*
            tuningObjects.add(PbilTuningController(
                "PHASE",
                TunableConstants.PHASE_PIECE_VALUE,
                intArrayOf(0, 4, 5, 5, 6, 7, 0),
                false, 0, 6))
                */

            /*
            tuningObject.registerTuningData(StsTuningData(
                "RAZOR_MARGIN",
                TunableConstants.RAZOR_MARGIN,
                intArrayOf(0, 10),
                false, intArrayOf(0), 10))
                */

            tuningObject.registerTuningData(StsTuningData(
                "TEMPO_TUNING",
                TunableConstants.TEMPO_TUNING,
                intArrayOf(6, 6),
                false, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MATERIAL_SCORE_MG",
                TunableConstants.MATERIAL_SCORE_MG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, intArrayOf(0), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MATERIAL_SCORE_EG",
                TunableConstants.MATERIAL_SCORE_EG,
                intArrayOf(0, 8, 10, 10, 10, 11),
                false, intArrayOf(0), 5))

            /*
            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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
            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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
            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
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

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_MG[KNIGHT]",
                TunableConstants.MOBILITY_MG[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_EG[KNIGHT]",
                TunableConstants.MOBILITY_EG[Piece.KNIGHT],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_MG[BISHOP]",
                TunableConstants.MOBILITY_MG[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_EG[BISHOP]",
                TunableConstants.MOBILITY_EG[Piece.BISHOP],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_MG[ROOK]",
                TunableConstants.MOBILITY_MG[Piece.ROOK],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_EG[ROOK]",
                TunableConstants.MOBILITY_EG[Piece.ROOK],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_MG[QUEEN]",
                TunableConstants.MOBILITY_MG[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "MOBILITY_EG[QUEEN]",
                TunableConstants.MOBILITY_EG[Piece.QUEEN],
                intArrayOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            /*
            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SUPPORT_MG",
                TunableConstants.PAWN_SUPPORT_MG,
                intArrayOf(0, 0, 8, 8, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SUPPORT_EG",
                TunableConstants.PAWN_SUPPORT_EG,
                intArrayOf(0, 0, 8, 8, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_THREAT_MG",
                TunableConstants.PAWN_THREAT_MG,
                intArrayOf(0, 0, 8, 8, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_THREAT_EG",
                TunableConstants.PAWN_THREAT_EG,
                intArrayOf(0, 0, 8, 8, 0, 0, 0),
                true, intArrayOf(0, 1, 4, 5, 6), 1))
                */

            /*
            tuningObject.registerTuningData(StsTuningData(
                "PAWN_BONUS_MG",
                TunableConstants.PAWN_BONUS_MG,
                intArrayOf(6, 6, 6, 6, 6),
                true, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_BONUS_EG",
                TunableConstants.PAWN_BONUS_EG,
                intArrayOf(6, 6, 6, 6, 6),
                true, intArrayOf(), 5))
                */

            /*
            tuningObject.registerTuningData(StsTuningData(
                "PASSED_PAWN_MG",
                TunableConstants.PASSED_PAWN_MG,
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PASSED_PAWN_EG",
                TunableConstants.PASSED_PAWN_EG,
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))
                */

            /*
            tuningObject.registerTuningData(StsTuningData(
                "PASSED_PAWN_BONUS_MG",
                TunableConstants.PASSED_PAWN_BONUS_MG,
                intArrayOf(8, 8, 8, 8, 8, 8),
                false, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PASSED_PAWN_BONUS_EG",
                TunableConstants.PASSED_PAWN_BONUS_EG,
                intArrayOf(8, 8, 8, 8, 8, 8),
                false, intArrayOf(), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[0][0]",
                TunableConstants.PAWN_SHIELD_MG[0][0],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[0][1]",
                TunableConstants.PAWN_SHIELD_MG[0][1],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[0][2]",
                TunableConstants.PAWN_SHIELD_MG[0][2],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[0][3]",
                TunableConstants.PAWN_SHIELD_MG[0][3],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[1][0]",
                TunableConstants.PAWN_SHIELD_MG[1][0],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[1][1]",
                TunableConstants.PAWN_SHIELD_MG[1][1],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[1][2]",
                TunableConstants.PAWN_SHIELD_MG[1][2],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_MG[1][3]",
                TunableConstants.PAWN_SHIELD_MG[1][3],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[0][0]",
                TunableConstants.PAWN_SHIELD_EG[0][0],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[0][1]",
                TunableConstants.PAWN_SHIELD_EG[0][1],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[0][2]",
                TunableConstants.PAWN_SHIELD_EG[0][2],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[0][3]",
                TunableConstants.PAWN_SHIELD_EG[0][3],
                intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(0, 7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[1][0]",
                TunableConstants.PAWN_SHIELD_EG[1][0],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[1][1]",
                TunableConstants.PAWN_SHIELD_EG[1][1],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[1][2]",
                TunableConstants.PAWN_SHIELD_EG[1][2],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))

            tuningObject.registerTuningData(StsTuningData(
                "PAWN_SHIELD_EG[1][3]",
                TunableConstants.PAWN_SHIELD_EG[1][3],
                intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
                true, intArrayOf(7), 5))
                */

            /*
            tuningObject.registerTuningData(StsTuningData(
                "KING_THREAT_MG",
                TunableConstants.KING_THREAT_MG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 5))

            tuningObject.registerTuningData(StsTuningData(
                "KING_THREAT_EG",
                TunableConstants.KING_THREAT_EG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 5))

            tuningObject.registerTuningData(StsTuningData(
                "SAFE_CHECK_THREAT_MG",
                TunableConstants.SAFE_CHECK_THREAT_MG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 5))

            tuningObject.registerTuningData(StsTuningData(
                "SAFE_CHECK_THREAT_EG",
                TunableConstants.SAFE_CHECK_THREAT_EG,
                intArrayOf(0, 0, 6, 6, 6, 6, 0),
                false, intArrayOf(0, 1, 6), 5))
            */

            return tuningObject
        }

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false

        optimize(tuningObjects)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun optimize(tuningController: StsTuningController) {
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
        val timeControl = intArrayOf(3, 4, 5, 6)
        var prevScore = 0
        var score = 0
        for (time in timeControl) {
            val newScore = TestingApplication.test(fileLoader, time)
            println("Score $newScore")
            if (prevScore > newScore) {
                break
            }
            prevScore = newScore
            score += prevScore
        }
        return (15000.0 - score / timeControl.size) / 15000.0
    }
}
