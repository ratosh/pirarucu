package pirarucu.tuning

import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.util.SplitValue
import kotlin.math.min

object TunableConstants {
    val RAZOR_MARGIN = intArrayOf(0, 280, 300, 320)
    val FUTILITY_CHILD_MARGIN = intArrayOf(0, 120, 180, 250, 330, 420, 520)
    val FUTILITY_PARENT_MARGIN = intArrayOf(0, 100, 200, 310, 430, 550, 660)

    val TEMPO_MG = intArrayOf(10, -10)
    val TEMPO_EG = intArrayOf(2, -2)
    val TEMPO = IntArray(TEMPO_MG.size)

    val PHASE_PIECE_VALUE = intArrayOf(0, 1, 9, 10, 20, 40, 0)

    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val MATERIAL_SCORE_MG = intArrayOf(0, 144, 608, 642, 799, 1765)
    val MATERIAL_SCORE_EG = intArrayOf(0, 174, 384, 421, 766, 1366)
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
            -10, -10, -20, -30,
            -5, -15, -20, -30,
            -5, -15, -20, -30,
            -5, -15, -20, -30,
            5, -5, -5, -15,
            15, 5, 5, 5,
            25, 30, 15, 15,
            45, 70, 25, 45
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
            -40, -30, -20, -10,
            -20, -10, 0, 10,
            -20, 0, 30, 40,
            -20, 0, 40, 50,
            -20, 0, 40, 50,
            -20, 0, 30, 40,
            -20, -20, 10, 10,
            -40, -20, -20, -20
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

    val OUTPOST_MG = arrayOf(
        intArrayOf(0, 0, 6, -10, 0, 0, 0),
        intArrayOf(0, 0, 4, -16, 0, 0, 0)
    )
    val OUTPOST_EG = arrayOf(
        intArrayOf(0, 0, 34, 9, 0, 0, 0),
        intArrayOf(0, 0, 4, 3, 0, 0, 0)
    )
    val OUTPOST = Array(2) { IntArray(Piece.SIZE) }

    const val PAWN_BONUS_SUPPORTED = 0
    const val PAWN_BONUS_PHALANX = 1
    const val PAWN_BONUS_ISOLATED = 2
    const val PAWN_BONUS_STACKED = 3
    const val PAWN_BONUS_BACKWARD = 4

    val PAWN_BONUS_MG = intArrayOf(24, 11, -21, -2, -5)
    val PAWN_BONUS_EG = intArrayOf(5, -2, 0, -41, -7)
    val PAWN_BONUS = IntArray(PAWN_BONUS_EG.size)

    val PAWN_PASSED_MG = intArrayOf(0, 11, -1, -4, 19, 32, 64, 0)
    val PAWN_PASSED_EG = intArrayOf(0, -31, -19, 11, 47, 155, 225, 0)
    val PAWN_PASSED = IntArray(Rank.SIZE)

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
            TEMPO[index] = SplitValue.mergeParts(TEMPO_MG[index], TEMPO_EG[index])
        }

        for (index in 0 until OUTPOST[0].size) {
            OUTPOST[0][index] = SplitValue.mergeParts(OUTPOST_MG[0][index], OUTPOST_EG[0][index])
            OUTPOST[1][index] = SplitValue.mergeParts(OUTPOST_MG[1][index], OUTPOST_EG[1][index])
        }

        for (index in 0 until PAWN_BONUS.size) {
            PAWN_BONUS[index] = SplitValue.mergeParts(PAWN_BONUS_MG[index], PAWN_BONUS_EG[index])
        }

        for (index in 0 until PAWN_PASSED.size) {
            PAWN_PASSED[index] = SplitValue.mergeParts(PAWN_PASSED_MG[index], PAWN_PASSED_EG[index])
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
    }
}

