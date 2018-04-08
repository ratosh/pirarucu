package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants

object SearchOptions {
    private const val GAME_MOVES = 50

    var searchTimeLimit = 0
    var extraPanicTimeLimit = 0

    var depth = GameConstants.MAX_PLIES
    var panicEnabled = true

    var stop = false
    var panic = false

    var whiteTime: Int = 0
    var blackTime: Int = 0
    var whiteIncrement: Int = 0
    var blackIncrement: Int = 0

    private const val PANIC_RATIO = 0.5f

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

        searchTimeLimit = totalTime
        extraPanicTimeLimit = (searchTimeLimit * PANIC_RATIO).toInt()
    }
}