package pirarucu.board

object File {

    const val INVALID = -1

    const val FILE_A = 0
    const val FILE_B = 1
    const val FILE_C = 2
    const val FILE_D = 3
    const val FILE_E = 4
    const val FILE_F = 5
    const val FILE_G = 6
    const val FILE_H = 7

    const val SIZE = 8

    private val CHARACTER = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')

    fun getFile(token: Char): Int {
        return getFile(CHARACTER.indexOf(token))
    }

    fun getFile(square: Int): Int {
        return square and FILE_H
    }

    fun isValid(file: Int): Boolean {
        return file in FILE_A until SIZE
    }

    fun toString(file: Int): Char {
        return CHARACTER[file]
    }
}
