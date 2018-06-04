package pirarucu.cache

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.eval.EvalConstants
import pirarucu.stats.Statistics
import pirarucu.util.Utils

/**
 * 80 Bits per entry cache
 */
class PawnEvaluationCache(sizeMb: Int) {

    private val tableBits = Square.getSquare(sizeMb) + 16
    var tableLimit = Bitboard.getBitboard(tableBits).toInt()
    private val indexShift = 64 - tableBits

    private val keys = LongArray(tableLimit)
    val values = ShortArray(tableLimit)

    fun reset() {
        Utils.specific.arrayFill(keys, 0)
    }

    fun findEntry(board: Board): Int {
        if (Statistics.ENABLED) {
            Statistics.pawnCache++
        }
        val wantedKey = getKey(board)
        val index = getIndex(wantedKey)
        if (keys[index] == wantedKey) {
            if (Statistics.ENABLED) {
                Statistics.pawnCacheHit++
            }
            return index
        }
        return -1
    }

    fun saveEntry(board: Board, value: Int) {
        if (EvalConstants.PAWN_EVAL_CACHE) {
            val key = getKey(board)
            val index = getIndex(key)
            keys[index] = key
            values[index] = value.toShort()
        }
    }

    private fun getKey(board: Board): Long {
        return board.pawnZobristKey
    }

    private fun getIndex(key: Long): Int {
        return (key ushr indexShift).toInt()
    }

}