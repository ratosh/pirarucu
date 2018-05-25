package pirarucu.search

import pirarucu.eval.AttackInfo
import pirarucu.move.Move

class SearchPlyInfo(val ply: Int) {

    var attackInfo = AttackInfo()

    var killerMove1 = Move.NONE
    var killerMove2 = Move.NONE

    var ttMove = Move.NONE

    init {
        clear()
    }

    fun clear() {
        ttMove = Move.NONE

        killerMove1 = Move.NONE
        killerMove2 = Move.NONE
    }

    fun addKillerMove(move: Int) {
        if (killerMove1 != move) {
            killerMove2 = killerMove1
            killerMove1 = move
        }
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
}