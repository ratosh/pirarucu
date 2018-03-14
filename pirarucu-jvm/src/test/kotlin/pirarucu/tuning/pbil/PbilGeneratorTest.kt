package pirarucu.tuning.pbil

import java.util.Arrays
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PbilGeneratorTest {

    @Test
    fun testGenerateGenes() {
        val testElements = intArrayOf(1, 8, 9, 9, 9, 10, 11, 1)
        val generator = PbilGenerator(
            testElements,
            testElements.sum(),
            false, 0, 7)
        for (i in 1..1000) {
            val genes = generator.generateGenes()
            val elements = generator.generateElements(genes)
            println("elements " + Arrays.toString(elements))
            assertEquals(elements[0], 0)
            assertTrue(elements[1] < 1 shl testElements[1])
            assertTrue(elements[2] < 1 shl testElements[2])
            assertTrue(elements[3] < 1 shl testElements[3])
            assertTrue(elements[4] < 1 shl testElements[4])
            assertTrue(elements[5] < 1 shl testElements[5])
            assertTrue(elements[6] < 1 shl testElements[6])
            assertEquals(elements[7], 0)
        }
    }
}