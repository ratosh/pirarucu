package pirarucu.move

import pirarucu.board.Piece
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveTypeTest {
    @Test
    fun testPromotedPiece() {
        assertEquals(MoveType.getPromotedPiece(MoveType.TYPE_PROMOTION_QUEEN), Piece.QUEEN)
    }

    @Test
    fun testPromotion() {
        assertTrue(MoveType.isPromotion(MoveType.TYPE_PROMOTION_KNIGHT))
    }

    @Test
    fun testCastling() {
        assertTrue(MoveType.isCastling(MoveType.TYPE_CASTLING))
    }

    @Test
    fun testValid() {
        assertTrue(MoveType.isValid(MoveType.TYPE_NORMAL))
        assertTrue(MoveType.isValid(MoveType.TYPE_PASSANT))
        assertTrue(MoveType.isValid(MoveType.TYPE_CASTLING))
        assertTrue(MoveType.isValid(MoveType.TYPE_PROMOTION_ROOK))
        assertFalse(MoveType.isValid(MoveType.SIZE))
        assertFalse(MoveType.isValid(-1))
    }

    @Test
    fun testGetMoveType() {
        assertEquals(MoveType.getMoveType('-'), MoveType.TYPE_NORMAL)
        assertEquals(MoveType.getMoveType('n'), MoveType.TYPE_PROMOTION_KNIGHT)
        assertEquals(MoveType.getMoveType('b'), MoveType.TYPE_PROMOTION_BISHOP)
    }
}