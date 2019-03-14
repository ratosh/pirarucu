package pirarucu.tuning

import pirarucu.util.epd.EpdInfo

object ErrorUtil {

    const val ORIGINAL_CONSTANT = 1.4

    fun calculateSigmoid(score: Int, constant: Double = ORIGINAL_CONSTANT): Double {
        return 1 / (1 + Math.pow(10.0, -constant * score / 400))
    }

    fun calculate(list: List<EpdInfo>, constant: Double = ORIGINAL_CONSTANT): Double {
        var result = 0.0
        for (entry in list) {
            result += Math.pow(entry.result - ErrorUtil.calculateSigmoid(entry.eval, constant), 2.0)
        }

        return result / list.size

    }

    fun biggestError(list: List<EpdInfo>, size: Int = 10, constant: Double = ORIGINAL_CONSTANT): Map<EpdInfo, Double> {
        val result = mutableMapOf<EpdInfo, Double>()
        var smallestError = 1.0
        var smallestEntry: EpdInfo? = null
        for (entry in list) {
            val entryError = Math.pow(entry.result - ErrorUtil.calculateSigmoid(entry.eval, constant), 2.0)
            if (result.size < size) {
                result[entry] = entryError
                if (smallestError > entryError) {
                    smallestError = entryError
                    smallestEntry = entry
                }
            } else if (entryError > smallestError) {
                result.remove(smallestEntry)

                smallestError = entryError
                smallestEntry = entry

                result.forEach {
                    if (it.value < smallestError) {
                        smallestError = it.value
                        smallestEntry = it.key
                    }
                }
                result[entry] = entryError
            }
        }

        return result
    }
}
