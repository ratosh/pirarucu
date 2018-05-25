package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants
import kotlin.math.max

object SearchOptions {
    private const val MAX_TIME_RATIO = 2.5
    private const val GAME_MOVES = 40

    private const val INCREMENT_RATIO = 20

    var minSearchTimeLimit = 0
    var maxSearchTimeLimit = 0
    var searchTimeIncrement = 0

    var depth = GameConstants.MAX_PLIES - 1

    var stop = false

    var movesToGo = 0

    var whiteTime = 0
    var blackTime = 0
    var whiteIncrement = 0
    var blackIncrement = 0

    fun setTime(color: Int) {
        val totalTime = if (color == Color.WHITE) {
            whiteTime
        } else {
            blackTime
        }
        val moves = when {
            movesToGo != 0 -> movesToGo * 2
            else -> GAME_MOVES
        }

        minSearchTimeLimit = totalTime / moves
        maxSearchTimeLimit = when (movesToGo) {
            1 -> (totalTime - 1000)
            else -> (minSearchTimeLimit * MAX_TIME_RATIO).toInt()
        }

        searchTimeIncrement = max(1, (maxSearchTimeLimit - minSearchTimeLimit) / INCREMENT_RATIO)
    }
}
