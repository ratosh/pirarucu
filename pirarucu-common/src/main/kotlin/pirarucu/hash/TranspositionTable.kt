package pirarucu.hash

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.eval.EvalConstants
import pirarucu.search.SearchOptions
import pirarucu.util.Utils
import kotlin.math.max
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
object TranspositionTable {

    const val EMPTY_INFO = 0L

    var baseDepth = 0

    var ttUsage = 0L

    private val tableBits = Square.getSquare(HashConstants.TRANSPOSITION_TABLE_SIZE) + 16
    val tableLimit = Bitboard.getBitboard(tableBits).toInt()
    private val indexShift = 64 - tableBits

    private val keys = LongArray(tableLimit)
    private val infos = LongArray(tableLimit)

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
            // Unpopulated entry
            val key = keys[index]
            if (key == 0L) {
                break
            }
            if (wantedKey == key) {
                return infos[index]
            }
            index++
        }
        return EMPTY_INFO
    }

    fun save(board: Board, eval: Int, score: Int, scoreType: Int, depth: Int, ply: Int, bestMove: Int) {
        if (SearchOptions.stop) {
            return
        }
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex

        var usedIndex = startIndex

        val wantedKey = board.zobristKey

        var realDepth = depth + baseDepth
        var replacedDepth = depth + baseDepth

        while (index < maxIndex) {
            // Unpopulated entry
            if (keys[index] == 0L) {
                ttUsage++
                usedIndex = index
                break
            }
            val info = infos[index]
            val savedDepth = getDepth(info)

            // Update entry
            if (keys[index] == wantedKey) {
                usedIndex = index
                realDepth = max(savedDepth, realDepth)
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

        keys[usedIndex] = wantedKey
        infos[usedIndex] = buildInfo(bestMove, eval, realScore, realDepth, scoreType)
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
}