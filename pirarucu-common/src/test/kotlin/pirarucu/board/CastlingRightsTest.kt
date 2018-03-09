package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals

class CastlingRightsTest {
    @Test
    fun testGetCastlingRights() {
        assertEquals(CastlingRights.getCastlingRight('A'), CastlingRights.NO_CASTLING)
        assertEquals(CastlingRights.getCastlingRight('-'), CastlingRights.NO_CASTLING)
        assertEquals(CastlingRights.getCastlingRight('K'), CastlingRights.WHITE_OO)
        assertEquals(CastlingRights.getCastlingRight('Q'), CastlingRights.WHITE_OOO)
        assertEquals(CastlingRights.getCastlingRight('k'), CastlingRights.BLACK_OO)
        assertEquals(CastlingRights.getCastlingRight('q'), CastlingRights.BLACK_OOO)
        assertEquals(CastlingRights.getCastlingRight("KQkq"), CastlingRights.ANY_CASTLING)

        assertEquals(CastlingRights.getCastlingRight(CastlingRights.CASTLING_BITS),
            CastlingRights.NO_CASTLING)
        assertEquals(CastlingRights.getCastlingRight(-1), CastlingRights.NO_CASTLING)
        assertEquals(CastlingRights.getCastlingRight(0), CastlingRights.WHITE_OO)
        assertEquals(CastlingRights.getCastlingRight(1), CastlingRights.WHITE_OOO)
        assertEquals(CastlingRights.getCastlingRight(2), CastlingRights.BLACK_OO)
        assertEquals(CastlingRights.getCastlingRight(3), CastlingRights.BLACK_OOO)
    }

    @Test
    fun testGetCastlingRightIndex() {
        assertEquals(CastlingRights.getCastlingRightIndex(Color.WHITE, CastlingRights.KING_SIDE),
            CastlingRights.WHITE_KING_CASTLING_INDEX)
        assertEquals(CastlingRights.getCastlingRightIndex(Color.WHITE, CastlingRights.QUEEN_SIDE),
            CastlingRights.WHITE_QUEEN_CASTLING_INDEX)
        assertEquals(CastlingRights.getCastlingRightIndex(Color.BLACK, CastlingRights.KING_SIDE),
            CastlingRights.BLACK_KING_CASTLING_INDEX)
        assertEquals(CastlingRights.getCastlingRightIndex(Color.BLACK, CastlingRights.QUEEN_SIDE),
            CastlingRights.BLACK_QUEEN_CASTLING_INDEX)
    }

    @Test
    fun testFilterCastlingRight() {
        assertEquals(CastlingRights.filterCastlingRight(Color.WHITE, CastlingRights.ANY_CASTLING),
            CastlingRights.WHITE_CASTLING_RIGHTS)
        assertEquals(CastlingRights.filterCastlingRight(Color.BLACK, CastlingRights.ANY_CASTLING),
            CastlingRights.BLACK_CASTLING_RIGHTS)
    }

    @Test
    fun testToString() {
        assertEquals(CastlingRights.toString(CastlingRights.ANY_CASTLING), "KQkq")
        assertEquals(CastlingRights.toString(CastlingRights.WHITE_CASTLING_RIGHTS), "KQ")
        assertEquals(CastlingRights.toString(CastlingRights.BLACK_CASTLING_RIGHTS), "kq")
        assertEquals(CastlingRights.toString(CastlingRights.NO_CASTLING), "-")
    }
}