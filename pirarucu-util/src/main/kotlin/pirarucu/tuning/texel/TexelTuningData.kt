package pirarucu.tuning.texel

import pirarucu.util.PlatformSpecific
import java.util.*

data class TexelTuningData(
        val name: String, val elementList: IntArray, val bitsPerValue: IntArray,
        val allowNegatives: Boolean, val ignoreElementList: IntArray, val increment: Int, val minIncrement: Int = 1
) {

    private var upperBounds = IntArray(elementList.size)
    private var lowerBounds = IntArray(elementList.size)

    private var originalElementList = IntArray(elementList.size)
    private var bestElementList = IntArray(elementList.size)
    private var bestInteractionElementList = IntArray(elementList.size)

    var totalBits = 0
        private set

    private var geneStartIndex = 0

    private var currentIndex = 0
    private var baseIncrement = increment
    private var currentMultiplier = 1

    var geneIndex: Int
        get() = geneStartIndex
        set(value) {
            geneStartIndex = value
        }

    fun foundImprovement(): Boolean {
        return !(originalElementList contentEquals bestElementList)
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
        PlatformSpecific.arrayCopy(elementList, 0, originalElementList, 0, elementList.size)
        setBestResult()
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

    fun setBestResult() {
        PlatformSpecific.arrayCopy(elementList, 0, bestElementList, 0, elementList.size)
    }

    fun setBestInteractionResult() {
        PlatformSpecific.arrayCopy(elementList, 0, bestInteractionElementList, 0, elementList.size)
    }

    fun getBestInteractionElement(): String {
        return "$name " + bestInteractionElementList.contentToString()
    }

    fun getElementString(): String {
        return "$name " + elementList.contentToString()
    }

    fun getBestElement(): String {
        return "$name " + bestElementList.contentToString()
    }

    fun next() {
        for (index in elementList.indices) {
            elementList[index] = bestElementList[index]
        }
        if (insideBounds()) {
            elementList[currentIndex] = nextValue()
        }
    }

    fun hasNext(): Boolean {
        if (baseIncrement < minIncrement) {
            return false
        }
        currentIndex++
        while (currentIndex < elementList.size &&
                (Arrays.binarySearch(ignoreElementList, currentIndex) >= 0 ||
                        !insideBounds())
        ) {
            currentIndex++
        }
        if (currentIndex >= elementList.size) {
            return if (currentMultiplier > 0) {
                currentIndex = 0
                currentMultiplier *= -1
                hasNext()
            } else {
                currentIndex = 0
                currentMultiplier *= -1
                false
            }
        }
        return true
    }

    fun canLowerIncrement(): Boolean {
        return baseIncrement > minIncrement
    }

    private fun insideBounds(): Boolean {
        val nextEntry = nextValue()
        return nextEntry <= upperBounds[currentIndex] && nextEntry >= lowerBounds[currentIndex]
    }

    private fun nextValue() = elementList[currentIndex] + baseIncrement * currentMultiplier

    fun lowerIncrement() {
        if (canLowerIncrement()) {
            baseIncrement /= 2
            currentMultiplier = 1
        }
        println("increment -> $baseIncrement")
    }
}