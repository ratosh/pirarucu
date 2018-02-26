package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitboardTest {

    @Test
    fun testMirrorVertical() {
        for (square in Square.A1 until Square.SIZE) {
            assertEquals(Bitboard.mirrorVertical(Bitboard.getBitboard(square)),
                Bitboard.getBitboard(Square.invertSquare(square)))
        }
    }

    @Test
    fun testNeighbours() {
        for (square in Square.A1 until Square.SIZE) {
            val file = File.getFile(square)
            val bitboard = Bitboard.getBitboard(square)
            val neighbours = Bitboard.getNeighbours(bitboard)
            if (file > File.FILE_A) {
                val leftNeighbour = bitboard ushr 1
                assertEquals(neighbours and leftNeighbour, leftNeighbour)
            }
            if (file < File.FILE_H) {
                val rightNeighbour = bitboard shl 1
                assertEquals(neighbours and rightNeighbour, rightNeighbour)
            }
        }
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

    @Test
    fun testBitCount() {
        for (square in Square.A1 until Square.SIZE) {
            val bitboard = Bitboard.getBitboard(square)
            assertEquals(Bitboard.bitCount(bitboard), 1)
            val garbageBitboard = bitboard or Bitboard.getBitboard((square + 1) % Square.SIZE)
            assertEquals(Bitboard.bitCount(garbageBitboard), 2)
        }
        assertEquals(Bitboard.bitCount(Bitboard.ALL), 64)
    }
}

