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
class TranspositionTable(sizeMb: Int) {

    private var currentHashSize = sizeMb

    var baseDepth = 0

    private var tableBits = calculateTableBits(currentHashSize)

    var tableElementCount = calculateTableElements(tableBits)
        private set

    private var indexShift = calculateIndexShift(tableBits)

    private var keys = LongArray(tableElementCount)
    private var infos = LongArray(tableElementCount)

    constructor() : this(HashConstants.TRANSPOSITION_TABLE_SIZE)

    fun resize(sizeMb: Int) {
        if (currentHashSize == sizeMb) {
            return
        }
        currentHashSize = sizeMb
        tableBits = calculateTableBits(sizeMb)
        tableElementCount = calculateTableElements(tableBits)
        indexShift = calculateIndexShift(tableBits)

        keys = LongArray(tableElementCount)
        infos = LongArray(tableElementCount)

        baseDepth = 0
    }

    fun reset() {
        baseDepth = 0

        Utils.specific.arrayFill(keys, 0)
        Utils.specific.arrayFill(infos, 0)
    }

    fun findEntry(board: Board): Long {
        val startIndex = getIndex(board.zobristKey, indexShift)
        val maxIndex = getMaxIndex(startIndex, tableElementCount)
        var index = startIndex
        val wantedKey = board.zobristKey
        while (index < maxIndex) {
            val info = infos[index]
            val key = keys[index]
            val savedKey = key xor info
            // Unpopulated entry
            if (key == 0L && info == 0L) {
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
        val startIndex = getIndex(board.zobristKey, indexShift)
        val endIndex = getMaxIndex(startIndex, tableElementCount)
        var index = startIndex

        var usedIndex = -1

        val wantedKey = board.zobristKey

        var replacedDepth = Int.MAX_VALUE

        while (index < endIndex) {
            val info = infos[index]
            val key = keys[index]
            val savedKey = key xor info
            // Unpopulated entry
            if (key == 0L && info == 0L) {
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

    fun getUsageSample(): Int {
        var result = 0
        for (index in 0 until 1_000) {
            if (infos[index] != 0L && keys[index] != 0L) {
                result++
            }
        }

        return result
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

        fun calculateTableBits(sizeMb: Int): Int {
            return Square.getSquare(sizeMb) + 16
        }

        fun calculateTableElements(tableBits: Int): Int {
            return if (tableBits == 0) {
                0
            } else {
                Bitboard.getBitboard(tableBits).toInt()
            }
        }

        fun calculateIndexShift(tableBits: Int): Int {
            return Square.SIZE - tableBits
        }

        fun getIndex(zobristKey: Long, indexShift: Int): Int {
            return (zobristKey ushr indexShift).toInt()
        }

        fun getMaxIndex(index: Int, tableSize: Int): Int {
            return min(index + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, tableSize)
        }
    }
}