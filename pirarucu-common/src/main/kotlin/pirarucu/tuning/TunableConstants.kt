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

    val MATERIAL_SCORE_MG = intArrayOf(0, 87, 448, 460, 626, 1218)
    val MATERIAL_SCORE_EG = intArrayOf(0, 132, 405, 440, 761, 1492)
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
            intArrayOf(0, 0, 0, 0, -42, -6, 65, 96, 22, 29, 69, 80, 4, 10, 18, 27, -2, -8, 10, 19, -11, -10, 2, 4, -4, -8, 1, 3, 0, 0, 0, 0),
            intArrayOf(-183, -119, -100, -78, -76, -44, -10, -49, -23, -6, 2, 28, 11, 18, 41, 28, -2, 23, 29, 29, -26, 4, 10, 18, -10, -13, -5, 11, -56, -23, -6, -6),
            intArrayOf(-51, -73, -50, -120, -61, -47, -30, -43, 3, 6, 15, 7, -24, 1, 10, 14, 0, -3, 7, 19, -8, 9, 11, 6, 8, 20, 20, 8, 1, 18, -4, 5),
            intArrayOf(-3, -24, -15, -25, -3, -18, -3, -2, -11, 12, 2, 0, -13, 3, 8, 1, -11, 0, -8, -4, -10, 4, -2, -1, -16, 1, 8, 4, -8, 2, 10, 16),
            intArrayOf(7, 6, 19, 5, 21, -13, -10, -19, 33, 15, 6, 4, 24, 20, 20, 7, 26, 26, 28, 28, 36, 39, 37, 31, 55, 46, 46, 49, 45, 43, 42, 49),
            intArrayOf(-23, 209, 12, 28, 16, 4, 44, -51, -51, -2, -18, -61, -83, -57, -82, -106, -125, -97, -78, -80, -52, -44, -80, -56, 28, -4, -36, -24, 62, 29, 15, 31)
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
            intArrayOf(0, 0, 0, 0, 50, 40, 3, -37, 32, 26, -5, -47, 9, 5, -9, -23, -10, -8, -17, -25, -11, -14, -17, -15, -11, -14, -12, -14, 0, 0, 0, 0),
            intArrayOf(-77, -9, -9, 15, -8, 13, 13, 20, 0, 12, 31, 29, 7, 24, 30, 43, 11, 16, 26, 38, -6, -7, 2, 25, 6, 2, 2, 0, -8, -15, -5, 4),
            intArrayOf(-17, 11, 6, 20, 4, -1, 0, 6, 3, 0, 0, -9, 17, 8, 3, 10, 0, 0, 8, 5, -8, 1, 0, 10, -13, -12, -18, -5, -13, -15, -10, -6),
            intArrayOf(26, 33, 41, 39, 5, 24, 22, 15, 13, 12, 22, 9, 11, 13, 12, 8, -3, -5, 9, 6, -16, -22, -8, -8, -20, -22, -18, -13, -14, -15, -11, -19),
            intArrayOf(4, 1, 16, 28, -25, 45, 53, 57, -26, 16, 55, 47, 2, 21, 27, 52, 8, 13, 17, 30, -42, -20, 10, 2, -82, -54, -32, -24, -82, -58, -47, -44),
            intArrayOf(-56, -83, -22, 8, -5, 73, 74, 75, 15, 70, 69, 76, 11, 53, 65, 71, 9, 41, 46, 47, 0, 24, 31, 31, -17, 12, 20, 17, -77, -41, -32, -41)
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-52, -28, -15, -7, -3, 2, 9, 12, 14),
            intArrayOf(-34, -16, -7, 0, 4, 9, 10, 14, 13, 18, 25, 26, 53, 48),
            intArrayOf(-68, -44, -37, -31, -33, -25, -20, -17, -15, -12, -9, -10, -10, -5, -6),
            intArrayOf(-7, -9, -13, -4, -3, 4, 5, 8, 9, 12, 14, 16, 17, 18, 21, 21, 31, 30, 33, 34, 38, 77, 52, 55, 126, 184, 253, 253),
            intArrayOf(-114, -64, -23, -3, 13, 3, 27, 28, 26)
    )

    val MOBILITY_EG = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(-2, -14, 7, 16, 25, 35, 33, 30, 19),
            intArrayOf(-24, -44, -22, -5, 7, 17, 24, 27, 34, 34, 28, 35, 33, 33),
            intArrayOf(0, 10, 14, 14, 25, 28, 30, 33, 38, 43, 45, 50, 54, 48, 50),
            intArrayOf(155, 10, -5, -48, -30, -50, -22, -14, -1, 0, 1, 11, 12, 16, 12, 18, 9, 8, 4, 3, -10, -15, -17, -33, -56, -121, -108, -44),
            intArrayOf(0, 30, 26, 27, 18, 9, -1, 0, -24)
    )
    val MOBILITY = Array(Piece.SIZE) { IntArray(28) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, 6, 8, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 17, 12, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 73, 73, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 12, 44, 0, 0, 0)
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

    val PASSED_PAWN_MG = intArrayOf(0, -23, -40, -32, -11, -7, 160, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -47, -29, 14, 60, 148, 226, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    val PASSED_PAWN_BLOCKED_MG = intArrayOf(0, -35, -48, -41, -20, -12, 64, 0)
    val PASSED_PAWN_BLOCKED_EG = intArrayOf(0, -52, -24, -5, 19, 50, 72, 0)
    val PASSED_PAWN_BLOCKED = IntArray(Rank.SIZE)

    const val PASSED_PAWN_SAFE = 0
    const val PASSED_PAWN_SAFE_ADVANCE = 1
    const val PASSED_PAWN_SAFE_PATH = 2
    const val PASSED_PAWN_DEFENDED = 3
    const val PASSED_PAWN_DEFENDED_ADVANCE = 4
    const val PASSED_PAWN_KING_DISTANCE = 5

    val PASSED_PAWN_BONUS_MG = intArrayOf(-8, -4, -37, 32, 11, -5)
    val PASSED_PAWN_BONUS_EG = intArrayOf(9, 34, 33, -3, 12, 12)
    val PASSED_PAWN_BONUS = IntArray(PASSED_PAWN_BONUS_EG.size)

    val PAWN_SHIELD_MG = arrayOf(
            arrayOf(
                    intArrayOf(0, 51, 54, 10, 14, 7, -208, 0),
                    intArrayOf(0, 66, 63, 22, 26, 70, 48, 0),
                    intArrayOf(0, 61, 34, 34, 19, -17, 86, 0),
                    intArrayOf(0, 18, 24, 32, 32, -39, -95, 0)
            ),
            arrayOf(
                    intArrayOf(8, 32, 32, 19, 15, 53, -69, 0),
                    intArrayOf(78, 56, 28, 21, 18, 44, -56, 0),
                    intArrayOf(59, 42, 5, 18, 25, 54, -58, 0),
                    intArrayOf(57, 12, 2, 12, 4, -21, -36, 0)
            )
    )
    val PAWN_SHIELD_EG = arrayOf(
            arrayOf(
                    intArrayOf(0, -43, -25, -2, 4, 69, 98, 0),
                    intArrayOf(0, -25, -12, -4, -5, 6, -80, 0),
                    intArrayOf(0, -9, 3, -4, -2, -2, 45, 0),
                    intArrayOf(0, 10, 15, -10, -26, -26, 125, 0)
            ),
            arrayOf(
                    intArrayOf(-2, -19, -4, 0, 8, 8, -44, 0),
                    intArrayOf(-20, -20, -2, 0, -2, 11, 62, 0),
                    intArrayOf(-11, -11, 6, -9, -15, -23, -7, 0),
                    intArrayOf(-6, 4, 8, 0, -8, -1, 29, 0)
            )
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val PAWN_PUSH_THREAT_MG = intArrayOf(0, 0, 29, 27, 29, 23, 118)
    val PAWN_PUSH_THREAT_EG = intArrayOf(0, 0, 22, 21, 18, 11, 19)
    val PAWN_PUSH_THREAT = IntArray(PAWN_PUSH_THREAT_EG.size)

    val KING_THREAT_MG = intArrayOf(0, 0, 14, 13, 15, 12, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, -2, 0, -4, 5, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 91, 28, 93, 38, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, -7, 21, -9, 65, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    val PINNED_BONUS_MG = intArrayOf(0, -12, 1, -18, -35, -45, 0)
    val PINNED_BONUS_EG = intArrayOf(0, 0, -14, 3, 17, 10, 0)
    val PINNED_BONUS = IntArray(Piece.SIZE)

    const val OTHER_BONUS_BISHOP_PAIR = 0
    const val OTHER_BONUS_ROOK_ON_SEVENTH = 1
    const val OTHER_BONUS_ROOK_OPEN_FILE = 2
    const val OTHER_BONUS_ROOK_HALF_OPEN_FILE = 3

    val OTHER_BONUS_MG = intArrayOf(25, -4, 35, 15)
    val OTHER_BONUS_EG = intArrayOf(55, 30, 10, 0)
    val OTHER_BONUS = IntArray(OTHER_BONUS_EG.size)

    val THREATEN_BY_KNIGHT_MG = intArrayOf(0, 5, 0, 33, 84, 45, 0)
    val THREATEN_BY_KNIGHT_EG = intArrayOf(0, 18, 0, 29, -20, -18, 0)
    val THREATEN_BY_KNIGHT = IntArray(Piece.SIZE)

    val THREATEN_BY_BISHOP_MG = intArrayOf(0, 2, 27, 0, 50, 56, 0)
    val THREATEN_BY_BISHOP_EG = intArrayOf(0, 30, 25, 0, 9, 114, 0)
    val THREATEN_BY_BISHOP = IntArray(Piece.SIZE)

    val THREATEN_BY_ROOK_MG = intArrayOf(0, -4, 34, 35, 0, 85, 0)
    val THREATEN_BY_ROOK_EG = intArrayOf(0, 29, 31, 12, 0, 16, 0)
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

