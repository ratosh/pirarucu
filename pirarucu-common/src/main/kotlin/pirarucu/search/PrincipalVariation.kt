package pirarucu.search

import pirarucu.board.Board
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.util.Utils

object PrincipalVariation {

    val bestMoveList = IntArray(GameConstants.MAX_PLIES)
    var bestScore = 0

    var maxDepth = 0

    fun reset() {
        Utils.Companion.specific.arrayFill(bestMoveList, 0)
        bestScore = 0
        maxDepth = 0
    }

    fun save(board: Board) {
        var ply = 0
        while (ply < GameConstants.MAX_PLIES - 1 && TranspositionTable.findEntry(board)) {
            if (ply == 0) {
                bestScore = TranspositionTable.getScore(1)
            }
            val bestMove = TranspositionTable.firstMove
            if (bestMove != Move.NONE) {
                bestMoveList[ply] = bestMove
                maxDepth = ply + 1
                board.doMove(bestMove)
            } else {
                break
            }
            ply++
        }
        bestMoveList[ply] = Move.NONE
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
