package pirarucu.eval

import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue

object Evaluator {

    fun evaluate(board: Board): Int {
        var score = TunableConstants.TEMPO[board.colorToMove] +
            board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        return (mgScore * phase + egScore * (TunableConstants.PHASE_MAX - phase)) /
            TunableConstants.PHASE_MAX
    }
}
