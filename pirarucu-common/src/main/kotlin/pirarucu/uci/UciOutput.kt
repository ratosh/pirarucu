package pirarucu.uci


import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.search.SearchInfo

object UciOutput {

    var silent = false

    fun println(line: String) {
        if (!silent) {
            kotlin.io.println(line)
        }
    }

    fun info(line: String) {
        println("info $line")
    }

    fun bestMove(move: Int) {
        println("bestmove " + Move.toString(move))
    }

    fun searchInfo(
        depth: Int,
        time: Long,
        nodeCount: Long,
        searchInfo: SearchInfo
    ) {
        val nps = if (nodeCount > 0 && time > 0) {
            " nps " + nodeCount * 1000 / time
        } else {
            " nps 0"
        }
        println(
            "info depth " + depth +
                " time " + time +
                " score cp " + searchInfo.bestScore +
                nps +
                " nodes " + nodeCount +
                " hashfull " +
                searchInfo.transpositionTable.ttUsage * 1000 / searchInfo.transpositionTable.tableElementCount +
                " pv " + searchInfo.toString()
        )
    }
}