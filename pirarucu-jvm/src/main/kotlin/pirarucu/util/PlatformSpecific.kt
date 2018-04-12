package pirarucu.util

import pirarucu.eval.EvalConstants
import pirarucu.tuning.TunableConstants
import java.util.Arrays
import java.util.Random

actual class PlatformSpecific actual constructor() {

    private val random = Random()

    actual fun randomFloat(): Float {
        return random.nextFloat()
    }

    actual fun randomInt(bound: Int): Int {
        return random.nextInt(bound)
    }

    actual fun randomLong(): Long {
        return random.nextLong()
    }

    actual fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    actual fun numberOfTrailingZeros(value: Long): Int {
        return java.lang.Long.numberOfTrailingZeros(value)
    }

    actual fun numberOfTrailingZeros(value: Int): Int {
        return java.lang.Integer.numberOfTrailingZeros(value)
    }

    actual fun bitCount(value: Long): Int {
        return java.lang.Long.bitCount(value)
    }

    actual fun reverseBytes(value: Long): Long {
        return java.lang.Long.reverseBytes(value)
    }

    actual fun arraySort(array: IntArray, start: Int, end: Int) {
        Arrays.sort(array, start, end)
    }

    actual fun arrayFill(array: ShortArray, value: Short) {
        Arrays.fill(array, value)
    }

    actual fun arrayFill(array: IntArray, value: Int) {
        Arrays.fill(array, value)
    }

    actual fun arrayFill(array: LongArray, value: Long) {
        Arrays.fill(array, value)
    }

    actual fun arrayCopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
        System.arraycopy(src, srcPos, dest, destPos, length)
    }

    actual fun arrayCopy(src: LongArray, srcPos: Int, dest: LongArray, destPos: Int, length: Int) {
        System.arraycopy(src, srcPos, dest, destPos, length)
    }

    actual fun formatString(source: String, vararg args: Any): String {
        return String.format(source, args)
    }

    actual fun applyConfig(option: String, value: Int) {
        val optionList = option.split('-')

        val field = TunableConstants::class.java.getDeclaredField(optionList[0])
        var constant: IntArray? = null
        when (optionList.size) {
            1 -> {
                field.isAccessible = true
                field.set(EvalConstants::class.java, value)
            }
            2 -> {
                field.isAccessible = true
                constant = field.get(null) as IntArray
            }
            3 -> {
                field.isAccessible = true
                val arrayConstant = field.get(null) as Array<IntArray>
                constant = arrayConstant[optionList[1].toInt()]
            }
        }
        if (constant != null) {
            updateArray(constant, optionList[optionList.size - 1].toInt(), value)
        }
    }

    private fun updateArray(array: IntArray, position: Int, value: Int) {
        array[position] = value
    }

    actual fun exit(code: Int) {
        System.exit(code)
    }

    actual fun gc() {
        System.gc()
    }
}