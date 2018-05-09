package pirarucu.search

import pirarucu.eval.AttackInfo
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.util.Utils

class SearchPlyInfo(val ply: Int) {

    var attackInfo = AttackInfo()

    var killerMove1 = Move.NONE
    var killerMove2 = Move.NONE

    var ttMoves = IntArray(TranspositionTable.MAX_MOVES)

    init {
        clear()
    }

    fun clear() {
        Utils.specific.arrayFill(ttMoves, Move.NONE)

        killerMove1 = Move.NONE
        killerMove2 = Move.NONE
    }

    fun addKillerMove(move: Int) {
        if (killerMove1 != move) {
            killerMove2 = killerMove1
            killerMove1 = move
        }
    }

    fun addTTMove(index: Int, move: Int) {
        ttMoves[index] = move
    }

    fun isTTMove(move: Int): Boolean {
        return ttMoves.contains(move)
    }

    fun isKillerMove(move: Int): Boolean {
        return move == killerMove1 || move == killerMove2
    }
}