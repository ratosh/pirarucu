package pirarucu.search

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MovePicker

class SearchPlyInfo(private val moveGenerator: MoveGenerator) {

    val attackInfo = AttackInfo()

    private val movePicker = MovePicker()

    var killerMove1 = Move.NONE
    var killerMove2 = Move.NONE

    var currentMove = Move.NONE
        private set

    val phase: Int
        get() {
            return movePicker.phase
        }

    init {
        clear()
    }

    fun clear() {
        killerMove1 = Move.NONE
        killerMove2 = Move.NONE
    }

    fun next(skipQuiets: Boolean): Int {
        currentMove = movePicker.next(skipQuiets)
        return currentMove
    }

    fun setupMovePicker(board: Board, threshold: Int) {
        movePicker.setup(board, attackInfo, moveGenerator, threshold)
    }

    fun setupMovePicker(board: Board, threshold: Int, ttMove: Int) {
        movePicker.setup(board, attackInfo, moveGenerator, threshold, ttMove, killerMove1, killerMove2)
    }

    fun isKillerMove(move: Int): Boolean {
        return move == killerMove1 || move == killerMove2
    }

    fun addKillerMove(move: Int) {
        if (killerMove1 != move) {
            killerMove2 = killerMove1
            killerMove1 = move
        }
    }
}