package pirarucu.tuning.texel

import pirarucu.util.FixedSizeMap
import java.util.BitSet

class TexelTuningController {

    private val tuningDataList = mutableListOf<TexelTuningData>()

    private var totalBits = 0

    private val geneCache = FixedSizeMap<BitSet, Double>(10)
    private var currentGenes = BitSet()

    private var currentTuningObject = 0

    private var bestElementResult = 0.0
    private var bestInteractionResult = 1.0

    fun initialResult(result: Double) {
        bestElementResult = result
        reset()
    }

    fun registerTuningData(tuningData: TexelTuningData) {
        tuningData.geneIndex = totalBits
        totalBits += tuningData.totalBits
        tuningDataList.add(tuningData)
        geneCache.size = totalBits * 1000
    }

    private fun reset() {
        bestInteractionResult = 1.0
        currentTuningObject = 0
        currentGenes = BitSet()
        for (entry in tuningDataList) {
            entry.reset()
            currentGenes = entry.updateGenes(currentGenes)
        }
    }

    fun hasNext(): Boolean {
        for (entry in tuningDataList) {
            entry.reset()
        }
        while (!tuningDataList[currentTuningObject].hasNext()) {
            currentTuningObject++
            if (currentTuningObject >= tuningDataList.size) {
                return false
            }
        }
        return currentTuningObject < tuningDataList.size
    }

    fun next(): Boolean {
        tuningDataList[currentTuningObject].next()
        currentGenes = BitSet()
        for (entry in tuningDataList) {
            currentGenes = entry.updateGenes(currentGenes)
        }
        if (geneCache.contains(currentGenes)) {
            reportCurrent(geneCache.getValue(currentGenes)!!)
            return false
        }
        return true
    }

    fun reportCurrent(result: Double) {
        if (bestInteractionResult > result) {
            bestInteractionResult = result
            setBestInteractionResult()
        }
        if (bestElementResult > result) {
            bestElementResult = result
            setBestResult()
            println("Improvement found -> $bestInteractionResult | " +
                tuningDataList[currentTuningObject].getElementString())
        }
        geneCache.add(currentGenes, result)
    }

    private fun setBestResult() {
        tuningDataList[currentTuningObject].bestResult()
    }

    private fun setBestInteractionResult() {
        tuningDataList[currentTuningObject].bestInteractionResult()
    }

    fun printBestElements() {
        println("Best result $bestElementResult")
        for (entry in tuningDataList) {
            if (entry.foundImprovement()) {
                println(entry.getBestElement())
            }
        }
    }

    private fun printInteractionResult() {
        println("Interaction result $bestInteractionResult")
    }

    /**
     * Finish interaction and returns optimization state
     */
    fun finishInteraction(): Boolean {
        printInteractionResult()
        printBestElements()
        reportList()
        val interactionResult = bestInteractionResult
        reset()
        return interactionResult != bestElementResult
    }

    private fun reportList() {
        val percent = geneCache.storedElements * 1000 / geneCache.size
        println("Cache " + geneCache.storedElements + " | " + geneCache.size + " ($percent)")
    }
}
