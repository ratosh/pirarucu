package pirarucu.tuning.calculator

import pirarucu.tuning.ErrorUtil
import pirarucu.util.epd.EpdInfo

class ResultErrorCalculator : ICalculator {
    var constant = ErrorUtil.ORIGINAL_CONSTANT
    override fun calculate(list: List<EpdInfo>) = ErrorUtil.calculate(list, constant)

    override fun computeConstant(list: List<EpdInfo>) {
        constant = ErrorUtil.bestConstant(list)
    }
}