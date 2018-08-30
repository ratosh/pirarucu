package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitboardTest {

    @Test
    fun testGetBitboard() {
        for (square in Square.A1 until Square.SIZE) {
            val bitboard = Bitboard.getBitboard(square)
            assertEquals(bitboard, 1L shl square)
        }
        println(1L shl 63)
    }

    @Test
    fun testOneElmeent() {
        for (square in Square.A1 until Square.SIZE) {
            val bitboard = Bitboard.getBitboard(square)
            assertTrue(Bitboard.oneElement(bitboard))
            val garbageBitboard = bitboard or Bitboard.getBitboard((square + 1) % Square.SIZE)
            assertFalse(Bitboard.oneElement(garbageBitboard))
        }
        assertFalse(Bitboard.oneElement(Bitboard.ALL))
    }
}

