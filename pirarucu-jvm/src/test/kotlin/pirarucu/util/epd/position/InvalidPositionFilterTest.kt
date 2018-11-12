package pirarucu.util.epd.position

import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.factory.EpdInfoFactory
import kotlin.test.Test
import kotlin.test.assertTrue

class InvalidPositionFilterTest {

    @Test
    fun emptyFilterTest() {
        val list = mutableListOf<EpdInfo>()
        list.add(EpdInfoFactory.getEpdInfo("4r1k1/1bp1bpQp/1p2q3/3p4/1r6/4P1P1/PB3PBP/R2R2K1 b - - c9 \"1-0\";"))

        val filter = InvalidPositionFilter(2)
        assertTrue(filter.filter(list).isEmpty())
    }

    @Test
    fun notEmptyFilterTest() {
        val list = mutableListOf<EpdInfo>()
        list.add(EpdInfoFactory.getEpdInfo("rnb1kbnr/pp1pppp1/7p/2q5/5P2/N1P1P3/P2P2PP/R1BQKBNR w KQkq - c9 \"1/2-1/2\";"))

        val filter = InvalidPositionFilter(1)
        assertTrue(filter.filter(list).isNotEmpty())
    }

}