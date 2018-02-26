package pirarucu.board

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {

    @Test
    fun testInvertColor() {
        assertEquals(Color.invertColor(Color.WHITE), Color.BLACK)
        assertEquals(Color.invertColor(Color.BLACK), Color.WHITE)
    }

    @Test
    fun testGetColor() {
        assertEquals(Color.getColor('w'), Color.WHITE)
        assertEquals(Color.getColor('b'), Color.BLACK)
        assertEquals(Color.getColor('k'), Color.INVALID)
    }

    @Test
    fun testToString() {
        assertEquals(Color.toString(Color.WHITE), 'w')
        assertEquals(Color.toString(Color.BLACK), 'b')
    }
}