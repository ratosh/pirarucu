package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SquareTest {

    @Test
    fun testGetSquare() {
        assertEquals(Square.getSquare(Bitboard.A1), Square.A1)
        assertEquals(Square.getSquare(Bitboard.B3), Square.B3)
        assertEquals(Square.getSquare(Bitboard.C5), Square.C5)
        assertEquals(Square.getSquare(Bitboard.E6), Square.E6)
        assertEquals(Square.getSquare(Bitboard.G2), Square.G2)

        assertEquals(Square.getSquare(File.FILE_A, Rank.RANK_1), Square.A1)
        assertEquals(Square.getSquare(File.FILE_B, Rank.RANK_2), Square.B2)
        assertEquals(Square.getSquare(File.FILE_D, Rank.RANK_4), Square.D4)
        assertEquals(Square.getSquare(File.FILE_G, Rank.RANK_6), Square.G6)

        assertEquals(Square.getSquare("a1"), Square.A1)
        assertEquals(Square.getSquare("b2"), Square.B2)
        assertEquals(Square.getSquare("d4"), Square.D4)
        assertEquals(Square.getSquare("g6"), Square.G6)

        assertEquals(Square.getSquare("g6", "g5")[0], Square.G6)
    }

    @Test
    fun testIsSameColor() {
        assertTrue(Square.isSameSquareColor(Square.A1, Square.B2))
        assertFalse(Square.isSameSquareColor(Square.A1, Square.A2))
    }

    @Test
    fun testInvertSquare() {
        assertEquals(Square.invertSquare(Square.A1), Square.A8)
        assertEquals(Square.invertSquare(Square.A8), Square.A1)
    }

    @Test
    fun testGetRelativeSquare() {
        assertEquals(Square.getRelativeSquare(Color.WHITE, Square.A1), Square.A1)
        assertEquals(Square.getRelativeSquare(Color.BLACK, Square.A1), Square.A8)
    }

    @Test
    fun testToString() {
        assertEquals(Square.toString(Square.A1), "a1")
        assertEquals(Square.toString(Square.A8), "a8")

        assertEquals(Square.toString(Bitboard.A1 or Bitboard.A8), "a1a8")
    }

    @Test
    fun testIsValid() {
        assertTrue(Square.isValid(Square.A1))
        assertTrue(Square.isValid(Square.H8))
        assertFalse(Square.isValid(Square.SIZE))
    }
}