package pirarucu.tuning.pbil

import pirarucu.util.FixedSizeMap
import java.util.BitSet

class PbilTuningController {
    var population: Int = 100
        private set

    private val generator = PbilGenerator()

    private val tuningDataList = mutableListOf<PbilTuningData>()

    private var totalBits = 0

    private val usedGenes = FixedSizeMap<BitSet, Double>(10)

    private var currentGenes = BitSet()

    private var bestElementResult = 0.toDouble()
    private var bestInteractionResult = 1.toDouble()
    private var worstInteractionResult = 0.toDouble()

    private var bestInteractionGenes: BitSet? = null
    private var worstInteractionGenes: BitSet? = null

    fun initialResult(result: Double) {
        bestElementResult = result
    }

    fun registerTuningData(tuningData: PbilTuningData) {
        tuningData.geneIndex = totalBits
        totalBits += tuningData.totalBits
        generator.totalBits = totalBits
        population = totalBits ushr 3
        tuningDataList.add(tuningData)
        usedGenes.size = population * 20
    }

    protected fun reset() {
        bestInteractionResult = 1.toDouble()
        worstInteractionResult = 0.toDouble()
    }

    fun nextPopulation(): Boolean {
        val genes = generator.generateGenes()

        currentGenes = genes
        for (tuningData in tuningDataList) {
            tuningData.generateElements(genes)
        }
        if (usedGenes.contains(genes)) {
            reportCurrent(usedGenes.getValue(genes)!!)
            return false
        }
        return true
    }

    fun reportCurrent(result: Double) {
        if (bestInteractionResult > result) {
            bestInteractionResult = result
            bestInteractionGenes = currentGenes
            setBestInteractionResult()
        }
        if (worstInteractionResult < result) {
            worstInteractionResult = result
            worstInteractionGenes = currentGenes
        }
        if (bestElementResult > result) {
            bestElementResult = result
            setBestResult()
        }
        usedGenes.add(currentGenes, result)
    }

    private fun setBestResult() {
        for (entry in tuningDataList) {
            entry.bestResult()
        }
    }


    private fun setBestInteractionResult() {
        for (entry in tuningDataList) {
            entry.bestInteractionResult()
        }
    }

    fun printBestElements() {
        println("Best result $bestElementResult")
        for (entry in tuningDataList) {
            entry.printBestElement()
        }
    }

    private fun printInteractionResult() {
        println("Interaction result $bestInteractionResult")
        for (entry in tuningDataList) {
            entry.printBestInteractionElement()
        }
    }

    fun finishInteraction() {
        printInteractionResult()
        printBestElements()
        generator.reportResult(bestInteractionGenes!!, worstInteractionGenes!!)
        reset()
    }
}
