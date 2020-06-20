package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.util.PlatformSpecific

object DrawEvaluator {

    private const val FIFTY_RULE = 100
    private const val MOVE_REPETITION = 2
    private const val MIN_MOVE_REPETITION = 4

    /**
     * Both side can force a mate.
     * 3 pieces draws:
     * KNk
     * KBk
     */
    fun hasSufficientMaterial(board: Board): Boolean {
        return when (PlatformSpecific.bitCount(board.gameBitboard)) {
            2 -> false
            3 -> (board.pieceBitboard[Color.WHITE][Piece.BISHOP]
                or board.pieceBitboard[Color.BLACK][Piece.BISHOP]
                or board.pieceBitboard[Color.WHITE][Piece.KNIGHT]
                or board.pieceBitboard[Color.BLACK][Piece.KNIGHT]) == Bitboard.EMPTY
            4 -> fourIsSufficientMaterial(board)
            else -> true
        }
    }

    /**
     * Can easily convert material advantage
     * NOTE: Only used on winning side view
     * Not enough material when:
     * KNkp (KNkn already detected)
     * KBkp (KBkn and KBkb already detected)
     * KRkx
     */
    fun canEasilyConvertAdvantage(board: Board, ourColor: Int): Boolean {
        return board.pieceBitboard[ourColor][Piece.NONE] !=
            board.pieceBitboard[ourColor][Piece.KING] or board.pieceBitboard[ourColor][Piece.KNIGHT] &&
            board.pieceBitboard[ourColor][Piece.NONE] !=
                board.pieceBitboard[ourColor][Piece.KING] or board.pieceBitboard[ourColor][Piece.BISHOP]
    }

    /**
     * Both sides have sufficient material to force a mate
     * Detecting:
     * KNNk
     * KNkn
     * KNkb
     * KBBk
     * KBkb
     */
    private fun fourIsSufficientMaterial(board: Board): Boolean {
        val whiteKnightCount = PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.KNIGHT])
        val blackKnightCount = PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.KNIGHT])

        val whiteBishopCount = PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.BISHOP])
        val blackBishopCount = PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.BISHOP])

        return !(whiteKnightCount + whiteBishopCount == 1 &&
            blackKnightCount + blackBishopCount == 1) &&
            whiteKnightCount != 2 && blackKnightCount != 2
    }

    fun isDrawByRules(board: Board): Boolean {
        if (board.rule50 > FIFTY_RULE) {
            return true
        }
        // A repetition can only happen under rule50 moves as we should not be able to repeat a
        // position before last rule50 reset
        if (board.rule50 >= MIN_MOVE_REPETITION) {
            var moveNumber = board.moveNumber - MIN_MOVE_REPETITION
            while (moveNumber >= board.moveNumber - board.rule50) {
                val historyKey = board.historyZobristKey[moveNumber]
                // Repetition found
                if (historyKey == board.zobristKey) {
                    return true
                }
                moveNumber -= MOVE_REPETITION
            }
        }
        return false
    }
}
