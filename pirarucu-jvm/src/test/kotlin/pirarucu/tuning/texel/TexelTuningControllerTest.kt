package pirarucu.tuning.texel

import kotlin.test.Test
import kotlin.test.assertTrue

class TexelTuningControllerTest {

    @Test
    fun testDataLoop() {
        val controller = TexelTuningController()
        val elementList = intArrayOf(0, 10, 10)
        val bitsPerValue = intArrayOf(0, 4, 4)
        val ignoreElementList = intArrayOf(0)
        val tuningData = TexelTuningData("Test", elementList, bitsPerValue, false, ignoreElementList, 1)
        controller.registerTuningData(tuningData)
        while (controller.hasNext()) {
            assertTrue(controller.next())
        }

    }

}