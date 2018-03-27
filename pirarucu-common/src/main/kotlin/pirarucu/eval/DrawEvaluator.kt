package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece

object DrawEvaluator {

    private const val FIFTY_RULE = 100
    private const val REPETITION_RULE = 2
    private const val MOVE_REPETITION = 2
    private const val MIN_MOVE_REPETITION = 4

    // NO SIDE CAN FORCE A MATE
    fun hasSufficientMaterial(board: Board): Boolean {
        return when (board.pieceCountType[Piece.NONE]) {
            2 -> false
            3 -> (board.pieceBitboard[Color.WHITE][Piece.BISHOP]
                or board.pieceBitboard[Color.BLACK][Piece.BISHOP]
                or board.pieceBitboard[Color.WHITE][Piece.KNIGHT]
                or board.pieceBitboard[Color.BLACK][Piece.KNIGHT]) == Bitboard.EMPTY
            4 -> fourIsSufficientMaterial(board)
            else -> true
        }
    }

    private fun fourIsSufficientMaterial(board: Board): Boolean {
        val whiteKnightCount = board.pieceCountColorType[Color.WHITE][Piece.KNIGHT]
        val blackKnightCount = board.pieceCountColorType[Color.BLACK][Piece.KNIGHT]

        val whiteBishopCount = board.pieceCountColorType[Color.WHITE][Piece.BISHOP]
        val blackBishopCount = board.pieceCountColorType[Color.BLACK][Piece.BISHOP]

        return when {
            whiteKnightCount + whiteBishopCount == 1 &&
                blackKnightCount + blackBishopCount == 1 -> false
            else -> whiteKnightCount != 2 && blackKnightCount != 2
        }
    }

    fun isDrawByRules(board: Board): Boolean {
        if (board.rule50 >= FIFTY_RULE) {
            return true
        }
        if (board.moveNumber > MIN_MOVE_REPETITION) {
            var repetition = 0
            var moveNumber = board.moveNumber - MOVE_REPETITION
            while (moveNumber > 0) {
                val historyKey = board.historyZobristKey[moveNumber]
                if (historyKey == board.zobristKey) {
                    repetition++
                    if (repetition >= REPETITION_RULE) {
                        return true
                    }
                }
                moveNumber -= MOVE_REPETITION
            }
        }
        return false
    }
}
