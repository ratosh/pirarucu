package pirarucu.util

expect object PlatformSpecific {

    fun currentTimeMillis(): Long

    fun numberOfTrailingZeros(value: Long): Int

    fun numberOfTrailingZeros(value: Int): Int

    fun bitCount(value: Long): Int

    fun reverseBytes(value: Long): Long

    fun arraySort(array: IntArray, start: Int, end: Int)

    fun arrayFill(array: ShortArray, value: Short)

    fun arrayFill(array: IntArray, value: Int)

    fun arrayFill(array: LongArray, value: Long)

    fun arrayFill(array: Array<IntArray>, value: Int)

    fun arrayFill(array: Array<Array<IntArray>>, value: Int)

    fun arrayCopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int)

    fun arrayCopy(src: Array<IntArray>, dest: Array<IntArray>)

    fun arrayCopy(src: LongArray, srcPos: Int, dest: LongArray, destPos: Int, length: Int)

    fun arrayCopy(src: Array<LongArray>, dest: Array<LongArray>)

    fun formatString(source: String, vararg args: Any): String

    fun applyConfig(option: String, value: Int)

    fun exit(code: Int)

    fun gc()

    fun getVersion(): String
}
