package pirarucu.tuning.pbil

import java.util.Arrays
import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertTrue

class PbilGeneratorTest {

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
}