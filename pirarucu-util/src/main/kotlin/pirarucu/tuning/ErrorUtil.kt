package pirarucu.tuning

import pirarucu.util.epd.EpdInfo


object ErrorUtil {

    const val ORIGINAL_CONSTANT = 1.4

    fun calculateSigmoid(score: Int, constant: Double = ORIGINAL_CONSTANT): Double {
        return 1 / (1 + Math.pow(10.0, -constant * score / 400))
    }

    fun calculate(list: List<EpdInfo>, constant: Double = ORIGINAL_CONSTANT): Double {
        setError(list, constant)
        return list.sumByDouble { it.error } / list.size
    }

    fun setError(list: List<EpdInfo>, constant: Double = ORIGINAL_CONSTANT) {
        list.forEach { it.error = calculateError(it, constant) }
    }

    private fun calculateError(entry: EpdInfo, constant: Double) =
        Math.pow(entry.result - calculateSigmoid(entry.eval, constant), 2.0)
}