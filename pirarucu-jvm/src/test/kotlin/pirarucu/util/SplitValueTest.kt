package pirarucu.util

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitValueTest {

    @Test
    fun mergeParts1() {
        val value = SplitValue.mergeParts(-19, 13)
        assertEquals(-19, SplitValue.getFirstPart(value))
        assertEquals(13, SplitValue.getSecondPart(value))
    }

    @Test
    fun mergeParts2() {
        val value = SplitValue.mergeParts(13, -19)
        assertEquals(13, SplitValue.getFirstPart(value))
        assertEquals(-19, SplitValue.getSecondPart(value))
    }

    @Test
    fun mergeParts3() {
        val value = SplitValue.mergeParts(0, 0)
        assertEquals(0, SplitValue.getFirstPart(value))
        assertEquals(0, SplitValue.getSecondPart(value))
    }

    @Test
    fun add() {
        val value = SplitValue.mergeParts(12, 38) + SplitValue.mergeParts(9, 67)
        assertEquals(21, SplitValue.getFirstPart(value))
        assertEquals(105, SplitValue.getSecondPart(value))
    }

    @Test
    fun subFromNegative() {
        val value = SplitValue.mergeParts(-8, -8) - SplitValue.mergeParts(8, 8)
        assertEquals(-16, SplitValue.getFirstPart(value))
        assertEquals(-16, SplitValue.getSecondPart(value))
    }

    @Test
    fun subSwitchToNegative() {
        val value = SplitValue.mergeParts(3, 5) - SplitValue.mergeParts(7, 11)
        assertEquals(-4, SplitValue.getFirstPart(value))
        assertEquals(-6, SplitValue.getSecondPart(value))
    }

    @Test
    fun mutiplyPositive() {
        val value = 5 * SplitValue.mergeParts(3, 7)
        assertEquals(15, SplitValue.getFirstPart(value))
        assertEquals(35, SplitValue.getSecondPart(value))
    }

    @Test
    fun mutiplyNegative() {
        val value = -5 * SplitValue.mergeParts(3, 7)
        assertEquals(-15, SplitValue.getFirstPart(value))
        assertEquals(-35, SplitValue.getSecondPart(value))
    }

    @Test
    fun firstPartZero() {
        val value = SplitValue.mergeParts(0, -5)
        assertEquals(0, SplitValue.getFirstPart(value))
        assertEquals(-5, SplitValue.getSecondPart(value))
    }

    @Test
    fun secondPartZero() {
        val value = SplitValue.mergeParts(-5, 0)
        assertEquals(-5, SplitValue.getFirstPart(value))
        assertEquals(0, SplitValue.getSecondPart(value))
    }
}