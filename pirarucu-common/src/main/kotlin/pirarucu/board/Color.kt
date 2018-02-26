package pirarucu.board

object Color {

    const val INVALID = -1
    const val WHITE = 0
    const val BLACK = 1

    const val SIZE = 2

    private val CHARACTER = charArrayOf('w', 'b')

    fun invertColor(color: Int): Int {
        return color xor 1
    }

    fun getColor(character: Char): Int {
        return CHARACTER.indexOf(character)
    }

    fun toString(color: Int): Char {
        return CHARACTER[color]
    }
}
