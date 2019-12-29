package pirarucu.tuning.texel

import pirarucu.board.Piece
import pirarucu.eval.EvalConstants
import pirarucu.tuning.TunableConstants
import pirarucu.tuning.calculator.MoveScoreCalculator
import pirarucu.tuning.calculator.NodeCountCalculator
import pirarucu.tuning.calculator.ResultErrorCalculator
import pirarucu.tuning.evaluator.QuiescenceEvaluator
import pirarucu.tuning.evaluator.MainSearchEvaluator
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException

object TexelTuningApplication {

    private const val THREADS = 5

    private val tuningObjects: TexelTuningController
        get() {
            val tuningObject = TexelTuningController()

//            tuningObjects.add(TexelTuningData(
//                    "PHASE",
//                    TunableConstants.PHASE_PIECE_VALUE,
//                    intArrayOf(0, 4, 5, 5, 6, 7, 0),
//                    true, 0, 6))

//            tuningObject.registerTuningData(TexelTuningData(
//                    "FUTILITY_CHILD_MARGIN",
//                    TunableConstants.FUTILITY_CHILD_MARGIN,
//                    intArrayOf(0, 8, 8, 9, 9, 9, 10),
//                    true, intArrayOf(0), 32, 1
//            ))

//            tuningObject.registerTuningData(TexelTuningData(
//                    "FUTILITY_PARENT_MARGIN",
//                    TunableConstants.FUTILITY_PARENT_MARGIN,
//                    intArrayOf(0, 8, 8, 9, 9, 10, 10),
//                    true, intArrayOf(0), 32, 1
//            ))

//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MATERIAL_SCORE_MG",
//                            TunableConstants.MATERIAL_SCORE_MG,
//                            intArrayOf(0, 8, 9, 9, 10, 11),
//                            true, intArrayOf(0), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MATERIAL_SCORE_EG",
//                            TunableConstants.MATERIAL_SCORE_EG,
//                            intArrayOf(0, 8, 9, 9, 10, 11),
//                            true, intArrayOf(0), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[PAWN]",
//                            TunableConstants.PSQT_MG[Piece.PAWN],
//                            intArrayOf(
//                                    0, 0, 0, 0,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    0, 0, 0, 0
//                            ),
//                            true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[PAWN]",
//                            TunableConstants.PSQT_EG[Piece.PAWN],
//                            intArrayOf(
//                                    0, 0, 0, 0,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    0, 0, 0, 0
//                            ),
//                            true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[KNIGHT]",
//                            TunableConstants.PSQT_MG[Piece.KNIGHT],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[KNIGHT]",
//                            TunableConstants.PSQT_EG[Piece.KNIGHT],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[BISHOP]",
//                            TunableConstants.PSQT_MG[Piece.BISHOP],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[BISHOP]",
//                            TunableConstants.PSQT_EG[Piece.BISHOP],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[ROOK]",
//                            TunableConstants.PSQT_MG[Piece.ROOK],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[ROOK]",
//                            TunableConstants.PSQT_EG[Piece.ROOK],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[QUEEN]",
//                            TunableConstants.PSQT_MG[Piece.QUEEN],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[QUEEN]",
//                            TunableConstants.PSQT_EG[Piece.QUEEN],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_MG[KING]",
//                            TunableConstants.PSQT_MG[Piece.KING],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PSQT_EG[KING]",
//                            TunableConstants.PSQT_EG[Piece.KING],
//                            intArrayOf(
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8,
//                                    8, 8, 8, 8
//                            ),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_MG[KNIGHT]",
//                            TunableConstants.MOBILITY_MG[Piece.KNIGHT],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_EG[KNIGHT]",
//                            TunableConstants.MOBILITY_EG[Piece.KNIGHT],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_MG[BISHOP]",
//                            TunableConstants.MOBILITY_MG[Piece.BISHOP],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_EG[BISHOP]",
//                            TunableConstants.MOBILITY_EG[Piece.BISHOP],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_MG[ROOK]",
//                            TunableConstants.MOBILITY_MG[Piece.ROOK],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_EG[ROOK]",
//                            TunableConstants.MOBILITY_EG[Piece.ROOK],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_MG[QUEEN]",
//                            TunableConstants.MOBILITY_MG[Piece.QUEEN],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_EG[QUEEN]",
//                            TunableConstants.MOBILITY_EG[Piece.QUEEN],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_MG[KING]",
//                            TunableConstants.MOBILITY_MG[Piece.KING],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "MOBILITY_EG[KING]",
//                            TunableConstants.MOBILITY_EG[Piece.KING],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SUPPORT_MG",
//                            TunableConstants.PAWN_SUPPORT_MG,
//                            intArrayOf(0, 0, 8, 8, 0, 0, 0),
//                            true, intArrayOf(0, 1, 4, 5, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SUPPORT_EG",
//                            TunableConstants.PAWN_SUPPORT_EG,
//                            intArrayOf(0, 0, 8, 8, 0, 0, 0),
//                            true, intArrayOf(0, 1, 4, 5, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_THREAT_MG",
//                            TunableConstants.PAWN_THREAT_MG,
//                            intArrayOf(0, 0, 8, 8, 0, 0, 0),
//                            true, intArrayOf(0, 1, 4, 5, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_THREAT_EG",
//                            TunableConstants.PAWN_THREAT_EG,
//                            intArrayOf(0, 0, 8, 8, 0, 0, 0),
//                            true, intArrayOf(0, 1, 4, 5, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_STRUCTURE_MG",
//                            TunableConstants.PAWN_STRUCTURE_MG,
//                            intArrayOf(8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_STRUCTURE_EG",
//                            TunableConstants.PAWN_STRUCTURE_EG,
//                            intArrayOf(8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_MG",
//                            TunableConstants.PASSED_PAWN_MG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_EG",
//                            TunableConstants.PASSED_PAWN_EG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_BLOCKED_MG",
//                            TunableConstants.PASSED_PAWN_BLOCKED_MG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_BLOCKED_EG",
//                            TunableConstants.PASSED_PAWN_BLOCKED_EG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_BONUS_MG",
//                            TunableConstants.PASSED_PAWN_BONUS_MG,
//                            intArrayOf(8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PASSED_PAWN_BONUS_EG",
//                            TunableConstants.PASSED_PAWN_BONUS_EG,
//                            intArrayOf(8, 8, 8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[0][0]",
//                            TunableConstants.PAWN_SHIELD_MG[0][0],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[0][1]",
//                            TunableConstants.PAWN_SHIELD_MG[0][1],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[0][2]",
//                            TunableConstants.PAWN_SHIELD_MG[0][2],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[0][3]",
//                            TunableConstants.PAWN_SHIELD_MG[0][3],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[1][0]",
//                            TunableConstants.PAWN_SHIELD_MG[1][0],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[1][1]",
//                            TunableConstants.PAWN_SHIELD_MG[1][1],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[1][2]",
//                            TunableConstants.PAWN_SHIELD_MG[1][2],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_MG[1][3]",
//                            TunableConstants.PAWN_SHIELD_MG[1][3],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[0][0]",
//                            TunableConstants.PAWN_SHIELD_EG[0][0],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[0][1]",
//                            TunableConstants.PAWN_SHIELD_EG[0][1],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[0][2]",
//                            TunableConstants.PAWN_SHIELD_EG[0][2],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[0][3]",
//                            TunableConstants.PAWN_SHIELD_EG[0][3],
//                            intArrayOf(0, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[1][0]",
//                            TunableConstants.PAWN_SHIELD_EG[1][0],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[1][1]",
//                            TunableConstants.PAWN_SHIELD_EG[1][1],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[1][2]",
//                            TunableConstants.PAWN_SHIELD_EG[1][2],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_SHIELD_EG[1][3]",
//                            TunableConstants.PAWN_SHIELD_EG[1][3],
//                            intArrayOf(8, 8, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(7), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_PUSH_THREAT_MG",
//                            TunableConstants.PAWN_PUSH_THREAT_MG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 8),
//                            true, intArrayOf(0, 1), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PAWN_PUSH_THREAT_EG",
//                            TunableConstants.PAWN_PUSH_THREAT_EG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 8),
//                            true, intArrayOf(0, 1), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "KING_THREAT_MG",
//                            TunableConstants.KING_THREAT_MG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 1, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "KING_THREAT_EG",
//                            TunableConstants.KING_THREAT_EG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 1, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "SAFE_CHECK_THREAT_MG",
//                            TunableConstants.SAFE_CHECK_THREAT_MG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 1, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "SAFE_CHECK_THREAT_EG",
//                            TunableConstants.SAFE_CHECK_THREAT_EG,
//                            intArrayOf(0, 0, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 1, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PINNED_BONUS_MG",
//                            TunableConstants.PINNED_BONUS_MG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "PINNED_BONUS_EG",
//                            TunableConstants.PINNED_BONUS_EG,
//                            intArrayOf(0, 8, 8, 8, 8, 8, 0),
//                            true, intArrayOf(0, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "OTHER_BONUS_MG",
//                            TunableConstants.OTHER_BONUS_MG,
//                            intArrayOf(8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "OTHER_BONUS_EG",
//                            TunableConstants.OTHER_BONUS_EG,
//                            intArrayOf(8, 8, 8, 8),
//                            true, intArrayOf(), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "THREATEN_BY_KNIGHT_MG",
//                            TunableConstants.THREATEN_BY_KNIGHT_MG,
//                            intArrayOf(0, 8, 0, 8, 8, 8, 0),
//                            true, intArrayOf(0, 2, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "THREATEN_BY_KNIGHT_EG",
//                            TunableConstants.THREATEN_BY_KNIGHT_EG,
//                            intArrayOf(0, 8, 0, 8, 8, 8, 0),
//                            true, intArrayOf(0, 2, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "THREATEN_BY_BISHOP_MG",
//                            TunableConstants.THREATEN_BY_BISHOP_MG,
//                            intArrayOf(0, 8, 8, 0, 8, 8, 0),
//                            true, intArrayOf(0, 3, 6), 32, 1
//                    )
//            )
//
//            tuningObject.registerTuningData(
//                    TexelTuningData(
//                            "THREATEN_BY_BISHOP_EG",
//                            TunableConstants.THREATEN_BY_BISHOP_EG,
//                            intArrayOf(0, 8, 8, 0, 8, 8, 0),
//                            true, intArrayOf(0, 3, 6), 32, 1
//                    )
//            )
//
            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_ROOK_MG",
                            TunableConstants.THREATEN_BY_ROOK_MG,
                            intArrayOf(0, 8, 8, 8, 0, 8, 0),
                            true, intArrayOf(0, 4, 6), 32, 1
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_ROOK_EG",
                            TunableConstants.THREATEN_BY_ROOK_EG,
                            intArrayOf(0, 8, 8, 8, 0, 8, 0),
                            true, intArrayOf(0, 4, 6), 32, 1
                    )
            )

            return tuningObject
        }

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false
        val epdList = loadEpdFiles(listOf("g:\\chess\\epds\\texel-sets\\pirarucu.epd"))
//        nodeCountOptimize(tuningObjects, epdList)
//        moveScoreOptimize(tuningObjects, epdList)
        qsearchOptimize(tuningObjects, epdList)
//        searchOptimize(tuningObjects, epdList)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun nodeCountOptimize(tuningController: TexelTuningController, epdList: List<EpdInfo>) {
        val evaluator = MainSearchEvaluator(THREADS, 12)
        BasicTuner.optimize(evaluator, NodeCountCalculator(), tuningController, epdList)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun moveScoreOptimize(tuningController: TexelTuningController, epdList: List<EpdInfo>) {
        val evaluator = MainSearchEvaluator(THREADS, 12)
        BasicTuner.optimize(evaluator, MoveScoreCalculator(), tuningController, epdList)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun qsearchOptimize(tuningController: TexelTuningController, epdList: List<EpdInfo>) {
        val evaluator = QuiescenceEvaluator(THREADS)
        BasicTuner.optimize(evaluator, ResultErrorCalculator(), tuningController, epdList)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun searchOptimize(tuningController: TexelTuningController, epdList: List<EpdInfo>) {
        val evaluator = MainSearchEvaluator(THREADS, 2)
        BasicTuner.optimize(evaluator, ResultErrorCalculator(), tuningController, epdList)
    }

    private fun loadEpdFiles(fileList: List<String>): List<EpdInfo> {
        val list = mutableListOf<EpdInfo>()
        for (entry in fileList) {
            val epdLoader = EpdFileLoader(entry)
            list.addAll(epdLoader.epdList)
        }
        return InvalidPositionFilter(THREADS).filter(list)
    }
}