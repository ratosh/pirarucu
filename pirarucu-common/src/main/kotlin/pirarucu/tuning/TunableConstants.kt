package pirarucu.tuning

import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.game.GameConstants
import pirarucu.util.SplitValue
import kotlin.math.min

object TunableConstants {
    val RAZOR_MARGIN = intArrayOf(0, 400)
    val FUTILITY_CHILD_MARGIN = intArrayOf(0, 120, 180, 250, 330, 420, 520)
    val FUTILITY_PARENT_MARGIN = intArrayOf(0, 100, 200, 310, 430, 550, 660)

    val TEMPO_TUNING = intArrayOf(23, 13)
    val TEMPO = IntArray(Color.SIZE)

    val PHASE_PIECE_VALUE = intArrayOf(0, 1, 9, 10, 20, 40, 0)

    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val MATERIAL_SCORE_MG = intArrayOf(0, 144, 603, 647, 809, 1775)
    val MATERIAL_SCORE_EG = intArrayOf(0, 174, 389, 416, 771, 1366)
    val MATERIAL_SCORE = IntArray(Piece.SIZE)

    val QS_FUTILITY_VALUE = intArrayOf(0,
        min(MATERIAL_SCORE_MG[1], MATERIAL_SCORE_EG[1]),
        min(MATERIAL_SCORE_MG[2], MATERIAL_SCORE_EG[2]),
        min(MATERIAL_SCORE_MG[3], MATERIAL_SCORE_EG[3]),
        min(MATERIAL_SCORE_MG[4], MATERIAL_SCORE_EG[4]),
        min(MATERIAL_SCORE_MG[5], MATERIAL_SCORE_EG[5]))

    val SEE_VALUE = intArrayOf(0, 100, 325, 330, 550, 900, 10000)

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
            0, 0, 0, 0,
            -9, 58, 93, 98,
            -9, 12, 61, 38,
            -13, 0, -5, 32,
            -13, -21, -3, 20,
            -3, 2, -14, -16,
            -3, 1, 14, 0,
            0, 0, 0, 0
        ),
        intArrayOf(
            -40, -30, -20, -20,
            -30, -5, 15, 15,
            -25, 15, 25, 30,
            -20, 20, 30, 35,
            -20, 15, 30, 35,
            -25, 20, 25, 30,
            -30, -5, 15, 20,
            -40, -30, -20, -20
        ),
        intArrayOf(
            -20, -15, -10, -5,
            -15, -10, -5, 0,
            -10, -5, 0, 10,
            -5, 0, 10, 30,
            0, 10, 20, 35,
            -5, 0, 10, 20,
            -10, -5, 0, 10,
            -20, -10, -5, 0
        ),
        intArrayOf(
            0, 0, 0, 0,
            5, 10, 10, 10,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            0, 0, 0, 5
        ),
        intArrayOf(
            -40, -35, -30, -25,
            -30, -25, -20, -15,
            -20, -15, -10, -10,
            -10, -10, -5, -5,
            -5, -5, 0, 0,
            -5, 0, 0, 0,
            15, 25, 45, 50,
            15, 30, 60, 80
        ),
        intArrayOf(
            -58, 59, 59, 4,
            -22, 62, 57, 61,
            57, 63, 63, 61,
            -51, -32, 61, 42,
            -63, -31, -27, -61,
            -62, -33, -50, -30,
            -2, -2, -48, -63,
            2, 47, -4, 20
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
            0, 0, 0, 0,
            94, 64, 0, -13,
            39, 28, -15, -47,
            10, 1, -12, -39,
            -16, -7, -23, -39,
            -27, -23, -15, -11,
            -23, -17, -13, -1,
            0, 0, 0, 0
        ),
        intArrayOf(
            -40, -30, -20, -20,
            -30, -5, 15, 15,
            -25, 15, 25, 30,
            -20, 20, 30, 35,
            -20, 15, 30, 35,
            -25, 20, 25, 30,
            -30, -5, 15, 20,
            -40, -30, -20, -20
        ),
        intArrayOf(
            -20, -15, -10, -5,
            -15, -10, -5, 0,
            -10, -5, 0, 10,
            -5, 0, 10, 30,
            0, 10, 20, 35,
            -5, 0, 10, 20,
            -10, -5, 0, 10,
            -20, -10, -5, 0
        ),
        intArrayOf(
            0, 0, 0, 0,
            5, 10, 10, 10,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            0, 0, 0, 5
        ),
        intArrayOf(
            -15, -10, -5, 0,
            -10, 10, 10, 10,
            -5, 10, 10, 15,
            0, 10, 15, 15,
            5, 5, 10, 10,
            -10, 5, 5, 5,
            -15, 0, 0, 0,
            -25, -20, -20, -15
        ),
        intArrayOf(
            -57, -29, -13, -29,
            2, 25, 31, 16,
            -1, 48, 47, 16,
            0, 48, 31, 31,
            -18, 7, 41, 47,
            -10, 3, 36, 31,
            -31, -16, 28, 32,
            -63, -56, -16, -31
        )
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    val MOBILITY_MG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-41, -16, -6, -15, -8, 0, 16, 13, 62),
        intArrayOf(-32, -23, 0, 4, 7, 8, 12, 8, 14, 18, 36, 94, 43, 59),
        intArrayOf(-62, -62, -64, -56, -39, -27, -15, -8, 13, 20, 49, 45, 97, 15, 15),
        intArrayOf(-32, -54, -37, -30, -20, -3, -1, 0, 9, 15, 23, 28, 22, 32, 48, 31, 46, 47, 55, 76, 31, 0, 112, 111,
            90, 123, 59, 69, -40),
        intArrayOf())

    val MOBILITY_EG = arrayOf(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(-64, -31, -6, 15, 16, 33, 19, 32, -5),
        intArrayOf(-58, -63, -27, -2, 14, 23, 32, 31, 38, 27, 13, 6, 1, -4),
        intArrayOf(-80, -70, -11, 4, 6, 19, 25, 31, 41, 39, 35, 48, 29, 64, 64),
        intArrayOf(-76, -127, -113, -121, -104, -125, -96, -70, -72, -31, -17, -4, 23, 32, 28, 64, 64, 64, 63, 72, 117,
            123, 62, 85, 81, 71, 82, 107, 57),
        intArrayOf())
    val MOBILITY = Array(Piece.SIZE) { IntArray(32) }

    val PAWN_SUPPORT_MG = intArrayOf(0, 0, -6, -9, 0, 0, 0)
    val PAWN_SUPPORT_EG = intArrayOf(0, 0, 13, 12, 0, 0, 0)
    val PAWN_SUPPORT = IntArray(Piece.SIZE)

    val PAWN_THREAT_MG = intArrayOf(0, 0, 64, 64, 0, 0, 0)
    val PAWN_THREAT_EG = intArrayOf(0, 0, 23, 64, 0, 0, 0)
    val PAWN_THREAT = IntArray(Piece.SIZE)

    const val PAWN_BONUS_SUPPORTED = 0
    const val PAWN_BONUS_PHALANX = 1
    const val PAWN_BONUS_ISOLATED = 2
    const val PAWN_BONUS_STACKED = 3
    const val PAWN_BONUS_BACKWARD = 4

    val PAWN_BONUS_MG = intArrayOf(24, 11, -21, -2, -5)
    val PAWN_BONUS_EG = intArrayOf(5, -2, 0, -41, -7)
    val PAWN_BONUS = IntArray(PAWN_BONUS_EG.size)

    val PASSED_PAWN_MG = intArrayOf(0, 11, -1, -4, 19, 32, 64, 0)
    val PASSED_PAWN_EG = intArrayOf(0, -31, -19, 11, 47, 155, 225, 0)
    val PASSED_PAWN = IntArray(Rank.SIZE)

    const val PASSED_PAWN_SAFE = 0
    const val PASSED_PAWN_CAN_ADVANCE = 1
    const val PASSED_PAWN_SAFE_ADVANCE = 2
    const val PASSED_PAWN_SAFE_PATH = 3
    const val PASSED_PAWN_DEFENDED = 4
    const val PASSED_PAWN_DEFENDED_ADVANCE = 5

    val PASSED_PAWN_BONUS_MG = intArrayOf(5, 5, 5, 5, 5, 5)
    val PASSED_PAWN_BONUS_EG = intArrayOf(5, 5, 11, 26, 5, 17)
    val PASSED_PAWN_BONUS = IntArray(PASSED_PAWN_BONUS_EG.size)

    val PAWN_SHIELD_MG = arrayOf(
        arrayOf(
            intArrayOf(0, 63, 71, 29, 22, 16, -120, 0),
            intArrayOf(0, 74, 63, 50, 32, 9, -127, 0),
            intArrayOf(0, 30, 14, 17, 8, -24, -128, 0),
            intArrayOf(0, 11, 31, 23, 20, 0, -128, 0)),
        arrayOf(
            intArrayOf(30, 25, 22, 20, 6, -5, -83, 0),
            intArrayOf(64, 47, 27, 23, 14, 1, -86, 0),
            intArrayOf(35, 30, 12, 26, 18, -5, -89, 0),
            intArrayOf(15, 12, 10, 5, 3, -25, -76, 0))
    )
    val PAWN_SHIELD_EG = arrayOf(
        arrayOf(
            intArrayOf(0, -5, -10, -15, -31, -54, -128, 0),
            intArrayOf(0, -10, -15, -16, -27, -43, -125, 0),
            intArrayOf(0, 0, 1, -5, -14, -20, -127, 0),
            intArrayOf(0, 15, 0, -17, -26, -30, -124, 0)),
        arrayOf(
            intArrayOf(14, -7, -2, -11, -15, -35, -46, 0),
            intArrayOf(2, -7, -2, -5, 3, -17, -27, 0),
            intArrayOf(21, 18, 0, -4, -10, -25, -38, 0),
            intArrayOf(23, 5, 4, -11, -20, -25, -40, 0))
    )
    val PAWN_SHIELD = Array(2) { Array(File.SIZE / 2) { IntArray(Rank.SIZE) } }

    val KING_THREAT_MG = intArrayOf(0, 0, 5, 15, 7, 16, 0)
    val KING_THREAT_EG = intArrayOf(0, 0, 1, 1, 2, 11, 0)
    val KING_THREAT = IntArray(Piece.SIZE)

    val SAFE_CHECK_THREAT_MG = intArrayOf(0, 0, 63, 21, 63, 52, 0)
    val SAFE_CHECK_THREAT_EG = intArrayOf(0, 0, 18, 51, 15, 63, 0)
    val SAFE_CHECK_THREAT = IntArray(Piece.SIZE)

    init {
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

        for (index in 0 until PAWN_BONUS.size) {
            PAWN_BONUS[index] = SplitValue.mergeParts(PAWN_BONUS_MG[index], PAWN_BONUS_EG[index])
        }

        for (index in 0 until PASSED_PAWN.size) {
            PASSED_PAWN[index] = SplitValue.mergeParts(PASSED_PAWN_MG[index], PASSED_PAWN_EG[index])
        }

        for (index in 0 until PAWN_SHIELD.size) {
            for (index2 in 0 until PAWN_SHIELD[index].size) {
                for (index3 in 0 until PAWN_SHIELD[index][index2].size) {
                    PAWN_SHIELD[index][index2][index3] = SplitValue.mergeParts(
                        PAWN_SHIELD_MG[index][index2][index3],
                        PAWN_SHIELD_EG[index][index2][index3])
                }
            }
        }

        for (index in 0 until KING_THREAT.size) {
            KING_THREAT[index] = SplitValue.mergeParts(KING_THREAT_MG[index], KING_THREAT_EG[index])
        }

        for (index in 0 until SAFE_CHECK_THREAT.size) {
            SAFE_CHECK_THREAT[index] = SplitValue.mergeParts(SAFE_CHECK_THREAT_MG[index], SAFE_CHECK_THREAT_EG[index])
        }

        for (index in 0 until PASSED_PAWN_BONUS.size) {
            PASSED_PAWN_BONUS[index] = SplitValue.mergeParts(PASSED_PAWN_BONUS_MG[index], PASSED_PAWN_BONUS_EG[index])
        }
    }
}

