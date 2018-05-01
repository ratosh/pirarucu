package pirarucu.tuning

import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.util.SplitValue

object TunableConstants {
    val RAZOR_MARGIN = intArrayOf(0, 100, 150, 200, 250, 300, 350, 400)
    val FUTILITY_CHILD_MARGIN = intArrayOf(0, 120, 180, 250, 330, 420, 520)

    val TEMPO = SplitValue.mergeParts(20, 15)

    val PHASE_PIECE_VALUE = intArrayOf(0, 1, 9, 10, 20, 40, 0)

    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val QS_FUTILITY_VALUE = intArrayOf(0, 100, 325, 330, 550, 900, 10000)
    val SEE_VALUE = intArrayOf(0, 100, 325, 330, 550, 900, 10000)

    val MATERIAL_SCORE_MG = intArrayOf(0, 144, 608, 642, 799, 1765)
    val MATERIAL_SCORE_EG = intArrayOf(0, 174, 384, 421, 766, 1366)
    val MATERIAL_SCORE = IntArray(Piece.SIZE)

    val MG_PSQT = arrayOf(
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

    val EG_PSQT = arrayOf(
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


    val MATERIAL_IMBALANCE_OURS = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0)
    )

    val MATERIAL_IMBALANCE_THEIRS = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0)
    )

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
                    val psqtValue = SplitValue.mergeParts(MG_PSQT[piece][psqPosition], EG_PSQT[piece][psqPosition])
                    PSQT[piece][square] = psqtValue
                    PSQT[piece][Square.flipHorizontal(square)] = psqtValue
                    psqPosition++
                }
            }
        }
    }
}

