package pirarucu.util

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList

object Perft {

    private val attackInfo = AttackInfo()
    private val moveList = MoveList()

    fun perft(board: Board, depth: Int): Long {
        var nodes: Long = 0

        if (depth == 0) {
            return 1L
        }

        moveList.startPly()
        MoveGenerator.legalMoves(board, attackInfo, moveList)
        MoveGenerator.legalAttacks(board, attackInfo, moveList)
        
        while (moveList.hasNext()) {
            val move = moveList.next()
            if (board.isLegalMove(move)) {
                board.doMove(move)
                nodes += perft(board, depth - 1)
                board.undoMove(move)
            }
        }

        moveList.endPly()
        return nodes
    }
}