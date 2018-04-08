package pirarucu.hash

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.search.SearchOptions
import pirarucu.stats.Statistics
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
 * infos (ShortArray) (16 bits)
 * Depth - 8 bits
 * Score type - 2 bits
 * Unused - 6
 *
 * moves (LongArray) (64 bits)
 * Move - 16 bits (4 entries)
 *
 * Total bits - 128 bits
 *
 * 1k = 8 Entries
 */
object TranspositionTable {
    var ttUsage = 0L

    var foundKey = 0
    var foundScore = 0
    var foundInfo = 0
    var foundMoves = 0L

    private val tableBits = Square.getSquare(HashConstants.TRANSPOSITION_TABLE_SIZE) + 17
    val tableLimit = Bitboard.getBitboard(tableBits).toInt()
    private val indexShift = 64 - tableBits

    private val keys = IntArray(tableLimit)
    private val scores = ShortArray(tableLimit)
    private val infos = ShortArray(tableLimit)
    private val moves = LongArray(tableLimit)

    private const val DEPTH_SHIFT = 0
    private const val SCORE_TYPE_SHIFT = 8
    private const val MOVE_SHIFT = 16
    private const val MOVE_SHIFT_1 = MOVE_SHIFT * 0
    private const val MOVE_SHIFT_2 = MOVE_SHIFT * 1
    private const val MOVE_SHIFT_3 = MOVE_SHIFT * 2
    private const val MOVE_SHIFT_4 = MOVE_SHIFT * 3

    private const val DEPTH_MASK = GameConstants.MAX_PLIES.toShort() - 1
    private const val SCORE_TYPE_MASK = 0x3
    private const val MOVE_MASK = 0xFFFF.toLong()
    private const val MOVE_MASK_1 = MOVE_MASK shl MOVE_SHIFT_1
    private const val MOVE_MASK_2 = MOVE_MASK shl MOVE_SHIFT_2
    private const val MOVE_MASK_3 = MOVE_MASK shl MOVE_SHIFT_3
    private const val MOVE_MASK_4 = MOVE_MASK shl MOVE_SHIFT_4

    const val MAX_MOVES = 4

    fun reset() {
        ttUsage = 0

        Utils.specific.arrayFill(keys, 0)
        Utils.specific.arrayFill(scores, 0)
        Utils.specific.arrayFill(infos, 0)
        Utils.specific.arrayFill(moves, 0)
    }

    fun findEntry(board: Board): Boolean {
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex
        val wantedKey = board.zobristKey.toInt()
        while (index < maxIndex) {
            // Unpopulated entry
            val key = keys[index]
            if (key == 0) {
                break
            }
            if (wantedKey == key) {
                if (Statistics.ENABLED) {
                    Statistics.ttHits++
                }
                foundKey = key
                foundScore = scores[index].toInt()
                foundInfo = infos[index].toInt()
                foundMoves = moves[index]
                return true
            }
            index++
        }
        if (Statistics.ENABLED) {
            Statistics.ttMisses++
        }
        return false
    }

    fun save(board: Board, score: Int, scoreType: Int, depth: Int, ply: Int, bestMove: Int) {
        if (SearchOptions.stop) {
            return
        }
        val startIndex = getIndex(board)
        val maxIndex = getMaxIndex(startIndex)
        var index = startIndex

        var usedIndex = startIndex

        val key = board.zobristKey.toInt()

        var realDepth = depth
        var replacedDepth = depth
        var oldMoves = 0L

        while (index < maxIndex) {
            // Unpopulated entry
            if (keys[index] == 0) {
                ttUsage++
                usedIndex = index
                break
            }
            val savedDepth = getDepth(infos[index].toInt())

            // Update entry
            if (keys[index] == key) {
                usedIndex = index
                realDepth = max(savedDepth, realDepth)
                oldMoves = moves[usedIndex]
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

        keys[usedIndex] = key
        infos[usedIndex] = buildInfo(realDepth, scoreType)
        scores[usedIndex] = realScore.toShort()
        moves[usedIndex] = buildMoves(oldMoves, bestMove)
    }

    private fun getIndex(board: Board): Int {
        return (board.zobristKey ushr indexShift).toInt()
    }

    private fun getMaxIndex(startIndex: Int): Int {
        return min(startIndex + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, tableLimit)
    }

    fun getScore(value: Int, ply: Int): Int {
        return when {
            value > EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MAX - ply
            value < -EvalConstants.SCORE_MATE -> EvalConstants.SCORE_MIN + ply
            else -> value
        }
    }

    fun getFirstMove(value: Long): Int {
        return getMove(value, 0)
    }

    fun getMove(value: Long, position: Int): Int {
        return ((value ushr (MOVE_SHIFT * position)) and MOVE_MASK).toInt()
    }

    fun getScoreType(value: Int): Int {
        return (value ushr SCORE_TYPE_SHIFT) and SCORE_TYPE_MASK
    }

    fun getDepth(value: Int): Int {
        return value and DEPTH_MASK
    }

    private fun buildInfo(depth: Int, scoreType: Int): Short {
        return (depth or (scoreType shl SCORE_TYPE_SHIFT)).toShort()
    }

    private fun buildMoves(oldMoves: Long, move: Int): Long {
        val move1 = (move.toLong() and MOVE_MASK) shl MOVE_SHIFT_1
        val move2 = (move.toLong() and MOVE_MASK) shl MOVE_SHIFT_2
        val move3 = (move.toLong() and MOVE_MASK) shl MOVE_SHIFT_3

        // Inserting the move in the first position of the stack
        return when {
            MOVE_MASK_1 and oldMoves == move1 -> {
                // Move already in first position
                oldMoves
            }
            MOVE_MASK_2 and oldMoves == move2 -> {
                // Move in second position
                move1 or
                    ((oldMoves and MOVE_MASK_1) shl MOVE_SHIFT) or
                    (oldMoves and MOVE_MASK_3) or
                    (oldMoves and MOVE_MASK_4)
            }
            MOVE_MASK_3 and oldMoves == move3 -> {
                // Move in third position
                move1 or
                    ((oldMoves and (MOVE_MASK_1 or MOVE_MASK_2)) shl MOVE_SHIFT) or
                    (oldMoves and MOVE_MASK_4)
            }
            else -> {
                (oldMoves shl MOVE_SHIFT) or move1
            }
        }
    }
}