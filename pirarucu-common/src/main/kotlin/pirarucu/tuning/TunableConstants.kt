package pirarucu.tuning

import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.game.GameConstants
import pirarucu.util.SplitValue
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.roundToInt

object TunableConstants {
    val RAZOR_MARGIN = intArrayOf(0, 400)
    val FUTILITY_CHILD_MARGIN = intArrayOf(0, 100, 160, 230, 310, 400, 500)
    val FUTILITY_PARENT_MARGIN = intArrayOf(0, 100, 200, 310, 430, 550, 660)

    val FUTILITY_HISTORY_MARGIN = intArrayOf(0, -12500, -13000, -15500, -17000, -18000, -19000)

    val TEMPO_TUNING = intArrayOf(23, 13)
    val TEMPO = IntArray(Color.SIZE)

    val LMR_TABLE = Array(64) { IntArray(64) }

    val PHASE_PIECE_VALUE = intArrayOf(0, 0, 9, 10, 20, 40, 0)

    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val MATERIAL_SCORE_MG = intArrayOf(0, 87, 476, 508, 682, 1446)
    val MATERIAL_SCORE_EG = intArrayOf(0, 116, 327, 352, 606, 1082)
    val MATERIAL_SCORE = IntArray(Piece.SIZE)

    val QS_FUTILITY_VALUE = IntArray(Piece.SIZE)

    val SEE_VALUE = intArrayOf(0, 100, 325, 330, 550, 900, 0)

    val PSQT_MG = arrayOf(
        intArrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        intArrayOf(
                0, 0, 0, 0, -12, 36, 33, 74, 36, 57, 73, 92, 2, 16, 22, 31, -10, -10, 20, 25, -9, -8, 2, 6, -4, 6, 1,
                -15, 0, 0, 0, 0
        ),
        intArrayOf(
                -187, -100, -129, -23, -94, -51, 37, 13, -22, 38, 19, 43, 4, 14, 10, 28, -4, 14, 22, 25, -18, -8, 14,
                26, -14, -16, 4, 19, -50, -9, -22, -9
        ),
        intArrayOf(
                -51, -107, -102, -93, -78, -43, -16, -55, 1, 12, 21, 27, -12, 1, 7, 24, -2, 3, 3, 23, -2, 9, 17, 8, -6,
                28, 10, 8, -17, -6, -2, 5
        ),
        intArrayOf(
                -3, 11, 6, 36, -11, -10, 25, 16, -16, 19, 0, 6, -27, 5, 8, -7, -29, -6, -6, -6, -16, -12, -6, -11, -26,
                5, 4, 2, -8, 2, 10, 20
        ),
        intArrayOf(
                7, -16, -4, 37, -5, -35, 3, -37, 25, 12, 12, 0, -2, -8, 0, -9, 16, -2, 12, 6, 18, 31, 23, 29, 18, 28,
                44, 45, 27, 37, 45, 53
        ),
        intArrayOf(
                -23, 92, 39, 60, 24, 45, 25, 86, 35, 108, 126, 88, -18, -5, -6, -32, -89, -66, -64, -29, -55, -24, -68,
                -51, 18, 4, -50, -28, 34, 29, -5, 21
        )
    )

    val PSQT_EG = arrayOf(
        intArrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        intArrayOf(
                0, 0, 0, 0, 26, 12, -14, -37, 26, -4, -11, -43, 7, -1, -11, -23, -8, -8, -25, -29, -15, -18, -17, -19,
                -13, -18, -12, -16, 0, 0, 0, 0
        ),
        intArrayOf(
                -11, -31, -2, -35, -25, -8, -28, 8, -10, 8, 8, 0, -19, 5, 22, 29, -17, -7, 12, 22, -15, -22, -6, 10,
                -34, -20, -15, -3, -30, -35, -17, -13
        ),
        intArrayOf(
                -17, -5, 22, 25, 9, -1, -1, -12, -1, -14, -10, -15, -1, -4, 0, 2, -13, -8, 0, -5, -6, -9, -6, 0, -11,
                -20, -9, -9, 3, -3, -10, -14
        ),
        intArrayOf(
                26, 29, 33, 23, 15, 24, 20, 12, 13, 13, 14, 13, 15, 7, 9, 12, 5, 3, 5, 6, -9, -10, -6, 0, -14, -18, -12,
                -13, -14, -11, -11, -19
        ),
        intArrayOf(
                4, 13, 19, 10, -13, 23, 15, 47, -20, -13, -11, 37, 6, 27, 17, 41, -14, 25, 5, 30, -1, -32, 0, -16, -32,
                -57, -48, -40, -55, -70, -63, -44
        ),
        intArrayOf(
                -56, -27, -18, -16, -5, 35, 36, 18, 0, 8, 41, 48, 20, 27, 53, 49, 1, 19, 49, 39, -2, 12, 27, 25, -19,
                10, 24, 17, -65, -33, -18, -27
        )
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-52, -30, -15, -11, -1, 6, 13, 18, 25),
        intArrayOf(-34, -22, -5, 0, 4, 9, 14, 16, 17, 22, 41, 62, -34, 82),
        intArrayOf(-68, -46, -38, -31, -29, -19, -16, -11, -5, -2, 3, 6, 12, 11, 12),
        intArrayOf(
                -7, -9, 5, -5, -2, 6, 7, 8, 9, 12, 15, 16, 15, 18, 25, 23, 31, 46, 23, 54, 61, 119, 102, 155, 158, 296,
                127, 227
        ),
        intArrayOf(-114, -80, -23, 3, 13, 3, 17, 10, 18)
    )

    val MOBILITY_EG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-2, -8, 9, 18, 19, 27, 21, 18, 10),
        intArrayOf(-24, -42, -20, -9, 3, 7, 12, 15, 18, 19, 11, 12, 52, 4),
        intArrayOf(0, -16, 9, 18, 21, 26, 34, 39, 40, 43, 43, 48, 52, 52, 30),
        intArrayOf(
                155, 65, 18, -32, -49, -44, -20, -6, 7, 8, 17, 27, 32, 16, 34, 16, 33, 8, 34, 11, 10, -37, -38, -62,
                -50, -153, -38, -108
        ),
        intArrayOf(0, 36, 22, 19, 16, 5, -1, 0, -24)
    )
    val MOBILITY = Array(Piece.SIZE) { IntArray(28) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 4, 0, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 17, 18, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 75, 74, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 18, 46, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_STRUCTURE_DEFENDED = 0
    const val PAWN_STRUCTURE_PHALANX = 1
    const val PAWN_STRUCTURE_ISOLATED = 2
    const val PAWN_STRUCTURE_STACKED = 3
    const val PAWN_STRUCTURE_BACKWARD = 4
    const val PAWN_STRUCTURE_BACKWARD_HALF_OPEN = 5

    val PAWN_STRUCTURE_MG = intArrayOf(25, 10, -10, -4, -3, -22)
    val PAWN_STRUCTURE_EG = intArrayOf(6, 1, -10, -12, -10, -16)
    val PAWN_STRUCTURE = IntArray(PAWN_STRUCTURE_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, -7, -8, -18, 3, 7, 104, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -47, -39, -2, 34, 126, 230, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    val PASSED_PAWN_BLOCKED_MG = intArrayOf(0, -35, -14, -25, -8, 10, 104, 0)
    val PASSED_PAWN_BLOCKED_EG = intArrayOf(0, -60, -40, -15, 7, 34, 68, 0)
    val PASSED_PAWN_BLOCKED = IntArray(Rank.SIZE)

    const val PASSED_PAWN_SAFE = 0
    const val PASSED_PAWN_SAFE_ADVANCE = 1
    const val PASSED_PAWN_SAFE_PATH = 2
    const val PASSED_PAWN_DEFENDED = 3
    const val PASSED_PAWN_DEFENDED_ADVANCE = 4
    const val PASSED_PAWN_KING_DISTANCE = 5

    val PASSED_PAWN_BONUS_MG = intArrayOf(-8, -2, -17, 32, 9, -5)
    val PASSED_PAWN_BONUS_EG = intArrayOf(9, 34, 25, -3, 12, 12)
    val PASSED_PAWN_BONUS = IntArray(PASSED_PAWN_BONUS_EG.size)

    val PAWN_SHIELD_MG = arrayOf(
        arrayOf(
            intArrayOf(0, 51, 64, 32, 17, 37, -47, 0),
            intArrayOf(0, 66, 67, 40, 22, 17, -193, 0),
            intArrayOf(0, 39, 30, 17, 22, -33, -7, 0),
            intArrayOf(0, 46, 22, 16, 21, -81, 82, 0)
        ),
        arrayOf(
            intArrayOf(8, 24, 28, 15, 15, 9, -73, 0),
            intArrayOf(78, 54, 30, 33, 19, 22, -144, 0),
            intArrayOf(59, 44, 5, 10, 15, 31, 22, 0),
            intArrayOf(57, 46, 8, 21, 17, -47, -92, 0)
        )
    )
    val PAWN_SHIELD_EG = arrayOf(
        arrayOf(
            intArrayOf(0, -37, -31, -8, -1, -3, 78, 0),
            intArrayOf(0, -21, -14, -4, -1, 17, 9, 0),
            intArrayOf(0, -1, 3, 4, -10, 13, 51, 0),
            intArrayOf(0, 1, 9, 2, -18, 21, 9, 0)
        ),
        arrayOf(
            intArrayOf(-2, -11, -6, -4, -6, -20, 3, 0),
            intArrayOf(-20, -18, -4, -8, -2, 9, -15, 0),
            intArrayOf(-11, -9, 10, -1, -11, -15, -33, 0),
            intArrayOf(-6, -8, 4, -4, -22, 3, 7, 0)
        )
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val PAWN_PUSH_THREAT_MG = intArrayOf(0, 0, 17, 25, 23, 7, 170)
    val PAWN_PUSH_THREAT_EG = intArrayOf(0, 0, 8, 15, 10, 27, -1)
    val PAWN_PUSH_THREAT = IntArray(PAWN_PUSH_THREAT_EG.size)

    val KING_THREAT_MG = intArrayOf(0, 0, 12, 13, 11, 14, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, -2, 2, -2, 11, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 117, 14, 87, 30, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 5, 33, 1, 99, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    val PINNED_BONUS_MG = intArrayOf(0, -14, -14, -8, -19, -8, 0)
    val PINNED_BONUS_EG = intArrayOf(0, 8, -18, -11, -13, -18, 0)
    val PINNED_BONUS = IntArray(Piece.SIZE)

    const val OTHER_BONUS_BISHOP_PAIR = 0
    const val OTHER_BONUS_ROOK_ON_SEVENTH = 1
    const val OTHER_BONUS_ROOK_OPEN_FILE = 2
    const val OTHER_BONUS_ROOK_HALF_OPEN_FILE = 3

    val OTHER_BONUS_MG = intArrayOf(25, 0, 33, 11)
    val OTHER_BONUS_EG = intArrayOf(55, 14, 2, 4)
    val OTHER_BONUS = IntArray(OTHER_BONUS_EG.size)

    val THREATEN_BY_KNIGHT_MG = intArrayOf(0, 5, 0, 35, 64, 31, 0)
    val THREATEN_BY_KNIGHT_EG = intArrayOf(0, 10, 0, 21, 0, 0, 0)
    val THREATEN_BY_KNIGHT = IntArray(Piece.SIZE)

    val THREATEN_BY_BISHOP_MG = intArrayOf(0, 10, 21, 0, 46, 56, 0)
    val THREATEN_BY_BISHOP_EG = intArrayOf(0, 18, 31, 0, 7, 22, 0)
    val THREATEN_BY_BISHOP = IntArray(Piece.SIZE)

    val THREATEN_BY_ROOK_MG = intArrayOf(0, 0, 19, 0, 0, 84, 0)
    val THREATEN_BY_ROOK_EG = intArrayOf(0, 21, 17, 30, 0, 0, 0)
    val THREATEN_BY_ROOK = IntArray(Piece.SIZE)

    init {
        // Ethereal LMR formula with depth and number of performed moves
        for (depth in 1 until LMR_TABLE.size) {
            for (moveNumber in 1 until LMR_TABLE[depth].size) {
                LMR_TABLE[depth][moveNumber] =
                    (0.5 + ln(depth.toDouble()) * ln(moveNumber.toDouble() * 1.2) / 2.5).roundToInt()
            }
        }

        update()
    }

    fun update() {
        PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
            PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
            PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
            PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
            PHASE_PIECE_VALUE[Piece.QUEEN] * 2

        for (piece in Piece.PAWN until Piece.KING) {
            MATERIAL_SCORE[piece] = SplitValue.mergeParts(MATERIAL_SCORE_MG[piece], MATERIAL_SCORE_EG[piece])
        }

        for (index in QS_FUTILITY_VALUE.indices) {
            QS_FUTILITY_VALUE[index] = max(
                SplitValue.getFirstPart(MATERIAL_SCORE[index]),
                SplitValue.getSecondPart(MATERIAL_SCORE[index])
            )
        }

        for (piece in Piece.PAWN until Piece.SIZE) {
            var psqPosition = 0
            for (rank in Rank.RANK_1 until Rank.SIZE) {
                for (file in File.FILE_A until File.SIZE / 2) {
                    val square = Square.getSquare(file, Rank.invertRank(rank))
                    val psqtValue = SplitValue.mergeParts(PSQT_MG[piece][psqPosition], PSQT_EG[piece][psqPosition])
                    PSQT[piece][square] = psqtValue
                    PSQT[piece][Square.flipHorizontal(square)] = psqtValue
                    psqPosition++
                }
            }
        }

        for (piece in 0 until MOBILITY.size) {
            for (index in 0 until MOBILITY_MG[piece].size) {
                MOBILITY[piece][index] = SplitValue.mergeParts(MOBILITY_MG[piece][index], MOBILITY_EG[piece][index])
            }
        }

        for (index in 0 until TEMPO.size) {
            TEMPO[index] = SplitValue.mergeParts(TEMPO_TUNING[0], TEMPO_TUNING[1]) * GameConstants.COLOR_FACTOR[index]
        }

        for (piece in 0 until PAWN_SUPPORT.size) {
            PAWN_SUPPORT[piece] = SplitValue.mergeParts(PAWN_SUPPORT_MG[piece], PAWN_SUPPORT_EG[piece])
        }

        for (piece in 0 until PAWN_THREAT.size) {
            PAWN_THREAT[piece] = SplitValue.mergeParts(PAWN_THREAT_MG[piece], PAWN_THREAT_EG[piece])
        }

        for (index in 0 until PAWN_STRUCTURE.size) {
            PAWN_STRUCTURE[index] = SplitValue.mergeParts(PAWN_STRUCTURE_MG[index], PAWN_STRUCTURE_EG[index])
        }

        for (index in 0 until PASSED_PAWN.size) {
            PASSED_PAWN[index] = SplitValue.mergeParts(PASSED_PAWN_MG[index], PASSED_PAWN_EG[index])
        }

        for (index in 0 until PAWN_SHIELD.size) {
            for (index2 in 0 until PAWN_SHIELD[index].size) {
                for (index3 in 0 until PAWN_SHIELD[index][index2].size) {
                    PAWN_SHIELD[index][index2][index3] = SplitValue.mergeParts(
                        PAWN_SHIELD_MG[index][index2][index3],
                        PAWN_SHIELD_EG[index][index2][index3]
                    )
                }
            }
        }

        for (index in 0 until PAWN_PUSH_THREAT.size) {
            PAWN_PUSH_THREAT[index] = SplitValue.mergeParts(PAWN_PUSH_THREAT_MG[index], PAWN_PUSH_THREAT_EG[index])
        }

        for (index in 0 until KING_THREAT.size) {
            KING_THREAT[index] = SplitValue.mergeParts(KING_THREAT_MG[index], KING_THREAT_EG[index])
        }

        for (index in 0 until SAFE_CHECK_THREAT.size) {
            SAFE_CHECK_THREAT[index] = SplitValue.mergeParts(SAFE_CHECK_THREAT_MG[index], SAFE_CHECK_THREAT_EG[index])
        }

        for (index in 0 until PINNED_BONUS.size) {
            PINNED_BONUS[index] = SplitValue.mergeParts(PINNED_BONUS_MG[index], PINNED_BONUS_EG[index])
        }

        for (index in 0 until PASSED_PAWN_BONUS.size) {
            PASSED_PAWN_BONUS[index] = SplitValue.mergeParts(PASSED_PAWN_BONUS_MG[index], PASSED_PAWN_BONUS_EG[index])
        }

        for (index in 0 until PASSED_PAWN_BLOCKED.size) {
            PASSED_PAWN_BLOCKED[index] =
                SplitValue.mergeParts(PASSED_PAWN_BLOCKED_MG[index], PASSED_PAWN_BLOCKED_EG[index])
        }

        for (index in 0 until OTHER_BONUS.size) {
            OTHER_BONUS[index] = SplitValue.mergeParts(OTHER_BONUS_MG[index], OTHER_BONUS_EG[index])
        }

        for (piece in 0 until THREATEN_BY_KNIGHT.size) {
            THREATEN_BY_KNIGHT[piece] = SplitValue.mergeParts(
                THREATEN_BY_KNIGHT_MG[piece],
                THREATEN_BY_KNIGHT_EG[piece]
            )
        }

        for (piece in 0 until THREATEN_BY_BISHOP.size) {
            THREATEN_BY_BISHOP[piece] = SplitValue.mergeParts(
                THREATEN_BY_BISHOP_MG[piece],
                THREATEN_BY_BISHOP_EG[piece]
            )
        }

        for (piece in 0 until THREATEN_BY_ROOK.size) {
            THREATEN_BY_ROOK[piece] = SplitValue.mergeParts(
                THREATEN_BY_ROOK_MG[piece],
                THREATEN_BY_ROOK_EG[piece]
            )
        }
    }
}

