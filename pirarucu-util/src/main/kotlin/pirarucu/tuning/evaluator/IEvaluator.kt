package pirarucu.tuning.evaluator

import pirarucu.util.epd.EpdInfo

interface IEvaluator {

    fun evaluate(list: List<EpdInfo>)

}