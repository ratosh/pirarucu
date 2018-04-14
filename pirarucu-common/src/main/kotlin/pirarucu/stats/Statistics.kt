package pirarucu.stats

import pirarucu.hash.TranspositionTable

object Statistics {

    var ENABLED = false

    var ttHits = 0L
    var ttMisses = 0L

    var qNodes = 0L
    var qRenodes = 0L
    var qDraw = 0L
    var qTTEntry = 0L
    var qStandpat = 0L
    var qFutility = 0L
    var qFailHigh = 0L
    var qMaxPly = 0

    var gMoves = 0L
    var moves = 0L

    var abSearch = 0L
    var pvSearch = 0L
    var TTEntry = 0L

    var prunable = 0L
    var futility = 0L

    var futilityHit = 0L
    var razoring = 0L

    var razoringHit = 0L
    var nullMove = 0L

    var nullMoveHit = 0L
    var mate = 0L
    var stalemate = 0L

    var pvs = 0L
    var pvsHits = 0L

    fun reset() {
        ttHits = 0
        ttMisses = 0

        qNodes = 0
        qRenodes = 0
        qDraw = 0
        qTTEntry = 0
        qStandpat = 0
        qFutility = 0
        qFailHigh = 0
        qMaxPly = 0

        gMoves = 0
        moves = 0

        abSearch = 0
        pvSearch = 0
        TTEntry = 0

        prunable = 0L

        razoring = 0L
        razoringHit = 0L

        futility = 0L
        futilityHit = 0L

        nullMove = 0L
        nullMoveHit = 0L

        mate = 0L
        stalemate = 0L

        pvs = 0L
        pvsHits = 0L
    }

    override fun toString(): String {
        val buffer = StringBuilder()

        buffer.append("--- Main Search\n")
        buffer.append("MS abSearch: $abSearch\n")
        buffer.append("MS pvSearch: $pvSearch\n")
        buffer.append("MS TTEntry: " + buildPercentage(TTEntry, abSearch) + "\n")

        buffer.append("--- Pruning $prunable\n")
        buffer.append("MS futility: " + buildPercentage(futilityHit, futility) + "\n")
        buffer.append("MS razoring: " + buildPercentage(razoringHit, razoring) + "\n")
        buffer.append("MS nullMove: " + buildPercentage(nullMoveHit, nullMove) + "\n")

        buffer.append("--- Other \n")
        buffer.append("MS PVS: " + buildPercentage(pvsHits, pvs) + "\n")

        buffer.append("--- End conditions \n")
        buffer.append("Mates: " + buildPercentage(mate, moves) + "\n")
        buffer.append("Stalemates: " + buildPercentage(stalemate, moves) + "\n")

        buffer.append("--- Quiescence Search\n")
        buffer.append("QS Draw: " + buildPercentage(qDraw, qNodes) + " \n")
        buffer.append("QS TTEntry: " + buildPercentage(qTTEntry, qNodes) + " \n")
        buffer.append("QS Standpat: " + buildPercentage(qStandpat, qNodes) + " \n")
        buffer.append("QS Futility: " + buildPercentage(qFutility, qNodes) + " \n")
        buffer.append("QS FailHigh: " + buildPercentage(qFailHigh, qRenodes) + " \n")
        buffer.append("QS Nodes: $qNodes\n")
        buffer.append("QS Renodes: $qRenodes\n")
        buffer.append("QS Max ply: $qMaxPly\n")

        buffer.append("--- Transposition table\n")
        buffer.append("TT hit: " + buildPercentage(ttHits, ttHits + ttMisses) + "\n")
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