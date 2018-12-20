package pirarucu.search

import pirarucu.board.Color
import pirarucu.board.Square
import pirarucu.move.Move
import pirarucu.util.PlatformSpecific
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class History {
    private val history = Array(Color.SIZE) { IntArray(Square.SIZE * Square.SIZE) }

    fun reset() {
        PlatformSpecific.arrayFill(history, 0)
    }

    fun getHistoryScore(color: Int, move: Int): Int {
        return history[color][Move.getFromTo(move)]
    }

    // Ethereal based history
    fun addHistory(color: Int, move: Int, value: Int) {
        val fromTo = Move.getFromTo(move)

        val delta = max(-400, min(400, value))

        var entry = history[color][fromTo]
        entry += 32 * delta - entry * abs(delta) / 512
        history[color][fromTo] = entry
    }
}