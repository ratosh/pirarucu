package pirarucu.eval

import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.util.SplitValue

object Evaluator {
    fun evaluate(board: Board): Int {
        var score = EvalConstants.TEMPO + board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        val independentScore = materialImbalance(board, Color.WHITE, Color.BLACK) -
            materialImbalance(board, Color.BLACK, Color.WHITE)

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        return (mgScore * (EvalConstants.PHASE_MAX - phase) + egScore * phase) /
            EvalConstants.PHASE_MAX + independentScore
    }

    private fun materialImbalance(board: Board, ourColor: Int, theirColor: Int): Int {
        var result = 0

        for (piece in Piece.PAWN until Piece.KING) {
            if (board.pieceCountColorType[ourColor][piece] > 1) {
                for (otherPiece in Piece.NONE..piece) {
                    result += board.pieceCountColorType[ourColor][piece] *
                        (EvalConstants.MATERIAL_IMBALANCE_OURS[piece][otherPiece] *
                            board.pieceCountColorType[ourColor][otherPiece] +
                            EvalConstants.MATERIAL_IMBALANCE_THEIRS[piece][otherPiece] *
                            board.pieceCountColorType[theirColor][otherPiece])
                }
            }
        }

        return result
    }
}
