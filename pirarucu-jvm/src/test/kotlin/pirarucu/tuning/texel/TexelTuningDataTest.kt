package pirarucu.tuning.texel

import java.util.Arrays
import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TexelTuningDataTest {

    @Test
    fun testGenerateGenes() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = TexelTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 10)
        val wantedBitset = BitSet()
        wantedBitset.set(1)
        wantedBitset.set(3)
        assertEquals(wantedBitset, tuningData.updateGenes(BitSet()))
        assertTrue(tuningData.hasNext())
    }

    @Test
    fun testUpperBounds() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = TexelTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 5)
        assertTrue(tuningData.hasNext())
        tuningData.next()
        val wantedList = intArrayOf(0, 15, 0)
        assertTrue(wantedList contentEquals elementList)
        tuningData.reset()
        assertTrue(tuningData.hasNext())
        tuningData.next()
        val wantedList2 = intArrayOf(0, 5, 0)
        assertTrue(wantedList2 contentEquals elementList)
    }

    @Test
    fun testLowerBounds() {
        val bits = 4
        val elementList = intArrayOf(0, 0, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = TexelTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 20)
        assertFalse(tuningData.hasNext())
    }
}