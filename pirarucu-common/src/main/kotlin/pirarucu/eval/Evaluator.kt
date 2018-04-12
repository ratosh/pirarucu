package pirarucu.eval

import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue

object Evaluator {
    fun evaluate(board: Board): Int {
        var score = TunableConstants.TEMPO + board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        val independentScore = materialImbalance(board, Color.WHITE, Color.BLACK) -
            materialImbalance(board, Color.BLACK, Color.WHITE)

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        return (mgScore * (TunableConstants.PHASE_MAX - phase) + egScore * phase) /
            TunableConstants.PHASE_MAX + independentScore
    }

    private fun materialImbalance(board: Board, ourColor: Int, theirColor: Int): Int {
        var result = 0

        for (piece in Piece.PAWN until Piece.KING) {
            if (board.pieceCountColorType[ourColor][piece] > 1) {
                for (otherPiece in Piece.NONE..piece) {
                    result += board.pieceCountColorType[ourColor][piece] *
                        (TunableConstants.MATERIAL_IMBALANCE_OURS[piece][otherPiece] *
                            board.pieceCountColorType[ourColor][otherPiece] +
                            TunableConstants.MATERIAL_IMBALANCE_THEIRS[piece][otherPiece] *
                            board.pieceCountColorType[theirColor][otherPiece])
                }
            }
        }

        return result
    }
}
