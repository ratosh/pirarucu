package pirarucu.tuning.pbil

import java.security.SecureRandom
import java.util.Arrays
import java.util.BitSet

class PbilGenerator {

    private val random = SecureRandom()

    private var probability = DoubleArray(0)

    var totalBits: Int
        get() = probability.size
        set(value) {
            probability = DoubleArray(value)
            Arrays.fill(probability, 0.5)
        }

    fun generateGenes(): BitSet {
        val genes = BitSet(totalBits)
        for (i in 0 until totalBits) {
            if (random.nextDouble() < probability[i]) {
                genes.set(i)
            }
        }
        return genes
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
                probability[j] = probability[j] * (1.0 - MUTATION_PROBABILITY_SHIFT) + (if (probability[j] > 0.5) 0.0 else MUTATION_PROBABILITY_SHIFT)
            }
        }
        println("Current probability " + Arrays.toString(probability))
    }

    fun isOptimized(): Boolean {
        for (entry in probability) {
            if (entry > WANTED_DIVERGENCE && entry < 1 - WANTED_DIVERGENCE) {
                return false
            }
        }
        return true
    }

    companion object {

        private const val LEARN_RATE = 0.1

        private const val NEG_LEARN_RATE = 0.075

        private const val MUTATION_PROBABILITY = 0.01

        private const val MUTATION_PROBABILITY_SHIFT = 0.05

        private const val WANTED_DIVERGENCE = 0.01
    }
}
