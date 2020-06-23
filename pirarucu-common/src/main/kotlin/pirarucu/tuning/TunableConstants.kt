package pirarucu.tuning

import pirarucu.board.*
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

    val MATERIAL_SCORE_MG = intArrayOf(0, 87, 444, 452, 616, 1206)
    val MATERIAL_SCORE_EG = intArrayOf(0, 134, 407, 444, 765, 1498)
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
            intArrayOf(0, 0, 0, 0, -38, 0, 67, 99, 22, 25, 67, 80, 2, 8, 14, 25, -4, -10, 10, 17, -13, -12, 0, 4, -6, -12, -1, 1, 0, 0, 0, 0),
            intArrayOf(-183, -118, -102, -76, -78, -46, -12, -53, -25, -11, 0, 26, 7, 14, 37, 24, -4, 19, 25, 25, -28, 2, 8, 18, -12, -15, -7, 7, -54, -25, -8, -8),
            intArrayOf(-51, -71, -52, -120, -59, -48, -30, -41, 7, 8, 14, 1, -24, 1, 8, 12, 2, -3, 7, 19, -8, 9, 9, 6, 10, 20, 20, 8, 2, 20, -4, 4),
            intArrayOf(-3, -28, -9, -27, -7, -18, -3, -2, -9, 10, 4, 2, -9, 3, 7, 1, -9, -2, -10, -4, -8, 6, 0, -1, -14, 1, 6, 6, -6, 4, 10, 16),
            intArrayOf(7, 10, 18, 7, 21, -11, -10, -17, 33, 15, 6, 4, 24, 20, 20, 9, 26, 24, 30, 30, 36, 39, 37, 31, 55, 46, 46, 49, 41, 43, 41, 47),
            intArrayOf(-23, 209, 6, 22, 24, -2, 37, -46, -51, -2, -16, -61, -79, -59, -83, -107, -133, -95, -78, -80, -56, -40, -76, -54, 32, -4, -34, -22, 64, 29, 13, 31)
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
            intArrayOf(0, 0, 0, 0, 54, 44, 9, -31, 32, 28, -5, -49, 9, 5, -9, -23, -10, -8, -19, -25, -11, -14, -19, -17, -11, -14, -12, -14, 0, 0, 0, 0),
            intArrayOf(-77, -7, -7, 13, -5, 11, 13, 22, 2, 14, 33, 29, 11, 26, 32, 45, 13, 18, 28, 40, -4, -3, 2, 23, 11, 2, 4, 6, -10, -13, -3, 6),
            intArrayOf(-17, 9, 6, 18, 4, 0, 0, 8, 3, 0, 2, -3, 19, 10, 5, 12, 0, 0, 8, 7, -6, 1, 0, 10, -13, -12, -16, -3, -17, -16, -10, -4),
            intArrayOf(26, 37, 42, 43, 7, 26, 24, 15, 13, 16, 22, 9, 11, 13, 14, 10, -1, -1, 11, 6, -16, -20, -8, -8, -20, -20, -14, -15, -12, -15, -9, -17),
            intArrayOf(4, 5, 17, 26, -27, 41, 55, 57, -30, 10, 53, 47, 4, 21, 27, 50, 8, 17, 15, 28, -42, -18, 10, 4, -82, -56, -32, -24, -78, -58, -43, -40),
            intArrayOf(-56, -87, -22, 6, -7, 75, 77, 77, 16, 72, 71, 80, 11, 55, 67, 73, 9, 43, 47, 49, 0, 24, 33, 33, -19, 14, 24, 19, -81, -43, -30, -41)
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-52, -30, -15, -7, -3, 2, 9, 12, 14),
            intArrayOf(-34, -16, -7, 0, 6, 9, 12, 14, 15, 22, 27, 31, 54, 49),
            intArrayOf(-68, -44, -37, -31, -33, -25, -22, -19, -15, -14, -9, -10, -10, -7, -12),
            intArrayOf(-7, -8, -13, -4, -3, 4, 5, 8, 9, 12, 14, 16, 17, 18, 21, 21, 29, 30, 33, 36, 38, 75, 57, 57, 130, 180, 256, 252),
            intArrayOf(-114, -62, -23, -1, 15, 3, 27, 26, 24)
    )

    val MOBILITY_EG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-2, -12, 7, 16, 27, 37, 35, 32, 21),
            intArrayOf(-24, -42, -20, -3, 7, 19, 24, 29, 36, 34, 30, 35, 35, 33),
            intArrayOf(0, 12, 14, 16, 27, 30, 34, 35, 38, 45, 45, 50, 56, 48, 50),
            intArrayOf(155, 7, -7, -44, -28, -46, -18, -16, -3, 0, 3, 11, 14, 16, 12, 18, 9, 10, 6, 1, -10, -13, -21, -33, -60, -119, -98, -47),
            intArrayOf(0, 32, 30, 29, 18, 9, -1, -2, -26)
    )
    val MOBILITY = Array(Piece.SIZE) { IntArray(28) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 6, 10, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 17, 12, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 73, 73, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 8, 44, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_STRUCTURE_DEFENDED = 0
    const val PAWN_STRUCTURE_PHALANX = 1
    const val PAWN_STRUCTURE_ISOLATED = 2
    const val PAWN_STRUCTURE_STACKED = 3
    const val PAWN_STRUCTURE_BACKWARD = 4
    const val PAWN_STRUCTURE_BACKWARD_HALF_OPEN = 5

    val PAWN_STRUCTURE_MG = intArrayOf(25, 10, -6, -12, 1, -28)
    val PAWN_STRUCTURE_EG = intArrayOf(6, 1, -14, -14, -14, -16)
    val PAWN_STRUCTURE = IntArray(PAWN_STRUCTURE_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, -25, -38, -34, -9, -7, 160, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -47, -31, 14, 60, 150, 228, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    val PASSED_PAWN_BLOCKED_MG = intArrayOf(0, -37, -48, -41, -18, -14, 62, 0)
    val PASSED_PAWN_BLOCKED_EG = intArrayOf(0, -51, -24, -5, 19, 52, 68, 0)
    val PASSED_PAWN_BLOCKED = IntArray(Rank.SIZE)

    const val PASSED_PAWN_SAFE = 0
    const val PASSED_PAWN_SAFE_ADVANCE = 1
    const val PASSED_PAWN_SAFE_PATH = 2
    const val PASSED_PAWN_DEFENDED = 3
    const val PASSED_PAWN_DEFENDED_ADVANCE = 4
    const val PASSED_PAWN_KING_DISTANCE = 5

    val PASSED_PAWN_BONUS_MG = intArrayOf(-8, -2, -39, 32, 13, -5)
    val PASSED_PAWN_BONUS_EG = intArrayOf(9, 34, 33, -5, 12, 12)
    val PASSED_PAWN_BONUS = IntArray(PASSED_PAWN_BONUS_EG.size)

    val PAWN_SHIELD_MG = arrayOf(
            arrayOf(
                    intArrayOf(0, 49, 52, 6, 16, 13, -205, 0),
                    intArrayOf(0, 68, 65, 24, 28, 71, 50, 0),
                    intArrayOf(0, 63, 36, 34, 21, -21, 94, 0),
                    intArrayOf(0, 18, 24, 32, 32, -35, -99, 0)
            ),
            arrayOf(
                    intArrayOf(8, 32, 32, 19, 17, 53, -64, 0),
                    intArrayOf(78, 56, 28, 20, 17, 51, -51, 0),
                    intArrayOf(59, 40, 3, 16, 25, 60, -51, 0),
                    intArrayOf(57, 12, 2, 12, 6, -21, -32, 0)
            )
    )
    val PAWN_SHIELD_EG = arrayOf(
            arrayOf(
                    intArrayOf(0, -43, -25, 1, 5, 67, 97, 0),
                    intArrayOf(0, -25, -12, -4, -5, 6, -82, 0),
                    intArrayOf(0, -11, 1, -4, -2, -4, 39, 0),
                    intArrayOf(0, 10, 13, -12, -26, -30, 133, 0)
            ),
            arrayOf(
                    intArrayOf(-2, -19, -6, 0, 6, 10, -48, 0),
                    intArrayOf(-20, -20, -2, 0, 0, 11, 70, 0),
                    intArrayOf(-11, -11, 8, -7, -13, -23, -1, 0),
                    intArrayOf(-6, 4, 8, 0, -8, -1, 36, 0)
            )
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val PAWN_PUSH_THREAT_MG = intArrayOf(0, 0, 29, 27, 27, 23, 116)
    val PAWN_PUSH_THREAT_EG = intArrayOf(0, 0, 24, 21, 20, 13, 21)
    val PAWN_PUSH_THREAT = IntArray(PAWN_PUSH_THREAT_EG.size)

    val KING_THREAT_MG = intArrayOf(0, 0, 16, 13, 15, 12, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, 0, 0, 0, 7, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 93, 30, 95, 40, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 0, 23, 0, 67, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    val PINNED_BONUS_MG = intArrayOf(0, -14, 1, -16, -37, -44, 0)
    val PINNED_BONUS_EG = intArrayOf(0, 4, -14, 3, 17, 10, 0)
    val PINNED_BONUS = IntArray(Piece.SIZE)

    const val OTHER_BONUS_BISHOP_PAIR = 0
    const val OTHER_BONUS_ROOK_ON_SEVENTH = 1
    const val OTHER_BONUS_ROOK_OPEN_FILE = 2
    const val OTHER_BONUS_ROOK_HALF_OPEN_FILE = 3

    val OTHER_BONUS_MG = intArrayOf(25, -2, 37, 15)
    val OTHER_BONUS_EG = intArrayOf(55, 30, 12, 2)
    val OTHER_BONUS = IntArray(OTHER_BONUS_EG.size)

    val THREATEN_BY_KNIGHT_MG = intArrayOf(0, 5, 0, 30, 84, 45, 0)
    val THREATEN_BY_KNIGHT_EG = intArrayOf(0, 20, 0, 31, -20, -24, 0)
    val THREATEN_BY_KNIGHT = IntArray(Piece.SIZE)

    val THREATEN_BY_BISHOP_MG = intArrayOf(0, 2, 27, 0, 50, 56, 0)
    val THREATEN_BY_BISHOP_EG = intArrayOf(0, 28, 25, 0, 9, 115, 0)
    val THREATEN_BY_BISHOP = IntArray(Piece.SIZE)

    val THREATEN_BY_ROOK_MG = intArrayOf(0, -4, 34, 33, 0, 83, 0)
    val THREATEN_BY_ROOK_EG = intArrayOf(0, 29, 33, 12, 0, 14, 0)
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

