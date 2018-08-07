package pirarucu.search

import pirarucu.board.Color
import pirarucu.board.Square
import pirarucu.move.Move
import pirarucu.util.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object History {
    val history = Array(Color.SIZE) { IntArray(Square.SIZE * Square.SIZE) }

    fun getHistoryScore(color: Int, move: Int): Int {
        return history[color][Move.getFromTo(move)]
    }

    // Ethereal based history
    fun addHistory(color: Int, move: Int, value: Int) {
        val fromTo = Move.getFromTo(move)

        val colour = color

        val delta = max(-400, min(400, value))

        var entry = history[colour][fromTo]
        entry += 32 * delta - entry * abs(delta) / 512
        history[colour][fromTo] = entry
    }

    fun reset() {
        Utils.specific.arrayFill(history[0], 0)
        Utils.specific.arrayFill(history[1], 0)
    }
}