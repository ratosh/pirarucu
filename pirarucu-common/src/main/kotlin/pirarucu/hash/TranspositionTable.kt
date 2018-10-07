package pirarucu.hash

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.eval.EvalConstants
import pirarucu.util.Utils
import kotlin.math.min

/**
 * Transposition table
 * https://chessprogramming.wikispaces.com/Transposition+Table
 *
 * keys (LongArray)
 * Zobrist key - 64 bits
 *
 * infos (LongArray) (64 bits)
 * ttMove - 16 bits
 * Eval - 16 bits
 * Score - 16 bits
 * Depth - 14 bits
 * Score type - 2 bits
 *
 * Total bits - 128 bits
 *
 * 1KB = 64 Entries
 */
class TranspositionTable {

    var baseDepth = 0

    var ttUsage = 0L

    private var tableBits = Square.getSquare(HashConstants.TRANSPOSITION_TABLE_SIZE) + 16
    var tableLimit = Bitboard.getBitboard(tableBits).toInt()
    private var indexShift = Square.SIZE - tableBits

    private var keys = LongArray(tableLimit)
    private var infos = LongArray(tableLimit)

    fun resize(sizeMb: Int) {
        tableBits = Square.getSquare(sizeMb) + 16
        tableLimit = Bitboard.getBitboard(tableBits).toInt()
        indexShift = Square.SIZE - tableBits

        keys = LongArray(tableLimit)
        infos = LongArray(tableLimit)

        ttUsage = 0
        baseDepth = 0
    }

    fun reset() {
        ttUsage = 0
        baseDepth = 0

        Utils.specific.arrayFill(keys, 0)
        Utils.specific.arrayFill(infos, 0)
    }

    fun findEntry(board: Board): Long {
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex
        val wantedKey = board.zobristKey
        while (index < maxIndex) {
            val info = infos[index]
            val key = keys[index]
            val savedKey = key xor info
            // Unpopulated entry
            if (key == 0L && savedKey == 0L) {
                break
            }
            if (wantedKey == savedKey) {
                return info
            }
            index++
        }
        return HashConstants.EMPTY_INFO
    }

    fun save(board: Board, eval: Int, score: Int, scoreType: Int, depth: Int, ply: Int, bestMove: Int) {
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex

        var usedIndex = -1

        val wantedKey = board.zobristKey

        var replacedDepth = Int.MAX_VALUE

        while (index < maxIndex) {
            val info = infos[index]
            val key = keys[index]
            val savedKey = key xor info
            // Unpopulated entry
            if (key == 0L && savedKey == 0L) {
                ttUsage++
                usedIndex = index
                break
            }
            val savedDepth = getDepth(info)

            // Update entry
            if (savedKey == wantedKey) {
                usedIndex = index
                if (savedDepth > depth && scoreType != HashConstants.SCORE_TYPE_EXACT_SCORE) {
                    return
                }
                break
            }

            // Replace the lowest depth
            if (savedDepth < replacedDepth) {
                usedIndex = index
                replacedDepth = savedDepth
            }
            index++
        }

        val realScore: Int = when {
            score >= EvalConstants.SCORE_MATE -> score + ply
            score <= -EvalConstants.SCORE_MATE -> score - ply
            else -> score
        }

        val info = buildInfo(bestMove, eval, realScore, depth + baseDepth, scoreType)
        infos[usedIndex] = info
        keys[usedIndex] = wantedKey xor info
    }

    private fun getIndex(board: Board): Int {
        return (board.zobristKey ushr indexShift).toInt()
    }

    private fun getMaxIndex(startIndex: Int): Int {
        return min(startIndex + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, tableLimit)
    }

    fun getScore(value: Long, ply: Int): Int {
        val score = ((value ushr SCORE_SHIFT) and SCORE_MASK).toShort()
        return when {
            score > EvalConstants.SCORE_MATE -> score - ply
            score < -EvalConstants.SCORE_MATE -> score + ply
            else -> score.toInt()
        }
    }

    fun getEval(value: Long): Int {
        return ((value ushr EVAL_SHIFT) and EVAL_MASK).toShort().toInt()
    }

    fun getMove(value: Long): Int {
        return ((value ushr MOVE_SHIFT) and MOVE_MASK).toInt()
    }

    fun getScoreType(value: Long): Int {
        return ((value ushr SCORE_TYPE_SHIFT) and SCORE_TYPE_MASK).toInt()
    }

    fun getDepth(value: Long): Int {
        return (((value ushr DEPTH_SHIFT) and DEPTH_MASK) - baseDepth).toInt()
    }

    private fun buildInfo(bestMove: Int, eval: Int, score: Int, depth: Int, scoreType: Int): Long {
        return (bestMove.toLong() and MOVE_MASK) or
            ((eval.toLong() and EVAL_MASK) shl EVAL_SHIFT) or
            ((score.toLong() and SCORE_MASK) shl SCORE_SHIFT) or
            (depth.toLong() shl DEPTH_SHIFT) or
            (scoreType.toLong() shl SCORE_TYPE_SHIFT)
    }

    companion object {
        private const val MOVE_SHIFT = 0
        private const val EVAL_SHIFT = 16
        private const val SCORE_SHIFT = 32
        private const val DEPTH_SHIFT = 48
        private const val SCORE_TYPE_SHIFT = 62

        private const val MOVE_MASK = 0xFFFFL
        private const val EVAL_MASK = 0xFFFFL
        private const val SCORE_MASK = 0xFFFFL
        private const val DEPTH_MASK = 0x3FFL
        private const val SCORE_TYPE_MASK = 0x3L
    }
}