package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants
import kotlin.math.max

class SearchOptions {

    var minSearchTimeLimit = 0L
    var maxSearchTimeLimit = 0L
    var searchTimeIncrement = 0L

    var depth = GameConstants.MAX_PLIES - 1

    var stop = false

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
            movesToGo != 0L -> movesToGo * 2
            else -> GAME_MOVES
        }

        minSearchTimeLimit = totalTime / moves
        maxSearchTimeLimit = when (movesToGo) {
            1L -> (totalTime - 1000)
            else -> (minSearchTimeLimit * MAX_TIME_RATIO).toLong()
        }

        searchTimeIncrement = max(1, (maxSearchTimeLimit - minSearchTimeLimit) / INCREMENT_RATIO)
    }

    companion object {
        private const val MAX_TIME_RATIO = 2.5
        private const val GAME_MOVES = 40L

        private const val INCREMENT_RATIO = 20
    }
}
