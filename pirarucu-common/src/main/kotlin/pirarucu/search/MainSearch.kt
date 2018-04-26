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
import pirarucu.stats.Statistics
import pirarucu.tuning.TunableConstants
import pirarucu.uci.UciOutput
import pirarucu.util.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object MainSearch {

    private val ttMoves = Array(GameConstants.MAX_PLIES) { IntArray(TranspositionTable.MAX_MOVES) }

    private var minSearchTimeLimit: Long = 0L
    private var panicSearchTimeLimit: Long = 0L
    private var maxSearchTimeLimit: Long = 0L

    private const val PHASE_END = 0
    private const val PHASE_QUIET = 1
    private const val PHASE_ATTACK = 2
    private const val PHASE_TT = 3

    private fun search(board: Board,
                       moveList: MoveList,
                       depth: Int,
                       ply: Int,
                       alpha: Int,
                       beta: Int,
                       pvNode: Boolean,
                       skipNullMove: Boolean = false): Int {
        if (SearchOptions.stop) {
            return 0
        }

        if (depth <= 0) {
            return QuiescenceSearch.search(board, moveList, ply, alpha, beta)
        }

        if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
            return EvalConstants.SCORE_DRAW
        }
        if (Statistics.ENABLED) {
            Statistics.abSearch++
        }

        val currentAlpha = max(alpha, EvalConstants.SCORE_MIN + ply)
        val currentBeta = min(beta, EvalConstants.SCORE_MAX - ply + 1)
        if (currentAlpha >= currentBeta) {
            return currentAlpha
        }
        if (Statistics.ENABLED && pvNode) {
            Statistics.pvSearch++
        }
        val inCheck = board.basicEvalInfo.checkBitboard[board.colorToMove] != Bitboard.EMPTY

        val ttEntry: Boolean
        val eval: Int

        var foundMoves = 0L
        val rootNode = pvNode && ply == 1

        val prunable = !inCheck

        if (SearchConstants.ENABLE_TT && TranspositionTable.findEntry(board)) {
            if (Statistics.ENABLED) {
                Statistics.TTEntry++
            }
            foundMoves = TranspositionTable.foundMoves
            val foundInfo = TranspositionTable.foundInfo
            val foundScore = TranspositionTable.foundScore
            ttEntry = foundMoves != 0L
            eval = TranspositionTable.getScore(foundScore, ply)
            if (ttEntry && TranspositionTable.getDepth(foundInfo) >= depth) {
                when (TranspositionTable.getScoreType(foundInfo)) {
                    HashConstants.SCORE_TYPE_EXACT_SCORE -> {
                        return eval
                    }
                    HashConstants.SCORE_TYPE_FAIL_LOW -> if (eval <= currentAlpha) {
                        return eval
                    }
                    HashConstants.SCORE_TYPE_FAIL_HIGH -> if (eval >= currentBeta) {
                        return eval
                    }
                }
            }
        } else {
            ttEntry = false
            eval = if (prunable) {
                GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board)
            } else {
                EvalConstants.SCORE_UNKNOWN
            }
        }

        // Prunes
        if (prunable) {
            if (Statistics.ENABLED) {
                Statistics.prunable++
            }

            // Futility
            if (SearchConstants.ENABLE_SEARCH_FUTILITY &&
                !rootNode &&
                depth < TunableConstants.FUTILITY_CHILD_MARGIN.size &&
                eval < EvalConstants.SCORE_KNOW_WIN) {
                if (Statistics.ENABLED) {
                    Statistics.futility[depth]++
                }
                if (eval - TunableConstants.FUTILITY_CHILD_MARGIN[depth] >= currentBeta) {
                    if (Statistics.ENABLED) {
                        Statistics.futilityHit[depth]++
                    }
                    return eval
                }
            }

            // Razoring
            if (SearchConstants.ENABLE_SEARCH_RAZORING &&
                !pvNode &&
                depth < TunableConstants.RAZOR_MARGIN.size) {
                val razorAlpha = currentAlpha - TunableConstants.RAZOR_MARGIN[depth]
                if (eval < razorAlpha) {
                    if (Statistics.ENABLED) {
                        Statistics.razoring[depth]++
                    }
                    val razorSearchValue = search(board, moveList, 0, ply + 1, razorAlpha, razorAlpha + 1, false)
                    if (razorSearchValue <= razorAlpha) {
                        if (Statistics.ENABLED) {
                            Statistics.razoringHit[depth]++
                        }
                        return razorSearchValue
                    }
                }
            }

            // Null move pruning and mate threat detection
            if (SearchConstants.ENABLE_SEARCH_NULL_MOVE &&
                !skipNullMove &&
                !pvNode &&
                eval >= currentBeta) {
                if (Statistics.ENABLED) {
                    Statistics.nullMove++
                }
                board.doNullMove()
                val reduction = 3 + depth / 3
                val score = -search(board, moveList, depth - reduction, ply + 1, -currentBeta, -currentBeta + 1, false, true)
                board.undoNullMove()
                if (score >= currentBeta && score < EvalConstants.SCORE_MATE) {
                    if (Statistics.ENABLED) {
                        Statistics.nullMoveHit++
                    }
                    return score
                }
            }
        }

        var bestMove = Move.NONE
        var bestScore = EvalConstants.SCORE_MIN

        if (!moveList.startPly()) {
            return eval
        }
        var movesPerformed = 0
        var searchAlpha = currentAlpha
        var phase = if (SearchConstants.ENABLE_TT) {
            PHASE_TT
        } else {
            PHASE_ATTACK
        }
        while (phase > PHASE_END) {
            when (phase) {
                PHASE_TT -> {
                    if (!ttEntry && depth >= 5) {
                        search(board, moveList, 3 * depth / 4 - 2, ply, currentAlpha, currentBeta, false)
                        if (TranspositionTable.findEntry(board)) {
                            foundMoves = TranspositionTable.foundMoves
                        }
                    }
                    if (foundMoves != 0L) {
                        for (index in 0 until TranspositionTable.MAX_MOVES) {
                            val ttMove = TranspositionTable.getMove(foundMoves, index)
                            if (ttMove != Move.NONE) {
                                moveList.addMove(ttMove)
                            }
                            ttMoves[ply][index] = ttMove
                        }
                    }

                }
                PHASE_ATTACK -> {
                    MoveGenerator.legalAttacks(board, moveList)
                }
                PHASE_QUIET -> {
                    MoveGenerator.legalMoves(board, moveList)
                }
            }
            while (moveList.hasNext()) {
                val move = moveList.next()
                if (ttEntry && phase != PHASE_TT && ttMoves[ply].contains(move)) {
                    continue
                }
                val moveType = Move.getMoveType(move)

                movesPerformed++

                board.doMove(move)

                val givesCheck = board.basicEvalInfo.checkBitboard[board.colorToMove] != Bitboard.EMPTY
                val isCapture = board.capturedPiece != Piece.NONE
                val isPromotion = MoveType.isPromotion(moveType)

                var reduction = when {
                    movesPerformed == 1 -> 1
                    isCapture -> 0
                    isPromotion -> 0
                    givesCheck -> 0
                    else -> 1
                }

                // Reductions
                if (depth >= 3 &&
                    movesPerformed > 1 &&
                    !inCheck &&
                    !isCapture &&
                    !isPromotion &&
                    !givesCheck) {
                    reduction += 1 + depth / 6
                }

                val searchDepth = depth - reduction

                var score = -search(board, moveList, searchDepth, ply + 1, -searchAlpha - 1, -searchAlpha, false)
                if (Statistics.ENABLED) {
                    Statistics.pvs++
                    if (score <= searchAlpha) {
                        Statistics.pvsHits++
                    }
                }

                if (score > searchAlpha) {
                    score = -search(board, moveList, searchDepth, ply + 1, -currentBeta, -searchAlpha, true, false)
                }
                board.undoMove(move)

                searchAlpha = max(searchAlpha, score)

                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }

                if (searchAlpha >= currentBeta) {
                    phase = PHASE_END
                    break
                }
            }
            phase--
        }
        moveList.endPly()

        if (movesPerformed == 0) {
            bestMove = Move.NONE
            bestScore = if (inCheck) {
                // MATED
                Statistics.mate++
                EvalConstants.SCORE_MIN + ply
            } else {
                // STALEMATE
                Statistics.stalemate++
                EvalConstants.SCORE_DRAW
            }
        }
        val scoreType = when {
            bestScore <= currentAlpha -> HashConstants.SCORE_TYPE_FAIL_LOW
            bestScore >= currentBeta -> HashConstants.SCORE_TYPE_FAIL_HIGH
            else -> HashConstants.SCORE_TYPE_EXACT_SCORE
        }

        TranspositionTable.save(board, bestScore, scoreType, depth, ply, bestMove)

        return bestScore
    }

    // Interactive deepening with aspiration window
    fun search(board: Board) {
        SearchOptions.reset()
        PrincipalVariation.reset()
        Statistics.reset()

        val moveList = MoveList()

        TranspositionTable.baseDepth = board.moveNumber

        var depth = 1
        var alpha = EvalConstants.SCORE_MIN
        var beta = EvalConstants.SCORE_MAX
        var score = EvalConstants.SCORE_MIN
        val startTime = Utils.specific.currentTimeMillis()
        minSearchTimeLimit = startTime + SearchOptions.minSearchTimeLimit
        panicSearchTimeLimit = startTime + SearchOptions.extraPanicTimeLimit
        maxSearchTimeLimit = startTime + SearchOptions.maxSearchTimeLimit

        while (PrincipalVariation.bestMove == Move.NONE || depth <= SearchOptions.depth) {
            if (SearchOptions.stop) {
                break
            }
            var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE

            val previousScore = score
            while (true) {
                score = search(board, moveList, depth, 1, alpha, beta, true)

                PrincipalVariation.save(board)

                val currentTime = Utils.specific.currentTimeMillis()
                UciOutput.searchInfo(depth, currentTime - startTime)

                SearchOptions.panic = score < previousScore - SearchConstants.PANIC_WINDOW &&
                    SearchOptions.panicEnabled &&
                    abs(score) < EvalConstants.SCORE_MATE

                if ((SearchOptions.panic && panicSearchTimeLimit < currentTime) ||
                    (!SearchOptions.panic && minSearchTimeLimit < currentTime)) {
                    SearchOptions.stop = true
                    break
                }

                if (score <= alpha) {
                    alpha = if (score < -EvalConstants.SCORE_MATE) {
                        EvalConstants.SCORE_MIN
                    } else {
                        max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                    }
                } else if (score >= beta) {
                    beta = if (score > EvalConstants.SCORE_MATE) {
                        EvalConstants.SCORE_MAX
                    } else {
                        min(score + aspirationWindow, EvalConstants.SCORE_MAX)
                    }
                } else {
                    alpha = max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                    beta = min(score + aspirationWindow, EvalConstants.SCORE_MAX)
                    break
                }

                aspirationWindow += aspirationWindow / 4
            }
            depth++
        }
        SearchOptions.stop = true

        UciOutput.bestMove(PrincipalVariation.bestMove)
    }
}