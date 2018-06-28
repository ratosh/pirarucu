package pirarucu.tuning.pbil

import pirarucu.util.Utils
import java.util.Arrays
import java.util.BitSet

data class PbilTuningData(val name: String, val elementList: IntArray, val bitsPerValue: IntArray,
                          val allowNegatives: Boolean, val ignoreElementList: IntArray) {

    private var bestElementList = IntArray(elementList.size)
    private var bestInteractionElementList = IntArray(elementList.size)

    var totalBits = 0
        private set

    private var geneStartIndex = 0

    var geneIndex: Int
        get() = geneStartIndex
        set(value) {
            geneStartIndex = value
        }

    init {
        if (allowNegatives) {
            for (index in bitsPerValue.indices) {
                bitsPerValue[index]++
            }
        }
        totalBits = bitsPerValue.sum()
        geneIndex = 0
        bestResult()
    }

    fun reset() {
        for (index in elementList.indices) {
            elementList[index] = 0
        }
    }

    fun generateElements(genes: BitSet) {
        reset()
        for (i in 0 until totalBits) {
            val genePosition = i + geneStartIndex
            val position = elementWithBit(i)
            if (Arrays.binarySearch(ignoreElementList, position) < 0 && genes.get(genePosition)) {
                if (allowNegatives && isSignalBit(i)) {
                    elementList[position] *= -1
                } else {
                    elementList[position] = elementList[position] or (1 shl elementBit(i))
                }
            }
        }
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

    fun bestResult() {
        Utils.specific.arrayCopy(elementList, 0, bestElementList, 0, elementList.size)
    }


    fun bestInteractionResult() {
        Utils.specific.arrayCopy(elementList, 0, bestInteractionElementList, 0, elementList.size)
    }

    fun printBestInteractionElement() {
        println("$name " + Arrays.toString(bestInteractionElementList))
    }

    fun printBestElement() {
        println("$name " + Arrays.toString(bestElementList))
    }
}