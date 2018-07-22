package pirarucu.tuning.sts

import pirarucu.util.FixedSizeMap
import java.util.BitSet

class StsTuningController {

    private val tuningDataList = mutableListOf<StsTuningData>()

    private var totalBits = 0

    private val geneCache = FixedSizeMap<BitSet, Double>(10)
    private var currentGenes = BitSet()

    private var currentTuningObject = 0

    private var bestElementResult = 1.0
    private var bestInteractionResult = 1.0

    fun initialResult(result: Double) {
        currentGenes = BitSet()
        for (entry in tuningDataList) {
            entry.reset()
            currentGenes = entry.updateGenes(currentGenes)
        }
        bestElementResult = result
        setBestResult()
        geneCache.add(currentGenes, result)
        reset()
    }

    fun registerTuningData(tuningData: StsTuningData) {
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
        println(tuningDataList[currentTuningObject].getElementString())
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
            println(entry.getBestElement())
        }
    }

    private fun printInteractionResult() {
        println("Interaction result $bestInteractionResult")
        for (entry in tuningDataList) {
            println(entry.getBestInteractionElement())
        }
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
