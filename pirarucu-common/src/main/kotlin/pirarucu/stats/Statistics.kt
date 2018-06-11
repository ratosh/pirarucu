package pirarucu.stats

import pirarucu.hash.TranspositionTable
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils

object Statistics {

    const val ENABLED = false

    var searchNodes = 0L

    var ttHits = 0L
    var ttMisses = 0L

    var pawnCache = 0L
    var pawnCacheHit = 0L

    var qNodes = 0L
    var qRenodes = 0L
    var qDraw = 0L
    var qTTEntry = 0L
    var qStandpat = 0L
    var qFutilityHit1 = 0L
    var qFutilityHit2 = 0L
    var qFailHigh = 0L
    var qMaxPly = 0

    var seeNodes = 0L
    var seeHits = 0L

    var gMoves = 0L
    var moves = 0L

    var abNodes = 0L
    var pvSearch = 0L
    var TTEntry = 0L

    var prunable = 0L

    var childFutility = LongArray(TunableConstants.FUTILITY_CHILD_MARGIN.size)
    var childFutilityHit = LongArray(TunableConstants.FUTILITY_CHILD_MARGIN.size)

    var razoring = LongArray(TunableConstants.RAZOR_MARGIN.size)
    var razoringHit = LongArray(TunableConstants.RAZOR_MARGIN.size)

    var nullMove = 0L
    var nullMoveHit = 0L

    var mate = 0L
    var stalemate = 0L

    var parentFutility = 0L
    var parentFutilityHit = 0L

    var negativeSee = 0L
    var negativeSeeHit = 0L

    var lmr = 0L
    var lmrHit = 0L

    var pvs = 0L
    var pvsHit = 0L

    var killer1 = 0L
    var killer1Hit = 0L

    var killer2 = 0L
    var killer2Hit = 0L

    fun reset() {
        searchNodes = 0L

        ttHits = 0
        ttMisses = 0

        pawnCache = 0L
        pawnCacheHit = 0L

        qNodes = 0
        qRenodes = 0
        qDraw = 0
        qTTEntry = 0
        qStandpat = 0
        qFutilityHit1 = 0
        qFutilityHit2 = 0
        qFailHigh = 0
        qMaxPly = 0

        seeNodes = 0
        seeHits = 0

        gMoves = 0
        moves = 0

        abNodes = 0
        pvSearch = 0
        TTEntry = 0

        prunable = 0L

        Utils.specific.arrayFill(childFutility, 0L)
        Utils.specific.arrayFill(childFutilityHit, 0L)

        Utils.specific.arrayFill(razoring, 0L)
        Utils.specific.arrayFill(razoringHit, 0L)

        nullMove = 0L
        nullMoveHit = 0L

        mate = 0L
        stalemate = 0L

        parentFutility = 0L
        parentFutilityHit = 0L

        negativeSee = 0L
        negativeSeeHit = 0L

        lmr = 0L
        lmrHit = 0L

        pvs = 0L
        pvsHit = 0L

        killer1 = 0L
        killer1Hit = 0L

        killer2 = 0L
        killer2Hit = 0L
    }

    override fun toString(): String {
        val buffer = StringBuilder()

        buffer.append("--- Main Search\n")
        buffer.append("MS abNodes: $abNodes\n")
        buffer.append("MS pvSearch: $pvSearch\n")
        buffer.append("MS TTEntry: " + buildPercentage(TTEntry, abNodes) + "\n")

        buffer.append("--- Pruning $prunable\n")
        for (index in childFutility.indices) {
            buffer.append("MS C FUT[$index]: " + buildPercentage(childFutilityHit[index], childFutility[index]) + "\n")
        }
        for (index in razoring.indices) {
            buffer.append("MS razoring[$index]: " + buildPercentage(razoringHit[index], razoring[index]) + "\n")
        }
        buffer.append("MS nullMove: " + buildPercentage(nullMoveHit, nullMove) + "\n")
        buffer.append("MS P FUT: " + buildPercentage(parentFutilityHit, parentFutility) + "\n")
        buffer.append("MS NSEE: " + buildPercentage(negativeSeeHit, negativeSee) + "\n")

        buffer.append("--- Other \n")
        buffer.append("MS LMR: " + buildPercentage(lmrHit, lmr) + "\n")
        buffer.append("MS PVS: " + buildPercentage(pvsHit, pvs) + "\n")
        buffer.append("MS K1: " + buildPercentage(killer1Hit, killer1) + "\n")
        buffer.append("MS K2: " + buildPercentage(killer2Hit, killer2) + "\n")

        buffer.append("--- End conditions \n")
        buffer.append("Mates: " + buildPercentage(mate, moves) + "\n")
        buffer.append("Stalemates: " + buildPercentage(stalemate, moves) + "\n")

        buffer.append("--- Quiescence Search\n")
        buffer.append("QS SEE pruning: " + buildPercentage(seeHits, seeNodes) + " \n")
        buffer.append("QS Draw: " + buildPercentage(qDraw, qNodes) + " \n")
        buffer.append("QS TTEntry: " + buildPercentage(qTTEntry, qNodes) + " \n")
        buffer.append("QS Standpat: " + buildPercentage(qStandpat, qNodes) + " \n")
        buffer.append("QS Futility1: " + buildPercentage(qFutilityHit1, qNodes) + " \n")
        buffer.append("QS Futility2: " + buildPercentage(qFutilityHit2, qRenodes) + " \n")
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