package pirarucu.stats

import pirarucu.hash.TranspositionTable

object Statistics {

    var searchNodes = 0L
    var mainNodes = 0L
    var qNodes = 0L

    var gMoves = 0L
    var moves = 0L

    fun reset() {
        searchNodes = 0

        mainNodes = 0
        qNodes = 0

        gMoves = 0
        moves = 0
    }

    override fun toString(): String {
        val buffer = StringBuilder()

        buffer.append("--- Search\n")
        buffer.append("Main search: " + buildPercentage(mainNodes, searchNodes) + "\n")
        buffer.append("Q search: " + buildPercentage(qNodes, searchNodes) + "\n")

        buffer.append("--- Transposition table\n")
        buffer.append("TT Occupation: " + buildPercentage(TranspositionTable.ttUsage,
            TranspositionTable.tableLimit.toLong()) + "\n")

        buffer.append("--- Moves\n")
        buffer.append("Moves: " + buildPercentage(moves, gMoves) + "\n")

        return buffer.toString()
    }


    private fun buildPercentage(hitCount: Long, tryCount: Long): String {
        return if (tryCount != 0L) {
            hitCount.toString() + "/" + tryCount + " (" + hitCount * 100 / tryCount + "%)"
        } else {
            "none"
        }

    }
}