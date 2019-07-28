package pirarucu.util.epd

import pirarucu.board.Board
import pirarucu.move.Move

data class EpdInfo(val fenPosition: String,
                   val bestMoveList: Set<String>?,
                   val avoidMoveList: Set<String>?,
                   val moveScoreList: Map<String, Int>?,
                   val result: Double,
                   val comment: String?) {
    var eval = 0
    var error = 0.0
    var time = 0L
    var nodes = 0L
    var valid = true

    fun getMoveScore(board: Board, foundMove: Int): Int {
        if (null != bestMoveList) {
            for (move in bestMoveList) {
                if (Move.areMovesCompatibles(board, foundMove, move)) {
                    return 10
                }
            }
        }
        if (null != avoidMoveList) {
            for (move in avoidMoveList) {
                if (Move.areMovesCompatibles(board, foundMove, move)) {
                    return 10
                }
            }
        }

        if (null != moveScoreList) {
            for ((key, value) in moveScoreList) {
                if (Move.areMovesCompatibles(board, foundMove, key)) {
                    return value
                }
            }
        }

        return 0
    }
}
