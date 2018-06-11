package pirarucu.move

import pirarucu.game.GameConstants
import pirarucu.stats.Statistics
import pirarucu.util.Utils

/**
 * https://chessprogramming.wikispaces.com/Move%20List
 */
class MoveList {

    private val moves = IntArray(GameConstants.MAX_MOVES)
    private val nextToMove = IntArray(GameConstants.MAX_PLIES)
    private val nextToGenerate = IntArray(GameConstants.MAX_PLIES)

    var currentPly: Int = 0
        private set

    fun startPly(): Boolean {
        if (currentPly >= GameConstants.MAX_PLIES - 1) {
            return false
        }
        nextToGenerate[currentPly + 1] = nextToGenerate[currentPly]
        nextToMove[currentPly + 1] = nextToGenerate[currentPly]
        currentPly++
        return true
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

    fun contains(move: Int): Boolean {
        val left = nextToMove[currentPly]
        val right = nextToGenerate[currentPly]
        for (index in left..right) {
            if (moves[index] == move) {
                return true
            }
        }
        return false
    }

    fun skipMoves() {
        nextToMove[currentPly] = nextToGenerate[currentPly]
    }

    override fun toString(): String {
        val left = nextToMove[currentPly]
        val right = nextToGenerate[currentPly]
        val buffer = StringBuilder()
        for (index in left until right) {
            buffer.append(Move.toString(moves[index]))
            buffer.append(' ')
        }
        return buffer.toString()
    }
}
