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

    fun searchInfo(depth: Int, time: Long, searchInfo: SearchInfo) {
        val nps = if (searchInfo.searchNodes > 0 && time > 0) {
            " nps " + searchInfo.searchNodes * 1000 / time
        } else {
            " nps 0"
        }
        println("info depth " + depth +
            " time " + time +
            " score cp " + searchInfo.bestScore +
            nps +
            " nodes " + searchInfo.searchNodes +
            " hashfull " + TranspositionTable.ttUsage * 1000 / TranspositionTable.tableLimit +
            " pv " + searchInfo.toString())
    }
}