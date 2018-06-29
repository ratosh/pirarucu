package pirarucu.tuning.pbil

import java.util.Arrays
import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertTrue

class PbilTuningDataTest {

    @Test
    fun testGenerateGenes() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val pbilTuningData = PbilTuningData("Test", elementList, bitsPerValue, false, ignoreElementList)
        val bitSet = BitSet(bits)
        bitSet.set(0, bits, true)
        pbilTuningData.generateElements(bitSet)
        var wantedElementList = intArrayOf(0, 15, 0)

        assertTrue(elementList contentEquals wantedElementList)

        pbilTuningData.geneIndex = 1
        pbilTuningData.generateElements(bitSet)
        wantedElementList = intArrayOf(0, 7, 0)

        println(Arrays.toString(elementList))
        println(Arrays.toString(wantedElementList))
        assertTrue(elementList contentEquals wantedElementList)
    }

    @Test
    fun testEveryBitMather() {
        val bits = 4
        val elementList = intArrayOf(0, 10, 0)
        val bitsPerValue = intArrayOf(0, bits, 0)
        val ignoreElementList = intArrayOf(0, 2)
        val pbilTuningData = PbilTuningData("Test", elementList, bitsPerValue, false, ignoreElementList)
        val bitSet = BitSet(bits)
        for (index in 0 until bits) {
            bitSet.set(index, true)
            pbilTuningData.generateElements(bitSet)
            assertTrue(elementList contentEquals intArrayOf(0, 1 shl index, 0))
            bitSet.set(index, false)
        }
    }

    @Test
    fun testNegatives() {
        val bits = 4
        val usedBits = 5
        val elementList = intArrayOf(0, 10, 10)
        val bitsPerValue = intArrayOf(bits, bits, bits)
        val ignoreElementList = intArrayOf(0)
        val pbilTuningData = PbilTuningData("Test", elementList, bitsPerValue, true, ignoreElementList)
        val bitSet = BitSet(bits)

        bitSet.set(4, true)
        bitSet.set(9, true)
        for (index in 0 until bits) {
            bitSet.set(index, true)
            pbilTuningData.generateElements(bitSet)
            assertTrue(elementList contentEquals intArrayOf(0, -(1 shl index), 0))
            bitSet.set(index, false)
        }
        for (index in 0 until bits) {
            bitSet.set(usedBits + index, true)
            pbilTuningData.generateElements(bitSet)
            assertTrue(elementList contentEquals intArrayOf(0, 0, -(1 shl index)))
            bitSet.set(usedBits + index, false)
        }
    }
}