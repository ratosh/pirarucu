package pirarucu.tuning.calculator

import pirarucu.util.epd.EpdInfo

class MoveScoreCalculator : ICalculator {

    override fun calculate(list: List<EpdInfo>) =
        1 - (list.sumByDouble { Math.pow(it.moveScore.toDouble(), 2.0) } / list.size / Companion.MAX_SCORE)

    override fun computeConstant(list: List<EpdInfo>) {
        // No constant on this one
    }

    companion object {
        private const val MAX_SCORE = 1000
    }
}