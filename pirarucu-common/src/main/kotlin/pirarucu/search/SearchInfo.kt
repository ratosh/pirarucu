package pirarucu.search

import pirarucu.board.Board
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.util.Utils

class SearchInfo {

    var searchNodes = 0L
    val history = History()
    val moveGenerator = MoveGenerator(history)
    val plyInfoList = Array(GameConstants.MAX_PLIES + 4) { SearchPlyInfo(it, moveGenerator) }

    fun reset() {
        searchNodes = 0
        for (info in plyInfoList) {
            info.clear()
        }
        Utils.specific.arrayFill(bestMoveList, 0)
        bestMove = Move.NONE
        bestScore = 0
    }

    var bestMove = Move.NONE
    private val bestMoveList = IntArray(PV_DEPTH)
    var bestScore = 0

    fun save(board: Board) {
        var ply = 0
        while (ply < PV_DEPTH) {
            val info = TranspositionTable.findEntry(board)
            if (info == TranspositionTable.EMPTY_INFO) {
                break
            }
            val firstMove = TranspositionTable.getMove(info)
            if (firstMove != Move.NONE) {
                if (ply == 0) {
                    bestMove = firstMove
                    bestScore = TranspositionTable.getScore(info, ply)
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

    companion object {
        private const val PV_DEPTH = 10
    }
}