package pirarucu.eval

import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue

object Evaluator {

    fun evaluate(board: Board): Int {
        var score = TunableConstants.TEMPO +
            board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        val result = (mgScore * phase + egScore * (TunableConstants.PHASE_MAX - phase)) /
            TunableConstants.PHASE_MAX

        if (EvalDebug.ENABLED) {
            EvalDebug.psqScore[Color.WHITE] = board.psqScore[Color.WHITE]
            EvalDebug.psqScore[Color.BLACK] = board.psqScore[Color.BLACK]
            EvalDebug.material[Color.WHITE] = board.materialScore[Color.WHITE]
            EvalDebug.material[Color.BLACK] = board.materialScore[Color.BLACK]
        }

        return result
    }
}
