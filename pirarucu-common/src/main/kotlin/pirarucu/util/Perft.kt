package pirarucu.util

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.game.GameConstants
import pirarucu.move.MoveGenerator
import pirarucu.move.OrderedMoveList
import pirarucu.search.History

object Perft {

    private val attackInfo = AttackInfo()
    private val moveGenerator = MoveGenerator(History())
    private val orderedMoveList = Array(GameConstants.MAX_PLIES) { OrderedMoveList() }

    fun perft(board: Board, depth: Int): Long {
        var nodes: Long = 0

        if (depth == 0) {
            return 1L
        }

        val moveList = orderedMoveList[depth]
        moveList.reset()

        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()
            if (board.isLegalMove(move)) {
                board.doMove(move)
                nodes += perft(board, depth - 1)
                board.undoMove(move)
            }
        }

        return nodes
    }
}