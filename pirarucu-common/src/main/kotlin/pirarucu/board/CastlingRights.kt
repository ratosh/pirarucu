package pirarucu.board

object CastlingRights {

    val KING_FINAL_SQUARE = arrayOf(Square.G1, Square.C1, Square.G8, Square.C8)
    val ROOK_FINAL_SQUARE = arrayOf(Square.F1, Square.D1, Square.F8, Square.D8)

    const val NO_CASTLING = 0
    const val WHITE_OO = 1
    const val WHITE_OOO = WHITE_OO shl 1
    const val BLACK_OO = WHITE_OO shl 2
    const val BLACK_OOO = WHITE_OO shl 3

    const val ANY_CASTLING = WHITE_OO or WHITE_OOO or BLACK_OO or BLACK_OOO

    const val KING_CASTLING = WHITE_OO or BLACK_OO
    const val QUEEN_CASTLING = WHITE_OOO or BLACK_OOO

    const val WHITE_CASTLING_RIGHTS = WHITE_OO or WHITE_OOO
    const val BLACK_CASTLING_RIGHTS = BLACK_OO or BLACK_OOO

    const val KING_SIDE = 0
    const val QUEEN_SIDE = 1

    const val SIZE = 16

    private val CHARACTER = charArrayOf('K', 'Q', 'k', 'q', '-')

    const val CASTLING_BITS = 4

    fun getCastlingRight(index: Int): Int {
        return if (index < 0 || index >= CASTLING_BITS) NO_CASTLING else 1 shl index
    }

    fun getCastlingRight(token: Char): Int {
        return getCastlingRight(CHARACTER.indexOf(token))
    }

    fun getCastlingRight(string: String): Int {
        var castlingRights = 0
        for (i in 0 until string.length) {
            castlingRights = castlingRights or getCastlingRight(string[i])
        }
        return castlingRights
    }

    fun getCastlingRightIndex(color: Int, side: Int): Int {
        return side + 2 * color
    }

    fun filterCastlingRight(color: Int, castlingRights: Int): Int {
        return castlingRights and (CastlingRights.WHITE_CASTLING_RIGHTS shl (color * 2))
    }

    fun toString(castlingRights: Int): String {
        if (castlingRights == 0) {
            return CHARACTER[CASTLING_BITS].toString()
        }
        val result = StringBuilder()
        for (i in 0 until CASTLING_BITS) {
            if (castlingRights and getCastlingRight(i) != 0) {
                result.append(CHARACTER[i])
            }
        }
        return result.toString()
    }
}
