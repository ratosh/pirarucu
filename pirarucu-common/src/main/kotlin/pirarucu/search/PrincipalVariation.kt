package pirarucu.search

import pirarucu.board.Board
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.util.Utils

object PrincipalVariation {

    private const val PV_DEPTH = 10

    var bestMove = Move.NONE
    val bestMoveList = IntArray(PV_DEPTH)
    var bestScore = 0

    fun reset() {
        Utils.Companion.specific.arrayFill(bestMoveList, 0)
        bestMove = Move.NONE
        bestScore = 0
    }

    fun save(board: Board) {
        var ply = 0
        while (ply < PV_DEPTH && TranspositionTable.findEntry(board)) {
            val moves = TranspositionTable.foundMoves
            val score = TranspositionTable.foundScore
            val firstMove = TranspositionTable.getFirstMove(moves)
            if (firstMove != Move.NONE) {
                if (ply == 0) {
                    bestMove = firstMove
                    bestScore = score
                }
                bestMoveList[ply] = firstMove
                board.doMove(firstMove)
            } else {
                break
            }
            ply++
        }
        if (ply < PV_DEPTH) {
            bestMoveList[ply] = Move.NONE
        }
        while (ply > 0) {
            ply--
            board.undoMove(bestMoveList[ply])
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for (move in bestMoveList) {
            if (move == Move.NONE) {
                break
            }
            result.append(Move.toString(move)).append(" ")
        }
        return result.toString()
    }
}
