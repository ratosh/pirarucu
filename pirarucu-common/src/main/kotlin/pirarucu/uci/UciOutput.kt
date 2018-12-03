package pirarucu.uci


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
        info(
            "depth " + depth +
                " time " + time +
                " score cp " + searchInfo.bestScore +
                nps +
                " nodes " + nodeCount +
                " pv " + searchInfo.toString()
        )
    }

    fun hashfullInfo(
        searchInfo: SearchInfo
    ) {
        info("hashfull ${searchInfo.transpositionTable.getUsageSample()}")
    }
}