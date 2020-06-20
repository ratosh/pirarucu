package pirarucu.util.epd

import pirarucu.board.Board
import pirarucu.move.Move

data class EpdInfo(val fenPosition: String,
                   val bestMoveList: MutableSet<String>?,
                   val avoidMoveList: Set<String>?,
                   val moveScoreList: MutableMap<String, Int>?,
                   var result: Double,
                   val comment: String?) {
    var eval = 0
    var error = 0.0
    var time = 0L
    var nodes = 0L
    var moveScore = 0
    var valid = true
    var entryAmount = 1

    fun getMoveScore(board: Board, foundMove: Int): Int {
        if (null != moveScoreList) {
            for ((key, value) in moveScoreList) {
                if (Move.areMovesCompatibles(board, foundMove, key)) {
                    return 90 + value
                }
            }
        }

        if (null != bestMoveList) {
            for (move in bestMoveList) {
                if (Move.areMovesCompatibles(board, foundMove, move)) {
                    return 100
                }
            }
        }

        if (null != avoidMoveList) {
            for (move in avoidMoveList) {
                if (Move.areMovesCompatibles(board, foundMove, move)) {
                    return 100
                }
            }
        }

        return 0
    }

    fun toPgnResult() : String {
        return when {
            (eval > 200) -> "1-0";
            (eval < -200) -> "0-1";
            else -> "1/2"
        }
    }
}
