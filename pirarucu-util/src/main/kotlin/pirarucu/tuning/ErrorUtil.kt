package pirarucu.tuning

import pirarucu.util.epd.EpdInfo

object ErrorUtil {

    const val ORIGINAL_CONSTANT = 1.4

    fun calculateSigmoid(score: Int, constant: Double): Double {
        return 1 / (1 + Math.pow(10.0, -constant * score / 400))
    }

    fun calculate(list: List<EpdInfo>, constant: Double): Double {
        var result = 0.0
        for (entry in list) {
            result += Math.pow(entry.result - ErrorUtil.calculateSigmoid(entry.eval, constant), 2.0)
        }

        return result / list.size

    }
}
