package pirarucu.move

import pirarucu.board.Piece

object MoveType {

    const val TYPE_NORMAL = 0

    const val TYPE_PASSANT = 1
    const val TYPE_CASTLING = 2

    const val TYPE_PROMOTION_KNIGHT = 3
    const val TYPE_PROMOTION_BISHOP = 4
    const val TYPE_PROMOTION_ROOK = 5
    const val TYPE_PROMOTION_QUEEN = 6
    const val TYPE_PROMOTION_KING = 7

    const val SIZE = 8

    private val CHARACTER = charArrayOf('-', '-', '-', 'n', 'b', 'r', 'q', 'k')

    /**
     * Get promotion piece.
     *
     * @param moveType move type
     * @return promotion piece
     */
    fun getPromotedPiece(moveType: Int): Int {
        return if (moveType >= TYPE_PROMOTION_KNIGHT) {
            moveType - 1
        } else {
            Piece.NONE
        }
    }

    /**
     * Check if move type is a promotion.
     *
     * @param moveType move type
     * @return is promotion
     */
    fun isPromotion(moveType: Int): Boolean {
        return moveType >= MoveType.TYPE_PROMOTION_KNIGHT
    }

    /**
     * Check if move type is castling.
     *
     * @param moveType move type
     * @return is castling
     */
    fun isCastling(moveType: Int): Boolean {
        return moveType == TYPE_CASTLING
    }

    fun isValid(moveType: Int): Boolean {
        return moveType in TYPE_NORMAL..TYPE_PROMOTION_KING
    }

    fun getMoveType(token: Char): Int {
        return CHARACTER.indexOf(token)
    }
}
