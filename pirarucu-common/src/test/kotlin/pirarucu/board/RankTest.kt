package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RankTest {

    @Test
    fun testGetRank() {
        assertEquals(Rank.getRank(Square.A1), Rank.RANK_1)
        assertEquals(Rank.getRank(Square.B2), Rank.RANK_2)
        assertEquals(Rank.getRank(Square.C3), Rank.RANK_3)
        assertEquals(Rank.getRank(Square.D4), Rank.RANK_4)
        assertEquals(Rank.getRank(Square.E5), Rank.RANK_5)
        assertEquals(Rank.getRank(Square.F6), Rank.RANK_6)
        assertEquals(Rank.getRank(Square.G7), Rank.RANK_7)
        assertEquals(Rank.getRank(Square.H8), Rank.RANK_8)

        assertEquals(Rank.getRank('1'), Rank.RANK_1)
        assertEquals(Rank.getRank('2'), Rank.RANK_2)
        assertEquals(Rank.getRank('3'), Rank.RANK_3)
        assertEquals(Rank.getRank('4'), Rank.RANK_4)
        assertEquals(Rank.getRank('5'), Rank.RANK_5)
        assertEquals(Rank.getRank('6'), Rank.RANK_6)
        assertEquals(Rank.getRank('7'), Rank.RANK_7)
        assertEquals(Rank.getRank('8'), Rank.RANK_8)
    }

    @Test
    fun testGetRelativeRank() {
        assertEquals(Rank.getRelativeRank(Color.WHITE, Rank.RANK_2), Rank.RANK_2)
        assertEquals(Rank.getRelativeRank(Color.WHITE, Rank.RANK_7), Rank.RANK_7)
        assertEquals(Rank.getRelativeRank(Color.BLACK, Rank.RANK_1), Rank.RANK_8)
        assertEquals(Rank.getRelativeRank(Color.BLACK, Rank.RANK_8), Rank.RANK_1)
    }

    @Test
    fun testInvertRank() {
        assertEquals(Rank.invertRank(Rank.RANK_1), Rank.RANK_8)
        assertEquals(Rank.invertRank(Rank.RANK_8), Rank.RANK_1)
    }

    @Test
    fun testIsValidPiece() {
        assertTrue(Rank.isValid(Rank.RANK_1))
        assertTrue(Rank.isValid(Rank.RANK_8))
        assertFalse(Rank.isValid(Rank.SIZE))
        assertFalse(Rank.isValid(-1))
    }
}