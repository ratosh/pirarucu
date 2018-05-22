package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants
import kotlin.math.max

object SearchOptions {
    private const val MAX_GAME_MOVES = 45
    private const val MIN_GAME_MOVES = 20

    private const val INCREMENT_RATIO = 10

    var minSearchTimeLimit = 0
    var maxSearchTimeLimit = 0
    var searchTimeIncrement = 0

    var depth = GameConstants.MAX_PLIES - 1

    var stop = false

    var whiteTime: Int = 0
    var blackTime: Int = 0
    var whiteIncrement: Int = 0
    var blackIncrement: Int = 0

    fun reset() {
        stop = false
    }

    fun setTime(color: Int) {
        val totalTime = if (color == Color.WHITE) {
            whiteTime
        } else {
            blackTime
        }

        minSearchTimeLimit = totalTime / MAX_GAME_MOVES
        maxSearchTimeLimit = totalTime / MIN_GAME_MOVES
        searchTimeIncrement = max(1, (maxSearchTimeLimit - minSearchTimeLimit) / INCREMENT_RATIO)
    }
}