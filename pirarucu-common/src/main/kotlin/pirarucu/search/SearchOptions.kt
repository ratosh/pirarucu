package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants

object SearchOptions {
    private const val GAME_MOVES = 50

    var minSearchTimeLimit = 0
    var extraPanicTimeLimit = 0
    var maxSearchTimeLimit = 0

    var depth = GameConstants.MAX_PLIES - 1
    var panicEnabled = true

    var stop = false
    var panic = false

    var whiteTime: Int = 0
    var blackTime: Int = 0
    var whiteIncrement: Int = 0
    var blackIncrement: Int = 0

    private const val PANIC_RATIO = 1.5f
    private const val MAX_TIME_RATIO = 2f

    fun reset() {
        stop = false
        panic = false
    }

    fun setTime(color: Int) {
        val totalTime = if (color == Color.WHITE) {
            whiteTime / GAME_MOVES
        } else {
            blackTime / GAME_MOVES
        }

        minSearchTimeLimit = totalTime
        extraPanicTimeLimit = (minSearchTimeLimit * PANIC_RATIO).toInt()
        maxSearchTimeLimit = (totalTime * MAX_TIME_RATIO).toInt()
    }
}