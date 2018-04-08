package pirarucu.uci

import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.search.PrincipalVariation
import pirarucu.stats.Statistics

object UciOutput {

    fun println(line: String) {
        kotlin.io.println(line)
    }

    fun bestMove(move: Int) {
        println("bestmove " + Move.toString(move))
    }

    fun searchInfo(depth: Int, time: Long) {
        val nps = if (Statistics.moves > 0 && time > 0) {
            " nps " + Statistics.moves * 1000 / time
        } else {
            " nps 0"
        }
        println("info depth " + depth +
            " time " + time +
            " score cp " + PrincipalVariation.bestScore +
            nps +
            " nodes " + Statistics.moves +
            " hashfull " + TranspositionTable.ttUsage * 1000 / TranspositionTable.tableLimit +
            " pv " + PrincipalVariation.toString())
    }

}