package pirarucu.hash

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.util.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * Transposition table
 * https://chessprogramming.wikispaces.com/Transposition+Table
 *
 * keys (IntArray)
 * Zobrist key - 32 bits
 *
 * scores (ShortArray) (16 bits)
 * Score - 16 Bits
 *
 * evals (ShortArray) (16 bits)
 * eval - 16 Bits
 *
 * info (LongArray) (64 bits)
 * Depth - 8 bits
 * Score type - 2 bits
 * Unused - 6
 * Move - 16 bits (3 entries)
 *
 * Total bits - 128 bits
 *
 * 1k = 8 Entries
 */
object TranspositionTable {

    private var foundKey: Int = 0
    private var foundScore: Short = 0
    private var foundEval: Short = 0
    private var foundInfo: Long = 0L

    private val tableBits: Int = Square.getSquare(HashConstants.TRANSPOSITION_TABLE_SIZE) + 17
    private val tableLimit: Int = Bitboard.getBitboard(tableBits).toInt()
    private val indexShift: Int = 64 - tableBits

    private val keys = IntArray(tableLimit)
    private val scores = ShortArray(tableLimit)
    private val evals = ShortArray(tableLimit)
    private val infos = LongArray(tableLimit)

    private const val DEPTH_SHIFT = 0
    private const val SCORE_TYPE_SHIFT = 8
    private const val MOVE_SHIFT = 16
    private const val MOVE_SHIFT_1 = MOVE_SHIFT * 1
    private const val MOVE_SHIFT_2 = MOVE_SHIFT * 2
    private const val MOVE_SHIFT_3 = MOVE_SHIFT * 3

    private const val SCORE_TYPE_MASK = 0x3.toLong()
    private const val DEPTH_MASK = GameConstants.MAX_PLIES.toLong() - 1
    private const val MOVE_MASK = 0xFFFF.toLong()
    private const val MOVE_MASK_1 = MOVE_MASK shl MOVE_SHIFT_1
    private const val MOVE_MASK_2 = MOVE_MASK shl MOVE_SHIFT_2
    private const val MOVE_MASK_3 = MOVE_MASK shl MOVE_SHIFT_3
    private const val MOVES_MASK = MOVE_MASK_1 or
        MOVE_MASK_2 or
        MOVE_MASK_3

    private const val MAX_MOVES = 3

    fun reset() {
        Utils.specific.arrayFill(keys, 0)
        Utils.specific.arrayFill(scores, 0)
        Utils.specific.arrayFill(evals, 0)
        Utils.specific.arrayFill(infos, 0)
    }

    fun getScore(ply: Int): Int {
        return getScore(foundScore, ply)
    }

    /**
     * Move number from 0 to 2
     */
    fun getMove(moveNumber: Int): Int {
        return getMove(foundInfo, moveNumber)
    }

    val eval: Int
        get() = foundEval.toInt()

    val scoreType: Int
        get() = getScoreType(foundInfo)

    val depth: Int
        get() = getDepth(foundInfo)

    val hasMove: Boolean
        get() = foundInfo and MOVES_MASK != 0L

    val firstMove: Int
        get() = getMove(0)

    fun findEntry(board: Board): Boolean {
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex
        val wantedKey = board.zobristKey.toInt()
        while (index < maxIndex) {
            // Unpopulated entry
            val key = keys[index]
            if (key == 0) {
                return false
            }
            if (wantedKey == key) {
                foundKey = key
                foundScore = scores[index]
                foundEval = evals[index]
                foundInfo = infos[index]
                return true
            }
            index++
        }

        return false
    }

    fun save(board: Board, eval: Int, score: Int, scoreType: Int, depth: Int, bestMove: Int) {
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex

        var usedIndex = startIndex

        val key = board.zobristKey.toInt()

        var realDepth = depth
        var replacedDepth = depth
        var oldInfo = 0L

        while (index < maxIndex) {
            // Unpopulated entry
            if (keys[index] == 0) {
                usedIndex = index
                break
            }
            val info = infos[index]
            val savedDepth = getDepth(info)

            // Update entry
            if (keys[index] == key) {
                usedIndex = index
                oldInfo = info
                realDepth = max(savedDepth, realDepth)
                break
            }

            // Replace the lowest depth
            if (savedDepth < replacedDepth) {
                usedIndex = index
                oldInfo = info
                replacedDepth = savedDepth
            }
            index++
        }

        val realScore: Int = when {
            score >= EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MAX
            score <= -EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MIN
            else -> score
        }

        keys[usedIndex] = key
        infos[usedIndex] = buildInfo(oldInfo, realDepth, scoreType, bestMove)
        scores[usedIndex] = realScore.toShort()
        evals[usedIndex] = eval.toShort()
    }

    private fun getIndex(board: Board): Int {
        return (board.zobristKey ushr indexShift).toInt()
    }

    private fun getMaxIndex(startIndex: Int): Int {
        return min(startIndex + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, tableLimit)
    }

    private fun getScore(value: Short, ply: Int): Int {
        return when {
            value > EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MAX - ply
            value < -EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MIN + ply
            else -> value.toInt()
        }
    }

    private fun getMove(value: Long, position: Int): Int {
        return ((value ushr (MOVE_SHIFT * (position + 1))) and MOVE_MASK).toInt()
    }

    private fun getScoreType(value: Long): Int {
        return ((value ushr SCORE_TYPE_SHIFT) and SCORE_TYPE_MASK).toInt()
    }

    private fun getDepth(value: Long): Int {
        return (value and DEPTH_MASK).toInt()
    }

    private fun buildInfo(oldInfo: Long, depth: Int, scoreType: Int, move: Int): Long {
        val move1 = (move.toLong() and MOVE_MASK) shl MOVE_SHIFT_1
        val move2 = (move.toLong() and MOVE_MASK) shl MOVE_SHIFT_2

        // Inserting the move in the first position of the stack
        val moveStack = when {
            MOVE_MASK_1 and oldInfo == move1 -> {
                // Move already in first position
                oldInfo and MOVES_MASK
            }
            MOVE_MASK_2 and oldInfo == move2 -> {
                // Move in second position
                move1 or
                    ((oldInfo and MOVE_MASK_1) shl MOVE_SHIFT) or
                    (oldInfo and MOVE_MASK_3)
            }
            else -> {
                ((oldInfo and MOVES_MASK) shl MOVE_SHIFT) or
                    move1
            }
        }
        return depth.toLong() or
            (scoreType.toLong() shl SCORE_TYPE_SHIFT) or
            moveStack
    }
}