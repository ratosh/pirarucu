package pirarucu.tuning.pbil

import java.security.SecureRandom
import java.util.Arrays
import java.util.BitSet

class PbilGenerator(private val bitsPerValue: IntArray,
    private val totalBits: Int,
    private val allowNegatives: Boolean,
    vararg ignoreElementList: Int) {

    private val random: SecureRandom = SecureRandom()

    private val probability: DoubleArray

    private val ignoreElementList: IntArray = ignoreElementList

    init {
        Arrays.sort(this.ignoreElementList)
        probability = DoubleArray(totalBits)
        Arrays.fill(probability, 0.5)
    }

    fun generateGenes(): BitSet {
        val genes = BitSet()
        for (i in 0 until totalBits) {
            if (random.nextDouble() < probability[i]) {
                genes.set(i)
            }
        }
        return genes
    }

    fun generateElements(genes: BitSet): IntArray {
        val elementList = IntArray(bitsPerValue.size)
        for (i in 0 until totalBits) {
            val position = elementWithBit(i)
            if (Arrays.binarySearch(ignoreElementList, position) < 0 && genes.get(i)) {
                if (allowNegatives && isSignalBit(i)) {
                    elementList[position] *= -1
                } else {
                    elementList[position] = elementList[position] or (1 shl elementBit(i))
                }
            }
        }
        return elementList
    }

    private fun elementWithBit(value: Int): Int {
        var result = -1
        var tmpValue = value
        while (tmpValue >= 0) {
            result++
            tmpValue -= bitsPerValue[result]
        }
        return result
    }

    private fun elementBit(value: Int): Int {
        var result = 0
        var tmpValue = value
        while (tmpValue >= bitsPerValue[result]) {
            tmpValue -= bitsPerValue[result]
            result++
        }
        return tmpValue
    }

    private fun isSignalBit(value: Int): Boolean {
        var result = 0
        var tmpValue = value
        while (tmpValue >= bitsPerValue[result]) {
            tmpValue -= bitsPerValue[result]
            result++
        }
        return tmpValue == bitsPerValue[result] - 1
    }

    fun reportResult(bestGenes: BitSet, worstGenes: BitSet) {
        // Update the probability vector with max and min cost genes
        for (i in 0 until totalBits) {
            if (bestGenes.get(i) == worstGenes.get(i)) {
                probability[i] = probability[i] * (1.0 - LEARN_RATE) + (if (bestGenes.get(i)) 1.0 else 0.0) * LEARN_RATE
            } else {
                val learnRate2 = LEARN_RATE + NEG_LEARN_RATE
                probability[i] = probability[i] * (1.0 - learnRate2) + (if (bestGenes.get(i)) 1.0 else 0.0) * learnRate2
            }
        }

        // Mutation
        for (j in 0 until totalBits) {
            if (random.nextDouble() < MUTATION_PROBABILITY) {
                probability[j] = probability[j] * (1.0 - MUTATION_PROBABILITY_SHIFT) + (if (random.nextBoolean()) 1.0 else 0.0) * MUTATION_PROBABILITY_SHIFT
            }
        }
    }

    companion object {

        private val LEARN_RATE = 0.1

        private val NEG_LEARN_RATE = 0.075

        private val MUTATION_PROBABILITY = 0.02

        private val MUTATION_PROBABILITY_SHIFT = 0.05
    }
}
