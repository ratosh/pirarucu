package pirarucu.tuning.calculator

import pirarucu.util.epd.EpdInfo

class NodeCountCalculator : ICalculator {
    override fun calculate(list: List<EpdInfo>): Double {
        var result = 0L
        list.forEach { result += it.nodes }
        return result.toDouble()
    }

    override fun computeConstant(list: List<EpdInfo>) {
        // No constant on this one
    }
}