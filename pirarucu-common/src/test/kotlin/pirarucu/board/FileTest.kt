package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileTest {

    @Test
    fun testGetFile() {
        assertEquals(File.getFile(Square.A1), File.FILE_A)
        assertEquals(File.getFile(Square.B2), File.FILE_B)
        assertEquals(File.getFile(Square.C3), File.FILE_C)
        assertEquals(File.getFile(Square.D4), File.FILE_D)
        assertEquals(File.getFile(Square.E3), File.FILE_E)
        assertEquals(File.getFile(Square.F2), File.FILE_F)
        assertEquals(File.getFile(Square.G1), File.FILE_G)
        assertEquals(File.getFile(Square.H8), File.FILE_H)

        assertEquals(File.getFile('a'), File.FILE_A)
        assertEquals(File.getFile('b'), File.FILE_B)
        assertEquals(File.getFile('C'), File.FILE_C)
        assertEquals(File.getFile('D'), File.FILE_D)
        assertEquals(File.getFile('e'), File.FILE_E)
        assertEquals(File.getFile('f'), File.FILE_F)
        assertEquals(File.getFile('G'), File.FILE_G)
        assertEquals(File.getFile('H'), File.FILE_H)
    }

    @Test
    fun testIsValid() {
        assertTrue(File.isValid(File.FILE_A))
        assertTrue(File.isValid(File.FILE_B))
        assertTrue(File.isValid(File.FILE_C))
        assertTrue(File.isValid(File.FILE_D))
        assertTrue(File.isValid(File.FILE_E))
        assertTrue(File.isValid(File.FILE_F))
        assertTrue(File.isValid(File.FILE_G))
        assertTrue(File.isValid(File.FILE_H))
        assertFalse(File.isValid(File.SIZE))
        assertFalse(File.isValid(File.INVALID))
    }

    @Test
    fun testToString() {
        assertEquals(File.toString(File.FILE_A), 'a')
        assertEquals(File.toString(File.FILE_B), 'b')
        assertEquals(File.toString(File.FILE_C), 'c')
        assertEquals(File.toString(File.FILE_D), 'd')
        assertEquals(File.toString(File.FILE_E), 'e')
        assertEquals(File.toString(File.FILE_F), 'f')
        assertEquals(File.toString(File.FILE_G), 'g')
        assertEquals(File.toString(File.FILE_H), 'h')
    }
}