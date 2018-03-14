package pirarucu.util

object SplitValue {

    private const val SCORE_BITS = 16
    private const val LIMIT = 1 shl SCORE_BITS - 1

    fun mergeParts(firstPart: Int, secondPart: Int): Int {
        return (firstPart shl 16) + secondPart
    }

    fun turnIntoPart(value: Int, partNumber: Int): Int {
        when (partNumber) {
            0 -> return value shl 16
            1 -> return value
        }
        return 0
    }

    fun getPart(value: Int, partNumber: Int): Int {
        when (partNumber) {
            0 -> return getFirstPart(value)
            1 -> return getSecondPart(value)
        }
        return 0
    }

    fun getFirstPart(value: Int): Int {
        return ((value + LIMIT) shr SCORE_BITS).toShort().toInt()
    }

    fun getSecondPart(value: Int): Int {
        return value.toShort().toInt()
    }

    fun toString(value: Int): String {
        val p1 = getFirstPart(value)
        val p2 = getSecondPart(value)
        return "$p1 | $p2"
    }
}
