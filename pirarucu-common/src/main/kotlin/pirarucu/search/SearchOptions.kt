package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants
import pirarucu.util.Utils
import kotlin.math.max

class SearchOptions {

    // Search control
    var stop = false
    var startTime = 0L
    var maxSearchTimeLimit = 0L
    var minSearchTimeLimit = 0L

    var minSearchTime = 0L
    var maxSearchTime = 0L
    var searchTimeIncrement = 0L

    var depth = GameConstants.MAX_PLIES - 1

    var movesToGo = 0L

    var whiteTime = 0L
    var blackTime = 0L
    var whiteIncrement = 0L
    var blackIncrement = 0L

    fun setTime(color: Int) {
        val totalTime = if (color == Color.WHITE) {
            whiteTime
        } else {
            blackTime
        }
        val moves = when {
            movesToGo <= 0L -> MAX_GAME_MOVES
            movesToGo < 3L -> MIN_GAME_MOVES
            else -> movesToGo * 2
        }

        maxSearchTime = totalTime - MOVE_OVERHEAD
        minSearchTime = maxSearchTime / moves

        searchTimeIncrement = max(1, (maxSearchTime - minSearchTime) / INCREMENT_RATIO)
    }

    fun startControl() {
        stop = false
        startTime = Utils.specific.currentTimeMillis()
        maxSearchTimeLimit = startTime + maxSearchTime
        minSearchTimeLimit = startTime + minSearchTime
    }

    companion object {
        private const val MIN_GAME_MOVES = 5L
        private const val MAX_GAME_MOVES = 40L

        private const val INCREMENT_RATIO = 30
        private const val MOVE_OVERHEAD = 200
    }
}
