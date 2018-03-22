package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PieceTest {

    @Test
    fun testGetPiece() {
        assertEquals(Piece.getPiece('-'), Piece.NONE)
        assertEquals(Piece.getPiece('p'), Piece.PAWN)
        assertEquals(Piece.getPiece('n'), Piece.KNIGHT)
        assertEquals(Piece.getPiece('b'), Piece.BISHOP)
        assertEquals(Piece.getPiece('R'), Piece.ROOK)
        assertEquals(Piece.getPiece('Q'), Piece.QUEEN)
        assertEquals(Piece.getPiece('K'), Piece.KING)
    }

    @Test
    fun testGetPieceColor() {
        assertEquals(Piece.getPieceColor('K'), Color.WHITE)
        assertEquals(Piece.getPieceColor('k'), Color.BLACK)
    }

    @Test
    fun testToString() {
        assertEquals(Piece.toString(Piece.PAWN), 'p')
        assertEquals(Piece.toString(Piece.KNIGHT), 'n')
        assertEquals(Piece.toString(Piece.BISHOP), 'b')
        assertEquals(Piece.toString(Piece.ROOK), 'r')
        assertEquals(Piece.toString(Piece.QUEEN), 'q')
        assertEquals(Piece.toString(Piece.KING), 'k')

        assertEquals(Piece.toString(Color.WHITE, Piece.PAWN), 'P')
        assertEquals(Piece.toString(Color.WHITE, Piece.KNIGHT), 'N')
    }

    @Test
    fun testIsValidPiece() {
        assertTrue(Piece.isValidPiece(Piece.PAWN))
        assertFalse(Piece.isValidPiece(Piece.NONE))
        assertTrue(Piece.isValid(Piece.PAWN))
        assertTrue(Piece.isValid(Piece.NONE))
    }
}