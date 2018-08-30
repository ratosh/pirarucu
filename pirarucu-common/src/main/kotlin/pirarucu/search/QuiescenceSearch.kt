package pirarucu.search

import pirarucu.board.Board
import pirarucu.board.Piece
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.eval.StaticExchangeEvaluator
import pirarucu.game.GameConstants
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
class QuiescenceSearch {

    var searchOptions = SearchOptions()
    var searchInfo = SearchInfo()

    fun search(board: Board,
               moveList: MoveList,
               ply: Int,
               alpha: Int,
               beta: Int): Int {
        Statistics.searchNodes++

        val currentNode = searchInfo.plyInfoList[ply]
        val eval = GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board, currentNode.attackInfo)

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
            val futilityValue = eval + getMoveValue(board, Move.getToSquare(move), moveType)
            if (futilityValue <= bestScore) {
                continue
            }

            if (!StaticExchangeEvaluator.seeInThreshold(board, move, 1)) {
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
                break
            }
        }

        moveList.endPly()
        return bestScore
    }

    private fun getMoveValue(board: Board, toSquare: Int, moveType: Int): Int {
        return when {
            moveType == MoveType.TYPE_PASSANT -> {
                TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN]
            }
            MoveType.isPromotion(moveType) -> {
                TunableConstants.QS_FUTILITY_VALUE[board.pieceTypeBoard[toSquare]] -
                    TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN] +
                    TunableConstants.QS_FUTILITY_VALUE[MoveType.getPromotedPiece(moveType)]
            }
            else -> {
                TunableConstants.QS_FUTILITY_VALUE[board.pieceTypeBoard[toSquare]]
            }
        }
    }

}