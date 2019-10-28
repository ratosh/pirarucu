package pirarucu.tuning.texel

import pirarucu.board.Piece
import pirarucu.eval.EvalConstants
import pirarucu.tuning.ErrorUtil
import pirarucu.tuning.QuiescenceEvaluator
import pirarucu.tuning.SearchErrorEvaluator
import pirarucu.tuning.TunableConstants
import pirarucu.util.PlatformSpecific
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException

object TexelTuningApplication {

    private const val THREADS = 4

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

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MATERIAL_SCORE_MG",
                            TunableConstants.MATERIAL_SCORE_MG,
                            intArrayOf(0, 8, 10, 10, 10, 11),
                            false, intArrayOf(0), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MATERIAL_SCORE_EG",
                            TunableConstants.MATERIAL_SCORE_EG,
                            intArrayOf(0, 8, 10, 10, 10, 11),
                            false, intArrayOf(0), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[PAWN]",
                            TunableConstants.PSQT_MG[Piece.PAWN],
                            intArrayOf(
                                    0, 0, 0, 0,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    0, 0, 0, 0
                            ),
                            true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[PAWN]",
                            TunableConstants.PSQT_EG[Piece.PAWN],
                            intArrayOf(
                                    0, 0, 0, 0,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    0, 0, 0, 0
                            ),
                            true, intArrayOf(0, 1, 2, 3, 28, 29, 30, 31), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[KNIGHT]",
                            TunableConstants.PSQT_MG[Piece.KNIGHT],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[KNIGHT]",
                            TunableConstants.PSQT_EG[Piece.KNIGHT],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[BISHOP]",
                            TunableConstants.PSQT_MG[Piece.BISHOP],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[BISHOP]",
                            TunableConstants.PSQT_EG[Piece.BISHOP],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[ROOK]",
                            TunableConstants.PSQT_MG[Piece.ROOK],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[ROOK]",
                            TunableConstants.PSQT_EG[Piece.ROOK],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[QUEEN]",
                            TunableConstants.PSQT_MG[Piece.QUEEN],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[QUEEN]",
                            TunableConstants.PSQT_EG[Piece.QUEEN],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_MG[KING]",
                            TunableConstants.PSQT_MG[Piece.KING],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PSQT_EG[KING]",
                            TunableConstants.PSQT_EG[Piece.KING],
                            intArrayOf(
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10,
                                    10, 10, 10, 10
                            ),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_MG[KNIGHT]",
                            TunableConstants.MOBILITY_MG[Piece.KNIGHT],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_EG[KNIGHT]",
                            TunableConstants.MOBILITY_EG[Piece.KNIGHT],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_MG[BISHOP]",
                            TunableConstants.MOBILITY_MG[Piece.BISHOP],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_EG[BISHOP]",
                            TunableConstants.MOBILITY_EG[Piece.BISHOP],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_MG[ROOK]",
                            TunableConstants.MOBILITY_MG[Piece.ROOK],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_EG[ROOK]",
                            TunableConstants.MOBILITY_EG[Piece.ROOK],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_MG[QUEEN]",
                            TunableConstants.MOBILITY_MG[Piece.QUEEN],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_EG[QUEEN]",
                            TunableConstants.MOBILITY_EG[Piece.QUEEN],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_MG[KING]",
                            TunableConstants.MOBILITY_MG[Piece.KING],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "MOBILITY_EG[KING]",
                            TunableConstants.MOBILITY_EG[Piece.KING],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SUPPORT_MG",
                            TunableConstants.PAWN_SUPPORT_MG,
                            intArrayOf(0, 0, 10, 10, 0, 0, 0),
                            true, intArrayOf(0, 1, 4, 5, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SUPPORT_EG",
                            TunableConstants.PAWN_SUPPORT_EG,
                            intArrayOf(0, 0, 10, 10, 0, 0, 0),
                            true, intArrayOf(0, 1, 4, 5, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_THREAT_MG",
                            TunableConstants.PAWN_THREAT_MG,
                            intArrayOf(0, 0, 10, 10, 0, 0, 0),
                            false, intArrayOf(0, 1, 4, 5, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_THREAT_EG",
                            TunableConstants.PAWN_THREAT_EG,
                            intArrayOf(0, 0, 10, 10, 0, 0, 0),
                            false, intArrayOf(0, 1, 4, 5, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_STRUCTURE_MG",
                            TunableConstants.PAWN_STRUCTURE_MG,
                            intArrayOf(10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_STRUCTURE_EG",
                            TunableConstants.PAWN_STRUCTURE_EG,
                            intArrayOf(10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_MG",
                            TunableConstants.PASSED_PAWN_MG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_EG",
                            TunableConstants.PASSED_PAWN_EG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_BLOCKED_MG",
                            TunableConstants.PASSED_PAWN_BLOCKED_MG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_BLOCKED_EG",
                            TunableConstants.PASSED_PAWN_BLOCKED_EG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_BONUS_MG",
                            TunableConstants.PASSED_PAWN_BONUS_MG,
                            intArrayOf(10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PASSED_PAWN_BONUS_EG",
                            TunableConstants.PASSED_PAWN_BONUS_EG,
                            intArrayOf(10, 10, 10, 10, 10, 10),
                            true, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[0][0]",
                            TunableConstants.PAWN_SHIELD_MG[0][0],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[0][1]",
                            TunableConstants.PAWN_SHIELD_MG[0][1],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[0][2]",
                            TunableConstants.PAWN_SHIELD_MG[0][2],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[0][3]",
                            TunableConstants.PAWN_SHIELD_MG[0][3],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[1][0]",
                            TunableConstants.PAWN_SHIELD_MG[1][0],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[1][1]",
                            TunableConstants.PAWN_SHIELD_MG[1][1],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[1][2]",
                            TunableConstants.PAWN_SHIELD_MG[1][2],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_MG[1][3]",
                            TunableConstants.PAWN_SHIELD_MG[1][3],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[0][0]",
                            TunableConstants.PAWN_SHIELD_EG[0][0],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[0][1]",
                            TunableConstants.PAWN_SHIELD_EG[0][1],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[0][2]",
                            TunableConstants.PAWN_SHIELD_EG[0][2],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[0][3]",
                            TunableConstants.PAWN_SHIELD_EG[0][3],
                            intArrayOf(0, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[1][0]",
                            TunableConstants.PAWN_SHIELD_EG[1][0],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[1][1]",
                            TunableConstants.PAWN_SHIELD_EG[1][1],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[1][2]",
                            TunableConstants.PAWN_SHIELD_EG[1][2],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_SHIELD_EG[1][3]",
                            TunableConstants.PAWN_SHIELD_EG[1][3],
                            intArrayOf(10, 10, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(7), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_PUSH_THREAT_MG",
                            TunableConstants.PAWN_PUSH_THREAT_MG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 10),
                            true, intArrayOf(0, 1), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PAWN_PUSH_THREAT_EG",
                            TunableConstants.PAWN_PUSH_THREAT_EG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 10),
                            true, intArrayOf(0, 1), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "KING_THREAT_MG",
                            TunableConstants.KING_THREAT_MG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 0),
                            false, intArrayOf(0, 1, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "KING_THREAT_EG",
                            TunableConstants.KING_THREAT_EG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 0),
                            false, intArrayOf(0, 1, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "SAFE_CHECK_THREAT_MG",
                            TunableConstants.SAFE_CHECK_THREAT_MG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 0),
                            false, intArrayOf(0, 1, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "SAFE_CHECK_THREAT_EG",
                            TunableConstants.SAFE_CHECK_THREAT_EG,
                            intArrayOf(0, 0, 10, 10, 10, 10, 0),
                            false, intArrayOf(0, 1, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PINNED_BONUS_MG",
                            TunableConstants.PINNED_BONUS_MG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 0),
                            true, intArrayOf(0, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "PINNED_BONUS_EG",
                            TunableConstants.PINNED_BONUS_EG,
                            intArrayOf(0, 10, 10, 10, 10, 10, 0),
                            false, intArrayOf(0, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "OTHER_BONUS_MG",
                            TunableConstants.OTHER_BONUS_MG,
                            intArrayOf(10, 10, 10, 10),
                            false, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "OTHER_BONUS_EG",
                            TunableConstants.OTHER_BONUS_EG,
                            intArrayOf(10, 10, 10, 10),
                            false, intArrayOf(), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_KNIGHT_MG",
                            TunableConstants.THREATEN_BY_KNIGHT_MG,
                            intArrayOf(0, 8, 0, 10, 10, 10, 0),
                            false, intArrayOf(0, 2, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_KNIGHT_EG",
                            TunableConstants.THREATEN_BY_KNIGHT_EG,
                            intArrayOf(0, 8, 0, 10, 10, 10, 0),
                            false, intArrayOf(0, 2, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_BISHOP_MG",
                            TunableConstants.THREATEN_BY_BISHOP_MG,
                            intArrayOf(0, 10, 10, 0, 10, 10, 0),
                            false, intArrayOf(0, 3, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_BISHOP_EG",
                            TunableConstants.THREATEN_BY_BISHOP_EG,
                            intArrayOf(0, 10, 10, 0, 10, 10, 0),
                            false, intArrayOf(0, 3, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_ROOK_MG",
                            TunableConstants.THREATEN_BY_ROOK_MG,
                            intArrayOf(0, 10, 10, 10, 0, 8, 0),
                            false, intArrayOf(0, 4, 6), 16, 4
                    )
            )

            tuningObject.registerTuningData(
                    TexelTuningData(
                            "THREATEN_BY_ROOK_EG",
                            TunableConstants.THREATEN_BY_ROOK_EG,
                            intArrayOf(0, 10, 10, 10, 0, 8, 0),
                            false, intArrayOf(0, 4, 6), 16, 4
                    )
            )

            return tuningObject
        }

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        EvalConstants.PAWN_EVAL_CACHE = false
        optimize(tuningObjects)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun optimize(tuningController: TexelTuningController) {
        val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\texel-sets\\pirarucu.epd")
        val list = mutableListOf<EpdInfo>()
        list.addAll(epdFileLoader.epdList)
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        println("Total entries ${epdList.size}")

        val evaluator = QuiescenceEvaluator(THREADS)
//        val evaluator = SearchErrorEvaluator(THREADS, 8)
        evaluator.evaluate(epdList)
//        val constant = ErrorUtil.ORIGINAL_CONSTANT
        val constant = ErrorUtil.bestConstant(epdList)
        println("Best constant $constant")
//        var bestError = ErrorUtil.getMoveScore(epdList)
        var bestError = ErrorUtil.calculate(epdList)
        println("Starting error $bestError")
        val startTime = PlatformSpecific.currentTimeMillis()
        tuningController.initialResult(bestError)

        while (true) {
            while (tuningController.hasNext()) {
                if (tuningController.next()) {
                    TunableConstants.update()
                    evaluator.evaluate(epdList)
//                    val error = ErrorUtil.getMoveScore(epdList)
                    val error = ErrorUtil.calculate(epdList)
                    tuningController.reportCurrent(error)
                    if (error < bestError) {
                        bestError = error
                    }
                }
            }
            val timeTaken = PlatformSpecific.currentTimeMillis() - startTime
            println("Total time taken $timeTaken millis")
            if (tuningController.finishInteraction()) {
                println("Seems like we are not improving")
                break
            }
        }

        println("Optimization done.")
        tuningController.printBestElements()
    }
}