package pirarucu.tuning.pbil

import pirarucu.util.Utils
import java.util.ArrayList
import java.util.Arrays
import java.util.BitSet

class PbilTunningObject(val name: String, protected val elementList: IntArray, bitsPerValue: IntArray,
                        allowNegatives: Boolean, vararg ignoreElementArgs: Int) {


    val ignoredElements = ignoreElementArgs
    val population: Int

    private val generator: PbilGenerator

    private var bestElementList: IntArray
    private var bestElementResult: Double = 0.toDouble()

    private var interactionBestElementList = IntArray(elementList.size)
    private var bestGenes: BitSet? = null
    private var bestResult: Double = 0.toDouble()

    private var worstGenes: BitSet? = null
    private var worstResult: Double = 0.toDouble()

    private val interactionGenes = ArrayList<BitSet>()

    init {
        if (allowNegatives) {
            for (index in bitsPerValue.indices) {
                bitsPerValue[index]++
            }
        }
        val totalBits = bitsPerValue.sum()
        this.population = totalBits

        this.generator = PbilGenerator(bitsPerValue, totalBits, allowNegatives,
            *ignoreElementArgs)

        this.bestElementList = Arrays.copyOf(elementList, elementList.size)
        this.bestElementResult = 0.0

        this.bestResult = 1.0
        this.worstResult = 0.0
    }

    protected fun reset() {
        bestResult = 1.0
        worstResult = 0.0
        Utils.specific.arrayCopy(bestElementList, 0, elementList, 0, elementList.size)
        interactionGenes.clear()
    }

    fun nextPopulation(): BitSet? {
        val genes = generator.generateGenes()
        for (index in interactionGenes.indices) {
            if (interactionGenes[index] == genes) {
                return null
            }
        }
        val elements = generator.generateElements(genes)
        for (ignored in ignoredElements) {
            elements[ignored] = bestElementList[ignored]
        }

        Utils.specific.arrayCopy(elements, 0, elementList, 0, elementList.size)

        interactionGenes.add(genes)
        return genes
    }

    fun reportCurrent(genes: BitSet, result: Double) {
        if (bestResult > result) {
            bestResult = result
            bestGenes = genes
            Utils.specific.arrayCopy(elementList, 0, interactionBestElementList, 0, elementList.size)
        }
        if (worstResult < result) {
            worstResult = result
            worstGenes = genes
        }
        if (bestElementResult > result) {
            Utils.specific.arrayCopy(elementList, 0, bestElementList, 0, elementList.size)
            bestElementResult = result
        }
    }

    private fun printElements(elementList: IntArray) {
        val sb = StringBuilder(name).append(" AVG:").append(elementList.sum() / elementList.size).append("\n")
        sb.append("Result:").append(Arrays.toString(elementList)).append("\n")
        println(sb.toString())
    }

    fun printBestElements() {
        println("Best result $bestElementResult")
        printElements(bestElementList)
    }

    fun finishInteraction() {
        println("Best interaction result $bestResult")
        printElements(interactionBestElementList)
        printBestElements()
        generator.reportResult(bestGenes!!, worstGenes!!)
        reset()
    }

    fun reportOriginal(error: Double) {
        bestElementResult = error
    }
}
