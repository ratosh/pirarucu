package pirarucu.move

import pirarucu.game.GameConstants

/**
 * https://chessprogramming.wikispaces.com/Move%20List
 */
class SimpleMoveList {

    private val moves = IntArray(GameConstants.MAX_PLY_MOVES)
    private var nextToMove = 0
    private var nextToGenerate = 0

    fun reset() {
        nextToMove = 0
        nextToGenerate = 0
    }

    fun restart() {
        nextToMove = 0
    }

    fun next(): Int {
        return moves[nextToMove++]
    }

    fun hasNext(): Boolean {
        return nextToGenerate != nextToMove
    }

    fun addMove(move: Int) {
        moves[nextToGenerate++] = move
    }

    fun contains(move: Int): Boolean {
        val left = nextToMove
        val right = nextToGenerate
        for (index in left..right) {
            if (moves[index] == move) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        val left = nextToMove
        val right = nextToGenerate
        val buffer = StringBuilder()
        for (index in left until right) {
            buffer.append(Move.toString(moves[index]))
            buffer.append(' ')
        }
        return buffer.toString()
    }
}
