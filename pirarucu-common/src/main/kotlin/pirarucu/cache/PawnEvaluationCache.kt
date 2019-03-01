package pirarucu.cache

import pirarucu.board.Board
import pirarucu.eval.EvalConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.util.PlatformSpecific

/**
 * 160 Bits per cache entry
 */
class PawnEvaluationCache(sizeMb: Int) {

    private var currentHashSize = sizeMb

    private var tableBits = TranspositionTable.calculateTableBits(sizeMb)
    var tableElementCount = TranspositionTable.calculateTableElements(tableBits)
    private var indexShift = TranspositionTable.calculateIndexShift(tableBits)

    private var keys = LongArray(tableElementCount)
    private var values = IntArray(tableElementCount)
    private var passed = LongArray(tableElementCount)

    constructor() : this(HashConstants.PAWN_HASH_DEFAULT_SIZE)

    fun resize(sizeMb: Int) {
        if (currentHashSize == sizeMb) {
            return
        }
        currentHashSize = sizeMb
        tableBits = TranspositionTable.calculateTableBits(sizeMb)
        tableElementCount = TranspositionTable.calculateTableElements(tableBits)
        indexShift = TranspositionTable.calculateIndexShift(tableBits)

        keys = LongArray(tableElementCount)
        values = IntArray(tableElementCount)
        passed = LongArray(tableElementCount)
    }

    fun reset() {
        PlatformSpecific.arrayFill(keys, 0)
    }

    fun findEntry(board: Board): Int {
        val wantedKey = getKey(board)
        val index = getIndex(wantedKey)
        val passedPawnInfo = passed[index]
        val value = values[index]
        val savedKey = keys[index] xor passedPawnInfo xor value.toLong()
        if (savedKey == wantedKey) {
            board.evalInfo.passedPawnBitboard = passedPawnInfo
            return value
        }
        return EvalConstants.SCORE_UNKNOWN
    }

    fun saveEntry(board: Board, value: Int, passedPawnBitboard: Long) {
        if (EvalConstants.PAWN_EVAL_CACHE) {
            val key = getKey(board)
            val index = getIndex(key)
            keys[index] = key xor value.toLong() xor passedPawnBitboard
            values[index] = value
            passed[index] = passedPawnBitboard
        }
    }

    private fun getKey(board: Board): Long {
        return board.pawnZobristKey
    }

    private fun getIndex(key: Long): Int {
        return (key ushr indexShift).toInt()
    }

}