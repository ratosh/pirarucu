package pirarucu.search

import pirarucu.board.Board
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import kotlin.math.max

/**
 * https://chessprogramming.wikispaces.com/Quiescence+Search
 */
object QuiescenceSearch {

    fun search(board: Board,
               moveList: MoveList,
               ply: Int,
               alpha: Int,
               beta: Int): Int {
        if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
            return EvalConstants.SCORE_DRAW
        }
        val eval: Int
        if (TranspositionTable.findEntry(board)) {
            val foundInfo = TranspositionTable.foundInfo
            eval = TranspositionTable.getScore(TranspositionTable.foundInfo, ply)
            when (TranspositionTable.getScoreType(foundInfo)) {
                HashConstants.SCORE_TYPE_EXACT_SCORE -> return eval
                HashConstants.SCORE_TYPE_FAIL_LOW -> if (eval >= beta) {
                    return eval
                }
                HashConstants.SCORE_TYPE_FAIL_HIGH -> if (eval <= alpha) {
                    return eval
                }
            }
        } else {
            eval = GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board)
        }

        if (eval >= beta) {
            return eval
        }

        var bestScore = max(alpha, eval)

        /**
         * Qsearch Futility
         */
        val futilityValue = eval + EvalConstants.QS_PIECE_VALUE[board.capturedPiece]
        if (futilityValue <= alpha) {
            return max(bestScore, futilityValue)
        }

        if (!moveList.startPly()) {
            return bestScore
        }
        MoveGenerator.legalAttacks(board, moveList)

        var moveCount = 0
        var bestMove = Move.NONE

        while (moveList.hasNext()) {
            val move = moveList.next()
            moveCount++

            board.doMove(move)
            val innerScore = -search(board, moveList, ply + 1, -beta, -bestScore)
            board.undoMove(move)

            if (innerScore > bestScore) {
                bestScore = innerScore
                bestMove = move
            }
            if (innerScore >= beta) {
                break
            }

        }

        moveList.endPly()
        val scoreType = when {
            bestScore <= alpha -> HashConstants.SCORE_TYPE_FAIL_LOW
            bestScore >= beta -> HashConstants.SCORE_TYPE_FAIL_HIGH
            else -> HashConstants.SCORE_TYPE_EXACT_SCORE
        }
        TranspositionTable.save(board, bestScore, scoreType, 0, ply, bestMove)

        return bestScore
    }
}