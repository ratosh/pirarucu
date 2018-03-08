package pirarucu.util

expect class PlatformSpecific() {

    fun randomFloat(): Float

    fun randomInt(bound: Int): Int

    fun randomLong(): Long

    fun currentTimeMillis(): Long

    fun numberOfTrailingZeros(value: Long): Int
    
    fun numberOfTrailingZeros(value: Int): Int

    fun bitCount(value: Long): Int

    fun reverseBytes(value: Long): Long

    fun arraySort(array: IntArray, start: Int, end: Int)

    fun arrayFill(array: ShortArray, value: Short)

    fun arrayFill(array: IntArray, value: Int)

    fun arrayFill(array: LongArray, value: Long)

    fun arrayCopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int)

    fun arrayCopy(src: LongArray, srcPos: Int, dest: LongArray, destPos: Int, length: Int)

    fun formatString(source: String, vararg args: Any): String

    fun exit(code: Int)

    fun gc()
}