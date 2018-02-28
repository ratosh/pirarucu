package pirarucu.move

import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square

/**
 * Original from chess22k.
 */
object Move {

    // Predefined moves
    const val NONE = 0
    const val NULL = -1

    const val NONE_STRING = "none"
    const val NULL_STRING = "null"

    private val TO_SHIFT = 6
    private val MOVED_PIECE_SHIFT = 12
    private val ATTACKED_PIECE_SHIFT = 15
    private val MOVE_TYPE_SHIFT = 18

    fun createAttackMove(fromSquare: Int, toSquare: Int,
        movedPieceType: Int, attackedPieceType: Int): Int {
        return createMove(fromSquare, toSquare, movedPieceType, attackedPieceType,
            MoveType.TYPE_NORMAL)
    }

    fun createPromotionMove(fromSquare: Int, toSquare: Int,
        moveType: Int): Int {
        return createMove(fromSquare, toSquare, Piece.PAWN, Piece.NONE, moveType)
    }

    fun createPromotionAttack(fromSquare: Int, toSquare: Int,
        attackedPieceType: Int, moveType: Int): Int {
        return createMove(fromSquare, toSquare, Piece.PAWN, attackedPieceType, moveType)
    }

    fun createPassantMove(fromSquare: Int, toSquare: Int): Int {
        return createMove(fromSquare, toSquare, Piece.PAWN, Piece.PAWN, MoveType.TYPE_PASSANT)
    }

    fun createCastlingMove(fromSquare: Int, toSquare: Int): Int {
        return createMove(fromSquare, toSquare, Piece.KING, Piece.NONE, MoveType.TYPE_CASTLING)
    }

    fun createMove(
        fromSquare: Int,
        toSquare: Int,
        movedPieceType: Int,
        attackedPieceType: Int = 0,
        moveType: Int = MoveType.TYPE_NORMAL): Int {
        return (fromSquare or (toSquare shl TO_SHIFT) or (movedPieceType shl MOVED_PIECE_SHIFT)
            or (attackedPieceType shl ATTACKED_PIECE_SHIFT) or (moveType shl MOVE_TYPE_SHIFT))
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

    fun getMovedPieceType(move: Int): Int {
        return move.ushr(MOVED_PIECE_SHIFT) and Piece.SIZE
    }

    fun getAttackedPieceType(move: Int): Int {
        return move.ushr(ATTACKED_PIECE_SHIFT) and Piece.SIZE
    }

    fun getMoveType(move: Int): Int {
        return move.ushr(MOVE_TYPE_SHIFT) and 0xf
    }

    fun toString(move: Int): String {
        if (move == Move.NONE) {
            return NONE_STRING
        } else if (move == Move.NULL) {
            return NULL_STRING
        }
        val sb = StringBuilder()
        val moveType = getMoveType(move)
        sb.append(Square.toString(Move.getFromSquare(move)))
        sb.append(Square.toString(Move.getToSquare(move)))
        if (MoveType.isPromotion(moveType)) {
            sb.append(Piece.toString(Color.WHITE, MoveType.getPromotedPiece(moveType)))
        }
        return sb.toString()
    }

    fun isCapture(move: Int): Boolean {
        return Piece.NONE != getAttackedPieceType(move)
    }

    fun getFromTo(move: Int): Int {
        return move and 0xFFF
    }
}