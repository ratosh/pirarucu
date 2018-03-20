package pirarucu.eval

import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.util.SplitValue

object EvalConstants {

    val PHASE_PIECE_VALUE = intArrayOf(0, 0, 45, 46, 88, 162, 0)
    var PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
        PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
        PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
        PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
        PHASE_PIECE_VALUE[Piece.QUEEN] * 2

    val PIECE_SCORE = intArrayOf(
        0,
        SplitValue.mergeParts(100, 100),
        SplitValue.mergeParts(320, 330),
        SplitValue.mergeParts(330, 350),
        SplitValue.mergeParts(500, 550),
        SplitValue.mergeParts(900, 950),
        0
    )

    private val MG_PSQT = arrayOf(
        arrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        arrayOf(
            0, 0, 0, 0,
            50, 50, 50, 50,
            10, 10, 20, 30,
            5, 5, 10, 25,
            0, 0, 0, 20,
            5, -5, -10, 0,
            5, 10, 10, -20,
            0, 0, 0, 0
        ),
        arrayOf(
            -50, -40, -30, -30,
            -40, -20, 0, 0,
            -30, 0, 10, 15,
            -30, 5, 15, 20,
            -30, 0, 15, 20,
            -30, 5, 10, 15,
            -40, -20, 0, 5,
            -50, -40, -30, -30
        ),
        arrayOf(
            -20, -10, -10, -10,
            -10, 0, 0, 0,
            -10, 0, 5, 10,
            -10, 5, 5, 10,
            -10, 0, 10, 10,
            -10, 10, 10, 10,
            -10, 5, 0, 0,
            -20, -10, -10, -10
        ),
        arrayOf(
            0, 0, 0, 0,
            5, 10, 10, 10,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            0, 0, 0, 5
        ),
        arrayOf(
            -20, -10, -10, -5,
            -10, 0, 0, 0,
            -10, 0, 5, 5,
            -5, 0, 5, 5,
            0, 0, 5, 5,
            -10, 5, 5, 5,
            -10, 0, 5, 0,
            -20, -10, -10, -5
        ),
        arrayOf(
            -30, -40, -40, -50,
            -30, -40, -40, -50,
            -30, -40, -40, -50,
            -30, -40, -40, -50,
            -20, -30, -30, -40,
            -10, -20, -20, -20,
            20, 20, 0, 0,
            20, 30, 10, 0
        )
    )

    private val EG_PSQT = arrayOf(
        arrayOf(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
        ),
        arrayOf(
            0, 0, 0, 0,
            50, 50, 50, 50,
            10, 10, 20, 30,
            5, 5, 10, 25,
            0, 0, 0, 20,
            5, -5, -10, 0,
            5, 10, 10, -20,
            0, 0, 0, 0
        ),
        arrayOf(
            -50, -40, -30, -30,
            -40, -20, 0, 0,
            -30, 0, 10, 15,
            -30, 5, 15, 20,
            -30, 0, 15, 20,
            -30, 5, 10, 15,
            -40, -20, 0, 5,
            -50, -40, -30, -30
        ),
        arrayOf(
            -20, -10, -10, -10,
            -10, 0, 0, 0,
            -10, 0, 5, 10,
            -10, 5, 5, 10,
            -10, 0, 10, 10,
            -10, 10, 10, 10,
            -10, 5, 0, 0,
            -20, -10, -10, -10
        ),
        arrayOf(
            0, 0, 0, 0,
            5, 10, 10, 10,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            -5, 0, 0, 0,
            0, 0, 0, 5
        ),
        arrayOf(
            -20, -10, -10, -5,
            -10, 0, 0, 0,
            -10, 0, 5, 5,
            -5, 0, 5, 5,
            0, 0, 5, 5,
            -10, 5, 5, 5,
            -10, 0, 5, 0,
            -20, -10, -10, -5
        ),
        arrayOf(
            -50, -40, -30, -20,
            -30, -20, -10, 0,
            -30, -10, 20, 30,
            -30, -10, 30, 40,
            -30, -10, 30, 40,
            -30, -10, 20, 30,
            -30, -30, 0, 0,
            -50, -30, -30, -30
        )
    )

    val PSQT = Array(Piece.SIZE) { IntArray(Square.SIZE) }

    init {
        for (square in Square.A1 until Square.SIZE / 2) {
            for (piece in Piece.PAWN until Piece.SIZE) {
                val psqtValue =
                    SplitValue.mergeParts(MG_PSQT[piece][square], EG_PSQT[piece][square])
                PSQT[piece][square] = psqtValue
                PSQT[piece][Square.flipHorizontal(square)] = psqtValue
            }
        }
    }

    fun update() {
        PHASE_MAX = PHASE_PIECE_VALUE[Piece.PAWN] * 16 +
            PHASE_PIECE_VALUE[Piece.KNIGHT] * 4 +
            PHASE_PIECE_VALUE[Piece.BISHOP] * 4 +
            PHASE_PIECE_VALUE[Piece.ROOK] * 4 +
            PHASE_PIECE_VALUE[Piece.QUEEN] * 2
    }
}