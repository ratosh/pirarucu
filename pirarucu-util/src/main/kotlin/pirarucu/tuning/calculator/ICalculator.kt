package pirarucu.tuning.calculator

import pirarucu.util.epd.EpdInfo

interface ICalculator {
     fun calculate(list: List<EpdInfo>) : Double

    fun computeConstant(list: List<EpdInfo>)
}