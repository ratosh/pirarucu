package pirarucu.tuning.texel

import pirarucu.util.Utils
import java.util.Arrays
import java.util.BitSet

data class TexelTuningData(val name: String, val elementList: IntArray, val bitsPerValue: IntArray,
                           val allowNegatives: Boolean, val ignoreElementList: IntArray, val increment: Int) {

    private var upperBounds = IntArray(elementList.size)
    private var lowerBounds = IntArray(elementList.size)

    private var bestElementList = IntArray(elementList.size)
    private var bestInteractionElementList = IntArray(elementList.size)

    var totalBits = 0
        private set

    private var geneStartIndex = 0

    private var currentIndex = 0
    private var currentIncrement = increment

    var geneIndex: Int
        get() = geneStartIndex
        set(value) {
            geneStartIndex = value
        }

    init {
        for (index in bitsPerValue.indices) {
            if (ignoreElementList.contains(index)) {
                bitsPerValue[index] = 0
            }
            if (allowNegatives) {
                upperBounds[index] = 1 shl bitsPerValue[index]
                lowerBounds[index] = -upperBounds[index]
                bitsPerValue[index]++
            } else {
                upperBounds[index] = 1 shl bitsPerValue[index]
                lowerBounds[index] = 0
            }
        }
        totalBits = bitsPerValue.sum()
        geneIndex = 0
        bestResult()
    }

    fun reset() {
        for (index in elementList.indices) {
            elementList[index] = bestElementList[index]
        }
    }

    fun updateGenes(genes: BitSet): BitSet {
        val result = BitSet()
        for (bit in 0 until genes.size()) {
            if (genes.get(bit)) {
                result.set(bit)
            }
        }
        for (bit in 0 until totalBits) {
            val genePosition = bit + geneStartIndex
            result.set(genePosition, false)
            val position = elementWithBit(bit)
            if (Arrays.binarySearch(ignoreElementList, position) < 0) {
                if (allowNegatives && isSignalBit(bit)) {
                    if (elementList[position] < 0) {
                        result.set(genePosition)
                    }
                } else {
                    if (elementList[position] and (1 shl elementBit(bit)) != 0) {
                        result.set(genePosition)
                    }
                }
            }
        }
        return result
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

    fun getBestInteractionElement(): String {
        return "$name " + Arrays.toString(bestInteractionElementList)
    }

    fun getElementString(): String {
        return "$name " + Arrays.toString(elementList)
    }

    fun getBestElement(): String {
        return "$name " + Arrays.toString(bestElementList)
    }

    fun next() {
        for (index in elementList.indices) {
            elementList[index] = bestElementList[index]
        }
        if (insideBounds(currentIndex, currentIncrement)) {
            elementList[currentIndex] = elementList[currentIndex] + currentIncrement
        }
        currentIndex += 1
    }

    fun hasNext(): Boolean {
        while (currentIndex < elementList.size &&
            (Arrays.binarySearch(ignoreElementList, currentIndex) >= 0 ||
                !insideBounds(currentIndex, currentIncrement))) {
            currentIndex++
        }
        if (currentIndex >= elementList.size) {
            if (currentIncrement > 0) {
                currentIndex = 0
                currentIncrement = -currentIncrement
                return hasNext()
            } else {
                currentIndex = 0
                currentIncrement = increment
                return false
            }
        }
        return true
    }

    private fun insideBounds(index: Int, increment: Int): Boolean {
        val nextEntry = elementList[index] + increment
        return nextEntry <= upperBounds[index] && nextEntry >= lowerBounds[index]
    }
}