package pirarucu.tuning.pbil

import java.util.BitSet
import kotlin.test.Test
import kotlin.test.assertEquals

class PbilGeneratorTest {

    @Test
    fun testGenerateGenes() {
        val totalBits = 100
        val generator = PbilGenerator()
        val bestBitSet = BitSet(totalBits)
        bestBitSet.set(0, totalBits, true)
        val worstBitSet = BitSet(totalBits)
        worstBitSet.set(0, totalBits, false)
        generator.totalBits = totalBits
        for (i in 0 until 100000) {
            generator.reportResult(bestBitSet, bestBitSet)
            if (generator.isOptimized()) {
                println("optimized in $i")
                break
            }
        }

        assertEquals(bestBitSet, generator.generateGenes())
    }

}