package pirarucu.eval

import pirarucu.board.Board
import pirarucu.util.SplitValue

object Evaluator {
    fun evaluate(board: Board): Int {
        var score = board.psqScore + board.pieceScore

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase
        return (mgScore * (EvalConstants.PHASE_MAX - phase) + egScore * phase) / EvalConstants.PHASE_MAX
    }
}
