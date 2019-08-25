package pirarucu.util

import pirarucu.tuning.TunableConstants
import pirarucu.uci.UciOutput
import java.security.SecureRandom
import java.util.Arrays
import kotlin.system.exitProcess

actual object PlatformSpecific {

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

    actual fun arrayFill(array: Array<IntArray>, value: Int) {
        array.forEach { arrayFill(it, value) }
    }

    actual fun arrayFill(array: Array<Array<IntArray>>, value: Int) {
        array.forEach { arrayFill(it, value) }
    }

    actual fun arrayCopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
        System.arraycopy(src, srcPos, dest, destPos, length)
    }

    actual fun arrayCopy(src: Array<IntArray>, dest: Array<IntArray>) {
        for (index in src.indices) {
            arrayCopy(src[index], 0, dest[index], 0, src[index].size)
        }
    }

    actual fun arrayCopy(src: LongArray, srcPos: Int, dest: LongArray, destPos: Int, length: Int) {
        System.arraycopy(src, srcPos, dest, destPos, length)
    }

    actual fun arrayCopy(src: Array<LongArray>, dest: Array<LongArray>) {
        for (index in src.indices) {
            arrayCopy(src[index], 0, dest[index], 0, src[index].size)
        }
    }

    actual fun formatString(source: String, vararg args: Any): String {
        return String.format(source, args)
    }

    actual fun applyConfig(option: String, value: Int) {
        UciOutput.println("Setting $option with value $value")
        val optionList = option.split('-')

        val field = TunableConstants::class.java.getDeclaredField(optionList[0])
        var constant: IntArray? = null
        when (optionList.size) {
            1 -> {
                field.isAccessible = true
                field.set(TunableConstants::class.java, value)
                println("Result " + field.get(TunableConstants::class.java))
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
            4 -> {
                field.isAccessible = true
                val arrayConstant = field.get(null) as Array<Array<IntArray>>
                constant = arrayConstant[optionList[1].toInt()][optionList[2].toInt()]
            }
        }
        if (constant != null) {
            updateArray(constant, optionList[optionList.size - 1].toInt(), value)
            UciOutput.println("Result ${constant.toList()}")
        }
    }

    private fun updateArray(array: IntArray, position: Int, value: Int) {
        array[position] = value
    }

    actual fun exit(code: Int) {
        exitProcess(code)
    }

    actual fun gc() {
        System.gc()
    }

    actual fun getVersion(): String {
        var version: String? = null
        val pkg = PlatformSpecific::class.java.`package`
        if (pkg != null) {
            version = pkg.implementationVersion
            if (version == null) {
                version = pkg.specificationVersion
            }
        }
        version = if (version == null) "" else version.trim { it <= ' ' }
        return if (version.isEmpty()) "v?" else version
    }
}