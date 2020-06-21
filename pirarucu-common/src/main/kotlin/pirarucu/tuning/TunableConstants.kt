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

    val MATERIAL_SCORE_MG = intArrayOf(0, 87, 448, 458, 624, 1214)
    val MATERIAL_SCORE_EG = intArrayOf(0, 134, 405, 442, 757, 1488)
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
            intArrayOf(0, 0, 0, 0, -42, -2, 65, 100, 22, 27, 69, 78, 4, 8, 16, 25, -2, -8, 10, 15, -11, -12, 0, 2, -4, -10, 1, 3, 0, 0, 0, 0),
            intArrayOf(-183, -123, -98, -78, -76, -44, -10, -49, -27, -4, 6, 30, 11, 20, 41, 26, -2, 25, 27, 31, -24, 4, 8, 18, -8, -13, -5, 11, -56, -25, -4, -4),
            intArrayOf(-51, -71, -50, -118, -63, -45, -30, -41, 3, 8, 15, 9, -24, 3, 10, 16, -2, -3, 9, 21, -10, 9, 11, 6, 6, 18, 18, 8, -1, 16, -6, 3),
            intArrayOf(-3, -26, -13, -27, -3, -18, -1, 0, -11, 14, 4, 2, -13, 3, 8, 3, -9, -2, -8, -2, -10, 6, -2, -3, -18, 1, 6, 4, -8, 2, 10, 14),
            intArrayOf(7, 4, 17, 5, 21, -13, -10, -17, 35, 17, 8, 6, 24, 22, 20, 9, 26, 26, 28, 28, 34, 39, 37, 29, 53, 46, 46, 47, 43, 41, 42, 47),
            intArrayOf(-23, 207, 10, 28, 14, 2, 42, -51, -51, -2, -16, -59, -85, -55, -82, -108, -127, -97, -76, -82, -52, -42, -78, -58, 28, -4, -36, -24, 62, 31, 15, 29)
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
            intArrayOf(0, 0, 0, 0, 54, 44, 5, -33, 30, 28, -3, -49, 7, 3, -11, -23, -10, -10, -17, -25, -11, -14, -17, -17, -11, -16, -12, -18, 0, 0, 0, 0),
            intArrayOf(-77, -9, -11, 13, -10, 13, 11, 20, -2, 14, 33, 31, 7, 24, 32, 45, 9, 16, 24, 38, -6, -9, 2, 25, 8, 0, 2, 2, -8, -17, -5, 6),
            intArrayOf(-17, 13, 8, 18, 2, -1, 0, 8, 5, 2, 0, -9, 15, 8, 3, 10, -2, 0, 6, 5, -8, -1, 0, 10, -13, -14, -20, -3, -15, -13, -12, -8),
            intArrayOf(26, 35, 43, 37, 5, 24, 24, 17, 15, 12, 22, 7, 9, 13, 12, 10, -1, -3, 11, 8, -16, -22, -6, -8, -20, -22, -18, -13, -14, -17, -11, -19),
            intArrayOf(4, 1, 16, 28, -23, 45, 53, 55, -24, 18, 57, 49, 4, 23, 27, 54, 8, 13, 17, 28, -44, -18, 10, 2, -84, -56, -32, -26, -84, -60, -49, -44),
            intArrayOf(-56, -85, -22, 6, -7, 71, 72, 73, 15, 68, 69, 78, 9, 51, 65, 73, 9, 41, 46, 49, 0, 24, 31, 31, -15, 12, 22, 19, -79, -41, -32, -43)
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-52, -30, -15, -7, -3, 2, 9, 14, 16),
            intArrayOf(-34, -16, -9, 0, 4, 9, 12, 16, 13, 20, 25, 28, 55, 50),
            intArrayOf(-68, -44, -37, -31, -33, -25, -22, -17, -15, -10, -7, -8, -10, -5, -6),
            intArrayOf(-7, -11, -15, -6, -1, 4, 5, 6, 9, 12, 14, 16, 17, 18, 23, 21, 31, 30, 31, 36, 38, 77, 52, 57, 128, 186, 253, 253),
            intArrayOf(-114, -66, -25, -3, 13, 5, 29, 26, 26)
    )

    val MOBILITY_EG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-2, -14, 7, 16, 27, 37, 35, 30, 19),
            intArrayOf(-24, -46, -20, -5, 7, 19, 26, 29, 36, 34, 30, 35, 33, 31),
            intArrayOf(0, 10, 16, 14, 25, 28, 32, 33, 38, 45, 45, 52, 54, 50, 52),
            intArrayOf(155, 8, -7, -50, -30, -50, -22, -16, -1, 2, 3, 11, 12, 16, 12, 18, 9, 6, 2, 1, -12, -17, -17, -35, -54, -121, -106, -46),
            intArrayOf(0, 28, 28, 27, 20, 9, -1, 0, -24)
    )
    val MOBILITY = Array(Piece.SIZE) { IntArray(28) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 6, 8, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 17, 10, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 75, 73, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 12, 44, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_STRUCTURE_DEFENDED = 0
    const val PAWN_STRUCTURE_PHALANX = 1
    const val PAWN_STRUCTURE_ISOLATED = 2
    const val PAWN_STRUCTURE_STACKED = 3
    const val PAWN_STRUCTURE_BACKWARD = 4
    const val PAWN_STRUCTURE_BACKWARD_HALF_OPEN = 5

    val PAWN_STRUCTURE_MG = intArrayOf(25, 10, -4, -14, 1, -26)
    val PAWN_STRUCTURE_EG = intArrayOf(6, 1, -14, -14, -12, -16)
    val PAWN_STRUCTURE = IntArray(PAWN_STRUCTURE_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, -23, -38, -32, -11, -5, 162, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -47, -31, 14, 58, 150, 228, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    val PASSED_PAWN_BLOCKED_MG = intArrayOf(0, -37, -48, -41, -20, -14, 62, 0)
    val PASSED_PAWN_BLOCKED_EG = intArrayOf(0, -52, -24, -5, 17, 48, 70, 0)
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
                    intArrayOf(0, 53, 54, 8, 16, 5, -210, 0),
                    intArrayOf(0, 66, 63, 24, 28, 72, 50, 0),
                    intArrayOf(0, 63, 34, 34, 21, -15, 88, 0),
                    intArrayOf(0, 18, 24, 32, 32, -37, -97, 0)
            ),
            arrayOf(
                    intArrayOf(8, 32, 32, 19, 17, 51, -71, 0),
                    intArrayOf(78, 56, 28, 21, 20, 42, -56, 0),
                    intArrayOf(59, 42, 5, 18, 25, 56, -58, 0),
                    intArrayOf(57, 14, 2, 12, 4, -23, -34, 0)
            )
    )
    val PAWN_SHIELD_EG = arrayOf(
            arrayOf(
                    intArrayOf(0, -43, -25, -2, 4, 67, 96, 0),
                    intArrayOf(0, -25, -12, -6, -3, 6, -78, 0),
                    intArrayOf(0, -9, 5, -4, -2, -4, 45, 0),
                    intArrayOf(0, 10, 15, -10, -26, -28, 123, 0)
            ),
            arrayOf(
                    intArrayOf(-2, -19, -4, 0, 10, 10, -42, 0),
                    intArrayOf(-20, -20, -2, 0, -4, 11, 62, 0),
                    intArrayOf(-11, -11, 6, -7, -13, -23, -5, 0),
                    intArrayOf(-6, 4, 8, 0, -8, 1, 31, 0)
            )
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val PAWN_PUSH_THREAT_MG = intArrayOf(0, 0, 29, 27, 29, 23, 118)
    val PAWN_PUSH_THREAT_EG = intArrayOf(0, 0, 24, 21, 20, 13, 19)
    val PAWN_PUSH_THREAT = IntArray(PAWN_PUSH_THREAT_EG.size)

    val KING_THREAT_MG = intArrayOf(0, 0, 16, 13, 17, 14, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, 0, 0, 0, 5, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 93, 30, 95, 40, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 0, 23, 0, 67, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    val PINNED_PENALTY_MG = intArrayOf(0, 12, 1, 20, 33, 47, 0)
    val PINNED_PENALTY_EG = intArrayOf(0, 0, 16, 0, 0, 0, 0)
    val PINNED_PENALTY = IntArray(Piece.SIZE)

    const val OTHER_BONUS_BISHOP_PAIR = 0
    const val OTHER_BONUS_ROOK_ON_SEVENTH = 1
    const val OTHER_BONUS_ROOK_OPEN_FILE = 2
    const val OTHER_BONUS_ROOK_HALF_OPEN_FILE = 3

    val OTHER_BONUS_MG = intArrayOf(25, -4, 35, 13)
    val OTHER_BONUS_EG = intArrayOf(55, 30, 10, 0)
    val OTHER_BONUS = IntArray(OTHER_BONUS_EG.size)

    val THREATEN_BY_KNIGHT_MG = intArrayOf(0, 7, 0, 33, 86, 43, 0)
    val THREATEN_BY_KNIGHT_EG = intArrayOf(0, 18, 0, 29, -18, -20, 0)
    val THREATEN_BY_KNIGHT = IntArray(Piece.SIZE)

    val THREATEN_BY_BISHOP_MG = intArrayOf(0, 2, 29, 0, 50, 58, 0)
    val THREATEN_BY_BISHOP_EG = intArrayOf(0, 30, 27, 0, 11, 116, 0)
    val THREATEN_BY_BISHOP = IntArray(Piece.SIZE)

    val THREATEN_BY_ROOK_MG = intArrayOf(0, -4, 36, 35, 0, 83, 0)
    val THREATEN_BY_ROOK_EG = intArrayOf(0, 31, 33, 12, 0, 16, 0)
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

        for (index in 0 until PINNED_PENALTY.size) {
            PINNED_PENALTY[index] = SplitValue.mergeParts(PINNED_PENALTY_MG[index], PINNED_PENALTY_EG[index])
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

