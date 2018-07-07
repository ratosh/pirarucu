package pirarucu.search

import pirarucu.board.Board
import pirarucu.board.Piece
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.eval.StaticExchangeEvaluator
import pirarucu.game.GameConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import pirarucu.move.MoveType
import pirarucu.stats.Statistics
import pirarucu.tuning.TunableConstants
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
        Statistics.searchNodes++

        var eval = EvalConstants.SCORE_UNKNOWN

        val foundInfo = TranspositionTable.findEntry(board)
        if (foundInfo != TranspositionTable.EMPTY_INFO) {
            eval = TranspositionTable.getEval(foundInfo)
        }

        val currentNode = SearchInfo.plyInfoList[ply]

        if (eval == EvalConstants.SCORE_UNKNOWN) {
            eval = GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board, currentNode.attackInfo)
        }

        if (eval >= beta) {
            return eval
        }

        var bestScore = max(alpha, eval)


        val futilityQueenValue = eval + TunableConstants.QS_FUTILITY_VALUE[Piece.QUEEN]
        if (futilityQueenValue <= bestScore) {
            return futilityQueenValue
        }

        if (!moveList.startPly()) {
            return bestScore
        }
        MoveGenerator.legalAttacks(board, currentNode.attackInfo, moveList)

        var moveCount = 0

        while (moveList.hasNext()) {
            val move = moveList.next()
            if (!board.isLegalMove(move)) {
                continue
            }
            moveCount++

            val moveType = Move.getMoveType(move)

            if (MoveType.isPromotion(moveType) && moveType != MoveType.TYPE_PROMOTION_QUEEN) {
                continue
            }

            // Qsearch Futility
            val capturedPiece = board.pieceTypeBoard[Move.getToSquare(move)]
            val futilityValue = eval + TunableConstants.QS_FUTILITY_VALUE[capturedPiece]
            if (futilityValue <= bestScore) {
                continue
            }

            if (StaticExchangeEvaluator.getSeeCaptureScore(board, move) <= 0) {
                continue
            }

            board.doMove(move)
            val innerScore = if (!DrawEvaluator.hasSufficientMaterial(board)) {
                EvalConstants.SCORE_DRAW
            } else {
                -search(board, moveList, ply + 1, -beta, -bestScore)
            }
            board.undoMove(move)

            if (innerScore > bestScore) {
                bestScore = innerScore
            }
            if (innerScore >= beta) {
                TranspositionTable.save(board, eval, bestScore, HashConstants.SCORE_TYPE_BOUND_LOWER, 0, ply, move)
                break
            }
        }

        moveList.endPly()
        return bestScore
    }
}