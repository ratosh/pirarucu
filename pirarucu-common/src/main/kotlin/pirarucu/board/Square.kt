package pirarucu.board

import pirarucu.util.Utils
import kotlin.math.abs
import kotlin.math.max

object Square {

    const val NONE = 64

    const val A1 = 0
    const val B1 = 1
    const val C1 = 2
    const val D1 = 3
    const val E1 = 4
    const val F1 = 5
    const val G1 = 6
    const val H1 = 7

    const val A2 = 8
    const val B2 = 9
    const val C2 = 10
    const val D2 = 11
    const val E2 = 12
    const val F2 = 13
    const val G2 = 14
    const val H2 = 15

    const val A3 = 16
    const val B3 = 17
    const val C3 = 18
    const val D3 = 19
    const val E3 = 20
    const val F3 = 21
    const val G3 = 22
    const val H3 = 23

    const val A4 = 24
    const val B4 = 25
    const val C4 = 26
    const val D4 = 27
    const val E4 = 28
    const val F4 = 29
    const val G4 = 30
    const val H4 = 31

    const val A5 = 32
    const val B5 = 33
    const val C5 = 34
    const val D5 = 35
    const val E5 = 36
    const val F5 = 37
    const val G5 = 38
    const val H5 = 39

    const val A6 = 40
    const val B6 = 41
    const val C6 = 42
    const val D6 = 43
    const val E6 = 44
    const val F6 = 45
    const val G6 = 46
    const val H6 = 47

    const val A7 = 48
    const val B7 = 49
    const val C7 = 50
    const val D7 = 51
    const val E7 = 52
    const val F7 = 53
    const val G7 = 54
    const val H7 = 55

    const val A8 = 56
    const val B8 = 57
    const val C8 = 58
    const val D8 = 59
    const val E8 = 60
    const val F8 = 61
    const val G8 = 62
    const val H8 = 63

    const val SIZE = 64

    val SQUARE_DISTANCE = Array(Square.SIZE) { IntArray(Square.SIZE) }

    private val CHARACTER = arrayOf(
        "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
        "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
        "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
        "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
        "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
        "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
        "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
        "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "-")

    init {
        populateDistance()
    }

    fun getSquare(file: Int, rank: Int): Int {
        return (rank shl Rank.RANK_SHIFT) + file
    }

    fun getSquare(bitboard: Long): Int {
        return Utils.specific.numberOfTrailingZeros(bitboard)
    }

    fun getSquare(bitboard: Int): Int {
        return Utils.specific.numberOfTrailingZeros(bitboard)
    }

    fun getSquare(square: String): Int {
        return CHARACTER.indexOf(square)
    }

    fun getSquare(vararg squareList: String): IntArray {
        val result = IntArray(squareList.size)
        for (i in squareList.indices) {
            result[i] = getSquare(squareList[i])
        }
        return result
    }

    fun isSameSquareColor(square1: Int, square2: Int): Boolean {
        val bitboard = Bitboard.getBitboard(square1) or Bitboard.getBitboard(square2)
        return bitboard and Bitboard.WHITES == 0L || bitboard and Bitboard.BLACKS == 0L
    }

    fun invertSquare(square: Int): Int {
        return square xor Square.A8
    }

    fun getRelativeSquare(color: Int, square: Int): Int {
        return square xor (Square.A8 * color)
    }

    fun getSquareListFromBitboard(bitboard: Long): IntArray {
        if (bitboard == 0L) {
            return IntArray(0)
        }
        val squareCount = Utils.specific.bitCount(bitboard)
        val result = IntArray(squareCount)
        var index = 0
        for (i in 0 until Square.SIZE) {
            if (bitboard and (1L shl i) != 0L) {
                result[index++] = i
                if (index == squareCount) {
                    break
                }
            }
        }
        return result
    }

    fun toString(bitboard: Long): String {
        val buffer = StringBuilder()
        val squareList = getSquareListFromBitboard(bitboard)
        for (square in squareList) {
            buffer.append(toString(square))
        }
        return buffer.toString()
    }

    fun toString(square: Int): String {
        return CHARACTER[square]
    }

    fun toString(vararg squareList: Int): String {
        val stringBuilder = StringBuilder()
        for (i in squareList.indices) {
            stringBuilder.append(toString(squareList[i]))
        }
        return stringBuilder.toString()
    }

    private fun populateDistance() {
        for (square1 in 0 until Square.SIZE) {
            for (square2 in 0 until Square.SIZE) {
                if (square1 == square2) {
                    continue
                }
                SQUARE_DISTANCE[square1][square2] = calculateDistance(square1, square2)
            }
        }
    }

    private fun calculateDistance(square1: Int, square2: Int): Int {
        return max(abs(File.getFile(square1) - File.getFile(square2)),
            abs(Rank.getRank(square1) - Rank.getRank(square2)))
    }

    fun isValid(square: Int): Boolean {
        return square >= Square.A1 && square <= Square.H8
    }
}
