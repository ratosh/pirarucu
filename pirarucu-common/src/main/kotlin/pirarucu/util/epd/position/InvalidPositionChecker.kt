package pirarucu.util.epd.position

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.OrderedMoveList
import pirarucu.search.History

class InvalidPositionChecker {
    private val moveGenerator = MoveGenerator(History())

    private val board = Board()

    private val moveList = OrderedMoveList()
    private val attackInfo = AttackInfo()

    fun isValid(fenPosition: String): Boolean {
        moveList.reset()

        BoardFactory.setBoard(fenPosition, board)

        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                return true
            }
        }

        return false
    }
}