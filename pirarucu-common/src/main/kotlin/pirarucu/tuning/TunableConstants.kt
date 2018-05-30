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
            40, 40, 40, 50,
            -5, -5, 5, 15,
            -10, -10, -5, 10,
            -15, -15, -15, 5,
            -10, -20, -25, -15,
            -10, -5, -5, -35,
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
            40, 40, 40, 50,
            -5, -5, 5, 15,
            -10, -10, -5, 10,
            -15, -15, -15, 5,
            -10, -20, -25, -15,
            -10, -5, -5, -35,
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
        for (index in 0 until TEMPO.size) {
            TEMPO[index] = SplitValue.mergeParts(TEMPO_MG[index], TEMPO_EG[index])
        }

    }
}

