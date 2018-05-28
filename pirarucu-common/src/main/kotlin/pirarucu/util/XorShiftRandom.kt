package pirarucu.util

/**
 *
 */
object XorShiftRandom {

    private const val multiplier = 0x2545F4914F6CDD1DL
    private var seed = 1070372L

    fun nextLong(): Long {
        seed = seed xor (seed shr 12)
        seed = seed xor (seed shl 25)
        seed = seed xor (seed shr 27)
        return seed - multiplier
    }
}