package pirarucu.search

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MovePicker

class SearchPlyInfo(val ply: Int, private val moveGenerator: MoveGenerator) {

    val attackInfo = AttackInfo()

    private val movePicker = MovePicker()

    var killerMove1 = Move.NONE
    var killerMove2 = Move.NONE

    private var ttMove = Move.NONE

    init {
        clear()
    }

    fun clear() {
        ttMove = Move.NONE

        killerMove1 = Move.NONE
        killerMove2 = Move.NONE
    }

    fun setupMovePicker(board: Board): MovePicker {
        movePicker.setup(board, attackInfo, moveGenerator)
        return movePicker
    }

    fun setupMovePicker(board: Board, ttMove: Int): MovePicker {
        movePicker.setup(board, attackInfo, moveGenerator, ttMove, killerMove1, killerMove2)
        return movePicker
    }

    fun setTTMove(move: Int) {
        ttMove = move
    }

    fun isTTMove(move: Int): Boolean {
        return ttMove == move
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