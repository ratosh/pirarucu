package pirarucu.tuning.sts

import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StsTuningDataTest {

    @Test
    fun testGenerateGenes() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = StsTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 10)
        val wantedBitset = BitSet()
        wantedBitset.set(1)
        wantedBitset.set(3)
        assertEquals(wantedBitset, tuningData.updateGenes(BitSet()))
        assertTrue(tuningData.hasNext())
    }

    @Test
    fun testSkip() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = StsTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 1)
        assertTrue(tuningData.hasNext())
        tuningData.next()
        println(tuningData.getElementString())
    }

    @Test
    fun testPositiveBounds() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = StsTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 14)
        assertTrue(tuningData.hasNext())
        tuningData.next()
        println(tuningData.getElementString())
        val wantedList = intArrayOf(0, 3, 0)
        assertTrue(wantedList contentEquals elementList)
    }

    @Test
    fun testNegativeBounds() {
        val bits = 4
        val elementList = intArrayOf(0, -10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val tuningData = StsTuningData("Test", elementList, bitsPerValue, true, ignoreElementList, 14)
        assertTrue(tuningData.hasNext())
        tuningData.next()
        println(tuningData.getElementString())
        val wantedList = intArrayOf(0, -3, 0)
        assertTrue(wantedList contentEquals elementList)
    }
}