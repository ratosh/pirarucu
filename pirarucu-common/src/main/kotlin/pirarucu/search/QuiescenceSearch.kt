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
        if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
            if (Statistics.ENABLED) {
                Statistics.qDraw++
            }
            return EvalConstants.SCORE_DRAW
        }
        Statistics.searchNodes++
        if (Statistics.ENABLED) {
            Statistics.qMaxPly = max(Statistics.qMaxPly, ply)
            Statistics.qNodes++
        }
        var ttScore = EvalConstants.SCORE_UNKNOWN
        if (SearchConstants.ENABLE_Q_TT) {
            val foundInfo = TranspositionTable.findEntry(board)
            if (foundInfo != TranspositionTable.EMPTY_INFO) {
                if (Statistics.ENABLED) {
                    Statistics.qTTEntry++
                }
                ttScore = TranspositionTable.getScore(foundInfo, ply)
                when (TranspositionTable.getScoreType(foundInfo)) {
                    HashConstants.SCORE_TYPE_EXACT_SCORE -> {
                        return ttScore
                    }
                    HashConstants.SCORE_TYPE_FAIL_LOW -> if (ttScore <= alpha) {
                        return ttScore
                    }
                    HashConstants.SCORE_TYPE_FAIL_HIGH -> if (ttScore >= beta) {
                        return ttScore
                    }
                }
            }
        }
        val eval = when {
            ttScore != EvalConstants.SCORE_UNKNOWN -> ttScore
            else -> GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board)
        }

        if (eval >= beta) {
            if (Statistics.ENABLED) {
                Statistics.qStandpat++
            }
            return eval
        }

        var bestScore = max(alpha, eval)

        if (SearchConstants.ENABLE_QSEARCH_FUTILITY) {
            val futilityValue = eval + TunableConstants.QS_FUTILITY_VALUE[Piece.QUEEN]
            if (futilityValue <= bestScore) {
                if (Statistics.ENABLED) {
                    Statistics.qFutilityHit1++
                }
                return futilityValue
            }
        }

        if (!moveList.startPly()) {
            return bestScore
        }
        val currentNode = SearchInfo.plyInfoList[ply]
        MoveGenerator.legalAttacks(board, currentNode.attackInfo, moveList)

        var moveCount = 0
        var bestMove = Move.NONE

        while (moveList.hasNext()) {
            val move = moveList.next()
            moveCount++

            if (Statistics.ENABLED) {
                Statistics.qRenodes++
            }

            val moveType = Move.getMoveType(move)

            if (MoveType.isPromotion(moveType) && moveType != MoveType.TYPE_PROMOTION_QUEEN) {
                continue
            }

            // Qsearch Futility
            if (SearchConstants.ENABLE_QSEARCH_FUTILITY) {
                val capturedPiece = board.pieceTypeBoard[Move.getToSquare(move)]
                val futilityValue = eval + TunableConstants.QS_FUTILITY_VALUE[capturedPiece]
                if (futilityValue <= bestScore) {
                    if (Statistics.ENABLED) {
                        Statistics.qFutilityHit2++
                    }
                    if (bestScore < futilityValue) {
                        bestScore = futilityValue
                        bestMove = move
                    }
                    continue
                }
            }

            if (SearchConstants.ENABLE_QSEARCH_SEE) {
                if (StaticExchangeEvaluator.getSeeCaptureScore(board, move) <= 0) {
                    if (Statistics.ENABLED) {
                        Statistics.seeHits++
                    }
                    continue
                }
            }

            board.doMove(move)
            val innerScore = -search(board, moveList, ply + 1, -beta, -bestScore)
            board.undoMove(move)

            if (innerScore > bestScore) {
                bestScore = innerScore
                bestMove = move
            }
            if (innerScore >= beta) {
                if (Statistics.ENABLED) {
                    Statistics.qFailHigh++
                }
                break
            }
        }

        moveList.endPly()
        if (bestMove != Move.NONE) {
            val scoreType = when {
                bestScore <= alpha -> HashConstants.SCORE_TYPE_FAIL_LOW
                bestScore >= beta -> HashConstants.SCORE_TYPE_FAIL_HIGH
                else -> HashConstants.SCORE_TYPE_EXACT_SCORE
            }
            TranspositionTable.save(board, bestScore, scoreType, 0, ply, bestMove)
        }

        return bestScore
    }
}