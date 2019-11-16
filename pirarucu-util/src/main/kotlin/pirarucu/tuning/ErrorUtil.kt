package pirarucu.tuning

import pirarucu.util.epd.EpdInfo


object ErrorUtil {

    const val ORIGINAL_CONSTANT = 1.4

    fun calculateSigmoid(score: Int, constant: Double = ORIGINAL_CONSTANT) =
            1 / (1 + Math.pow(10.0, -constant * score / 400))

    fun calculate(list: List<EpdInfo>, constant: Double = ORIGINAL_CONSTANT): Double {
        setError(list, constant)
        return list.sumByDouble { it.error } / list.size
    }

    fun setError(list: List<EpdInfo>, constant: Double = ORIGINAL_CONSTANT) =
            list.forEach { it.error = calculateError(it, constant) }

    private fun calculateError(entry: EpdInfo, constant: Double) =
            Math.pow(entry.result - calculateSigmoid(entry.eval, constant), 2.0)

    fun bestConstant(list: List<EpdInfo>): Double {
        var currentConstant = ORIGINAL_CONSTANT
        var constantRange = 1.0

        while (constantRange > 0.00001) {
            val constant1 = currentConstant - constantRange
            val error1 = calculate(list, constant1)
            val constant2 = currentConstant + constantRange
            val error2 = calculate(list, constant2)
            if (error1 < error2) {
                currentConstant -= constantRange / 2
            } else if (error2 < error1) {
                currentConstant += constantRange / 2
            }
            constantRange /= 2
        }

        return currentConstant
    }
}