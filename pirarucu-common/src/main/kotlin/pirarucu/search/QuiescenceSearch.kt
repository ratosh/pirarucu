package pirarucu.search

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Piece
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import pirarucu.move.MoveType
import kotlin.math.max

/**
 * https://chessprogramming.wikispaces.com/Quiescence+Search
 */
object QuiescenceSearch {
    fun search(board: Board, moveList: MoveList,
        ply: Int,
        alpha: Int,
        beta: Int,
        materialGain: Int = 0): Int {
        if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
            return EvalConstants.SCORE_DRAW
        }
        val eval = when {
            TranspositionTable.findEntry(board) -> TranspositionTable.eval
            else -> GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board)
        }

        if (eval >= beta) {
            return eval
        }

        var bestScore = max(alpha, eval)

        if (!moveList.startPly()) {
            return bestScore
        }
        MoveGenerator.legalAttacks(board, moveList)

        var moveCount = 0
        var bestMove = Move.NONE

        while (moveList.hasNext()) {
            val move = moveList.next()
            moveCount++

            val toSquare = Move.getToSquare(move)
            val moveType = Move.getMoveType(move)

            val promotedPiece = MoveType.getPromotedPiece(moveType)

            val materialDiff: Int = materialGain + if (promotedPiece != Piece.NONE) {
                EvalConstants.QS_PIECE_VALUE[promotedPiece] +
                    EvalConstants.QS_PIECE_VALUE[board.pieceTypeBoard[toSquare]] -
                    EvalConstants.QS_PIECE_VALUE[Piece.PAWN]
            } else {
                EvalConstants.QS_PIECE_VALUE[board.pieceTypeBoard[toSquare]]
            }

            // Skip search if losing material, it should not produce a better evaluation
            if (materialDiff < 0) {
                continue
            }

            board.doMove(move)
            val innerScore = -search(board, moveList, ply, -beta, -bestScore, -materialDiff)
            board.undoMove(move)

            if (innerScore > bestScore) {
                bestScore = innerScore
                bestMove = move
                if (bestScore >= beta) {
                    break
                }
            }
        }

        // No attack generated
        if (moveCount == 0) {
            MoveGenerator.legalMoves(board, moveList)
            // No move generated
            if (!moveList.hasNext()) {
                bestScore = if (board.basicEvalInfo.checkBitboard[board.colorToMove] != Bitboard.EMPTY) {
                    // Mated
                    -EvalConstants.SCORE_MAX + ply
                } else {
                    // stale mate
                    EvalConstants.SCORE_DRAW
                }
            }
        }

        moveList.endPly()
        val scoreType = when {
            bestScore <= alpha -> HashConstants.SCORE_TYPE_FAIL_LOW
            bestScore >= beta -> HashConstants.SCORE_TYPE_FAIL_HIGH
            else -> HashConstants.SCORE_TYPE_EXACT_SCORE
        }
        TranspositionTable.save(board, eval, bestScore, scoreType, 0, bestMove)

        return bestScore
    }
}