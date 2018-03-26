package pirarucu.move

import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square

/**
 * 16 Bits move representation
 */
object Move {
    // Predefined moves
    const val NONE = 0
    const val NULL = 65535

    const val NONE_STRING = "none"
    const val NULL_STRING = "null"

    private const val TO_SHIFT = 6
    private const val MOVE_TYPE_SHIFT = 12

    const val SIZE = 16

    fun createPromotionMove(fromSquare: Int, toSquare: Int,
        moveType: Int): Int {
        return createMove(fromSquare, toSquare, moveType)
    }

    fun createPassantMove(fromSquare: Int, toSquare: Int): Int {
        return createMove(fromSquare, toSquare, MoveType.TYPE_PASSANT)
    }

    fun createCastlingMove(fromSquare: Int, toSquare: Int): Int {
        return createMove(fromSquare, toSquare, MoveType.TYPE_CASTLING)
    }

    fun createMove(
        fromSquare: Int,
        toSquare: Int,
        moveType: Int = MoveType.TYPE_NORMAL): Int {
        return (fromSquare or (toSquare shl TO_SHIFT) or (moveType shl MOVE_TYPE_SHIFT))
    }

    fun isValid(move: Int): Boolean {
        return move != NONE && move != NULL
    }

    fun getFromSquare(move: Int): Int {
        return move and Square.H8
    }

    fun getToSquare(move: Int): Int {
        return (move ushr TO_SHIFT) and Square.H8
    }

    fun getMoveType(move: Int): Int {
        return move.ushr(MOVE_TYPE_SHIFT) and 0xf
    }

    fun toString(move: Int): String {
        return when (move) {
            Move.NONE -> NONE_STRING
            Move.NULL -> NULL_STRING
            else -> {
                val sb = StringBuilder()
                val moveType = getMoveType(move)
                sb.append(Square.toString(Move.getFromSquare(move)))
                sb.append(Square.toString(Move.getToSquare(move)))
                if (MoveType.isPromotion(moveType)) {
                    sb.append(Piece.toString(Color.WHITE, MoveType.getPromotedPiece(moveType)))
                }
                sb.toString()
            }
        }
    }
}
