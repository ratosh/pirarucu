package pirarucu.tuning.pbil

import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertEquals

class PbilTuningDataTest {

    @Test
    fun testGenerateGenes() {
        val totalBits = 10
        val generator = PbilGenerator()
        val bestBitSet = BitSet(totalBits)
        bestBitSet.set(0, totalBits, true)
        val worstBitSet = BitSet(totalBits)
        worstBitSet.set(0, totalBits, false)
        generator.totalBits = totalBits
        for (i in 0 until 10000) {
            generator.reportResult(bestBitSet, worstBitSet)
        }

        assertEquals(bestBitSet, generator.generateGenes())
    }
}