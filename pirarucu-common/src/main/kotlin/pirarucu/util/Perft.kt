package pirarucu.util

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.OrderedMoveList
import pirarucu.search.History

object Perft {

    private val attackInfo = AttackInfo()
    private val moveGenerator = MoveGenerator(History())

    fun perft(board: Board, depth: Int): Long {
        var nodes: Long = 0

        if (depth == 0) {
            return 1L
        }

        val orderedMoveList = OrderedMoveList()
        moveGenerator.legalMoves(board, attackInfo, orderedMoveList)
        moveGenerator.legalAttacks(board, attackInfo, orderedMoveList)

        while (orderedMoveList.hasNext()) {
            val move = orderedMoveList.next()
            if (board.isLegalMove(move)) {
                board.doMove(move)
                nodes += perft(board, depth - 1)
                board.undoMove(move)
            }
        }

        return nodes
    }
}