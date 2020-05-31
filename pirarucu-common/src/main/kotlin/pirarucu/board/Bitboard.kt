package pirarucu.board

/**
 * Bitboard singleton class.
 * Use a long to represent a chess board.
 */
object Bitboard {

    val A1 = getBitboard(Square.A1)
    val B1 = getBitboard(Square.B1)
    val C1 = getBitboard(Square.C1)
    val D1 = getBitboard(Square.D1)
    val E1 = getBitboard(Square.E1)
    val F1 = getBitboard(Square.F1)
    val G1 = getBitboard(Square.G1)
    val H1 = getBitboard(Square.H1)

    val A2 = getBitboard(Square.A2)
    val B2 = getBitboard(Square.B2)
    val C2 = getBitboard(Square.C2)
    val D2 = getBitboard(Square.D2)
    val E2 = getBitboard(Square.E2)
    val F2 = getBitboard(Square.F2)
    val G2 = getBitboard(Square.G2)
    val H2 = getBitboard(Square.H2)

    val A3 = getBitboard(Square.A3)
    val B3 = getBitboard(Square.B3)
    val C3 = getBitboard(Square.C3)
    val D3 = getBitboard(Square.D3)
    val E3 = getBitboard(Square.E3)
    val F3 = getBitboard(Square.F3)
    val G3 = getBitboard(Square.G3)
    val H3 = getBitboard(Square.H3)

    val A4 = getBitboard(Square.A4)
    val B4 = getBitboard(Square.B4)
    val C4 = getBitboard(Square.C4)
    val D4 = getBitboard(Square.D4)
    val E4 = getBitboard(Square.E4)
    val F4 = getBitboard(Square.F4)
    val G4 = getBitboard(Square.G4)
    val H4 = getBitboard(Square.H4)

    val A5 = getBitboard(Square.A5)
    val B5 = getBitboard(Square.B5)
    val C5 = getBitboard(Square.C5)
    val D5 = getBitboard(Square.D5)
    val E5 = getBitboard(Square.E5)
    val F5 = getBitboard(Square.F5)
    val G5 = getBitboard(Square.G5)
    val H5 = getBitboard(Square.H5)

    val A6 = getBitboard(Square.A6)
    val B6 = getBitboard(Square.B6)
    val C6 = getBitboard(Square.C6)
    val D6 = getBitboard(Square.D6)
    val E6 = getBitboard(Square.E6)
    val F6 = getBitboard(Square.F6)
    val G6 = getBitboard(Square.G6)
    val H6 = getBitboard(Square.H6)

    val A7 = getBitboard(Square.A7)
    val B7 = getBitboard(Square.B7)
    val C7 = getBitboard(Square.C7)
    val D7 = getBitboard(Square.D7)
    val E7 = getBitboard(Square.E7)
    val F7 = getBitboard(Square.F7)
    val G7 = getBitboard(Square.G7)
    val H7 = getBitboard(Square.H7)

    val A8 = getBitboard(Square.A8)
    val B8 = getBitboard(Square.B8)
    val C8 = getBitboard(Square.C8)
    val D8 = getBitboard(Square.D8)
    val E8 = getBitboard(Square.E8)
    val F8 = getBitboard(Square.F8)
    val G8 = getBitboard(Square.G8)
    val H8 = getBitboard(Square.H8)

    const val ALL: Long = -1
    const val EMPTY: Long = 0L
    const val WHITES: Long = -0x55aa55aa55aa55abL
    const val BLACKS: Long = 0x55aa55aa55aa55aaL

    val FILE_A = A1 or A2 or A3 or A4 or A5 or A6 or A7 or A8
    val FILE_B = B1 or B2 or B3 or B4 or B5 or B6 or B7 or B8
    val FILE_C = C1 or C2 or C3 or C4 or C5 or C6 or C7 or C8
    val FILE_D = D1 or D2 or D3 or D4 or D5 or D6 or D7 or D8
    val FILE_E = E1 or E2 or E3 or E4 or E5 or E6 or E7 or E8
    val FILE_F = F1 or F2 or F3 or F4 or F5 or F6 or F7 or F8
    val FILE_G = G1 or G2 or G3 or G4 or G5 or G6 or G7 or G8
    val FILE_H = H1 or H2 or H3 or H4 or H5 or H6 or H7 or H8

    val NOT_FILE_A = ALL xor FILE_A
    val NOT_FILE_B = ALL xor FILE_B
    val NOT_FILE_C = ALL xor FILE_C
    val NOT_FILE_D = ALL xor FILE_D
    val NOT_FILE_E = ALL xor FILE_E
    val NOT_FILE_F = ALL xor FILE_F
    val NOT_FILE_G = ALL xor FILE_G
    val NOT_FILE_H = ALL xor FILE_H

    val FILES = longArrayOf(FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H)

    val RANK_1 = A1 or B1 or C1 or D1 or E1 or F1 or G1 or H1
    val RANK_2 = A2 or B2 or C2 or D2 or E2 or F2 or G2 or H2
    val RANK_3 = A3 or B3 or C3 or D3 or E3 or F3 or G3 or H3
    val RANK_4 = A4 or B4 or C4 or D4 or E4 or F4 or G4 or H4
    val RANK_5 = A5 or B5 or C5 or D5 or E5 or F5 or G5 or H5
    val RANK_6 = A6 or B6 or C6 or D6 or E6 or F6 or G6 or H6
    val RANK_7 = A7 or B7 or C7 or D7 or E7 or F7 or G7 or H7
    val RANK_8 = A8 or B8 or C8 or D8 or E8 or F8 or G8 or H8

    val NOT_RANK_1 = ALL xor RANK_1
    val NOT_RANK_2 = ALL xor RANK_2
    val NOT_RANK_3 = ALL xor RANK_3
    val NOT_RANK_4 = ALL xor RANK_4
    val NOT_RANK_5 = ALL xor RANK_5
    val NOT_RANK_6 = ALL xor RANK_6
    val NOT_RANK_7 = ALL xor RANK_7
    val NOT_RANK_8 = ALL xor RANK_8

    val RANKS = longArrayOf(RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8)

    val FILES_ADJACENT = longArrayOf(
        FILE_B, FILE_A or FILE_C, FILE_B or FILE_D, FILE_C or FILE_E,
        FILE_D or FILE_F, FILE_E or FILE_G, FILE_F or FILE_H, FILE_G
    )

    val KING_SIDE = FILE_F or FILE_G or FILE_H
    val QUEEN_SIDE = FILE_A or FILE_B or FILE_C

    val PROMOTION_BITBOARD = RANK_1 or RANK_8
    val FROM_PROMOTION_BITBOARD = longArrayOf(RANK_7, RANK_2)
    val DOUBLE_MOVEMENT_BITBOARD = arrayOf(RANK_2, RANK_7)

    val OUTPOST = longArrayOf(RANK_4 or RANK_5 or RANK_6, RANK_3 or RANK_4 or RANK_5)

    /**
     * Get bitboard info from a square.
     */
    inline fun getBitboard(square: Int): Long {
        return 1L shl square
    }

    /**
     * Get bitboard info from a square list.
     */
    fun getBitboard(vararg squareList: Int): Long {
        var result = EMPTY
        for (square in squareList) {
            result = result or getBitboard(square)
        }
        return result
    }

    /**
     * Check if the bitboard only contains one element.
     */
    fun oneElement(bitboard: Long): Boolean {
        return bitboard and (bitboard - 1) == EMPTY
    }

    /**
     * Gets a bitboard and returns its string representation.
     */
    fun toString(bitboard: Long): String {
        val buffer = StringBuilder()
        buffer.append("  abcdefgh\n")
        for (rank in Rank.RANK_8 downTo Rank.RANK_1) {
            buffer.append(Rank.toString(rank)).append('-')
            for (file in File.FILE_A until File.SIZE) {
                val square = Square.getSquare(file, rank)
                buffer.append(if (bitboard and getBitboard(square) != EMPTY) "1" else "0")
            }
            buffer.append("\n")
        }

        return buffer.toString()
    }
}
