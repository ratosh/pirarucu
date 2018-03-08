package pirarucu.move

import pirarucu.game.GameConstants
import pirarucu.util.Utils

/**
 * https://chessprogramming.wikispaces.com/Move%20List
 */
class MoveList {

    private val moves = IntArray(GameConstants.MAX_MOVES)
    private val nextToMove = IntArray(GameConstants.MAX_PLIES + GameConstants.PLIES_EXTENDED)
    private val nextToGenerate = IntArray(GameConstants.MAX_PLIES + GameConstants.PLIES_EXTENDED)

    var currentPly: Int = 0
        private set

    fun startPly() {
        nextToGenerate[currentPly + 1] = nextToGenerate[currentPly]
        nextToMove[currentPly + 1] = nextToGenerate[currentPly]
        currentPly++
    }

    fun endPly() {
        currentPly--
    }

    operator fun next(): Int {
        return moves[nextToMove[currentPly]++]
    }

    operator fun hasNext(): Boolean {
        return nextToGenerate[currentPly] != nextToMove[currentPly]
    }

    fun addMove(move: Int) {
        moves[nextToGenerate[currentPly]++] = move
    }

    fun movesLeft(): Int {
        return nextToGenerate[currentPly] - nextToMove[currentPly]
    }

    fun sort() {
        val left = nextToMove[currentPly]
        val right = nextToGenerate[currentPly]
        if (left < right) {
            Utils.specific.arraySort(moves, left, right)
        }
    }

    fun skipMoves() {
        nextToMove[currentPly] = nextToGenerate[currentPly]
    }

    fun getString(): String {
        val left = nextToMove[currentPly]
        val right = nextToGenerate[currentPly]
        val buffer = StringBuilder()
        for (index in left until right) {
            buffer.append(Move.toString(moves[index]))
            buffer.append('\n')
        }
        return buffer.toString()
    }
}
