package pirarucu.uci


import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.search.PrincipalVariation
import pirarucu.stats.Statistics

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

    fun searchInfo(depth: Int, time: Long) {
        val nps = if (Statistics.searchNodes > 0 && time > 0) {
            " nps " + Statistics.searchNodes * 1000 / time
        } else {
            " nps 0"
        }
        println("info depth " + depth +
            " time " + time +
            " score cp " + PrincipalVariation.bestScore +
            nps +
            " nodes " + Statistics.searchNodes +
            " hashfull " + TranspositionTable.ttUsage * 1000 / TranspositionTable.tableLimit +
            " pv " + PrincipalVariation.toString())
    }
}