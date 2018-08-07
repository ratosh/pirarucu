package pirarucu.move

import pirarucu.game.GameConstants
import pirarucu.util.Utils

/**
 * https://chessprogramming.wikispaces.com/Move%20List
 */
class MoveList {

    private val moves = IntArray(GameConstants.MAX_MOVES)
    private val scores = IntArray(GameConstants.MAX_MOVES)
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
        var bestScore = INVALID_SCORE
        var bestIndex = -1
        val next = nextToMove[currentPly]
        for (i in next until nextToGenerate[currentPly]) {
            val score = scores[i]
            if (score > bestScore) {
                bestScore = score
                bestIndex = i
            }
        }
        val move = moves[bestIndex]
        if (bestIndex != next) {
            moves[bestIndex] = moves[next]
            scores[bestIndex] = scores[next]
        }
        nextToMove[currentPly]++
        return move
    }

    operator fun hasNext(): Boolean {
        return nextToGenerate[currentPly] != nextToMove[currentPly]
    }

    fun addMove(move: Int, score: Int) {
        scores[nextToGenerate[currentPly]] = score
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

    companion object {
        const val INVALID_SCORE = Int.MIN_VALUE
    }
}
