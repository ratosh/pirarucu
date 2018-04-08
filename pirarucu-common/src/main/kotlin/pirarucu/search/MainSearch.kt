package pirarucu.search

import pirarucu.board.Bitboard
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
import pirarucu.stats.Statistics
import pirarucu.uci.UciOutput
import pirarucu.util.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object MainSearch {

    private val ttMoves = Array(GameConstants.MAX_PLIES) { IntArray(TranspositionTable.MAX_MOVES) }

    private var searchTimeLimit: Long = 0L
    private var panicTimeLimit: Long = 0L

    private const val PHASE_END = 0
    private const val PHASE_QUIET = 1
    private const val PHASE_ATTACK = 2
    private const val PHASE_TT = 3

    private val RAZOR_MARGIN = intArrayOf(0, 100, 150, 200, 250, 300, 350, 400)
    private val FUTILITY_CHILD_MARGIN = intArrayOf(0, 120, 220, 320, 420, 520, 620, 720)

    private fun search(board: Board,
                       moveList: MoveList,
                       depth: Int,
                       ply: Int,
                       alpha: Int,
                       beta: Int,
                       skipNullMove: Boolean = false): Int {
        if (SearchOptions.stop) {
            return 0
        }

        if (depth <= 0) {
            val currentTime = Utils.specific.currentTimeMillis()
            if (PrincipalVariation.bestMove != Move.NONE &&
                ((SearchOptions.panic && panicTimeLimit < currentTime) ||
                    (!SearchOptions.panic && searchTimeLimit < currentTime))) {
                SearchOptions.stop = true
                return 0
            }
            return QuiescenceSearch.search(board, moveList, ply, alpha, beta)
        }

        if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
            return EvalConstants.SCORE_DRAW
        }
        if (Statistics.ENABLED) {
            Statistics.abSearch++
        }

        var currentAlpha = max(alpha, EvalConstants.SCORE_MIN + ply)
        val currentBeta = min(beta, EvalConstants.SCORE_MAX - ply)
        if (currentAlpha >= currentBeta) {
            return currentAlpha
        }

        val isPv = currentBeta - currentAlpha != 1
        if (isPv && Statistics.ENABLED) {
            Statistics.pvSearch++
        }
        val inCheck = board.basicEvalInfo.checkBitboard[board.colorToMove] != Bitboard.EMPTY

        val ttEntry: Boolean
        val eval: Int

        var foundMoves = 0L

        if (TranspositionTable.findEntry(board)) {
            foundMoves = TranspositionTable.foundMoves
            val foundInfo = TranspositionTable.foundInfo
            val foundScore = TranspositionTable.foundScore
            ttEntry = foundMoves != 0L
            eval = TranspositionTable.getScore(foundScore, ply)
            if (ttEntry && TranspositionTable.getDepth(foundInfo) >= depth) {
                when (TranspositionTable.getScoreType(foundInfo)) {
                    HashConstants.SCORE_TYPE_EXACT_SCORE -> return eval
                    HashConstants.SCORE_TYPE_FAIL_LOW -> if (eval >= currentBeta) {
                        return eval
                    }
                    HashConstants.SCORE_TYPE_FAIL_HIGH -> if (eval <= currentAlpha) {
                        return eval
                    }
                }
            }
        } else {
            ttEntry = false
            eval = if (!isPv && !inCheck) {
                GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board)
            } else {
                EvalConstants.SCORE_MIN
            }
        }

        // Prunes
        if (!isPv && !inCheck) {

            // Futility
            if (depth < FUTILITY_CHILD_MARGIN.size && eval < EvalConstants.SCORE_KNOW_WIN) {
                if (Statistics.ENABLED) {
                    Statistics.futility++
                }
                val futilityBase = eval - FUTILITY_CHILD_MARGIN[depth]
                if (futilityBase >= currentBeta) {
                    if (Statistics.ENABLED) {
                        Statistics.futilityHit++
                    }
                    return eval
                }
            }

            // Razoring
            if (depth < RAZOR_MARGIN.size) {
                val razorAlpha = currentAlpha - RAZOR_MARGIN[depth]
                if (eval < razorAlpha) {
                    if (Statistics.ENABLED) {
                        Statistics.razoring++
                    }
                    val razorSearchValue = search(board, moveList, 0, ply + 1, razorAlpha,
                        razorAlpha + 1)
                    if (razorSearchValue <= razorAlpha) {
                        if (Statistics.ENABLED) {
                            Statistics.razoringHit++
                        }
                        return razorSearchValue
                    }
                }
            }

            // Null move pruning and mate threat detection
            if (!skipNullMove) {
                if (Statistics.ENABLED) {
                    Statistics.nullMove++
                }
                board.doNullMove()
                val reduction = 3 + depth / 3
                val score = -search(board, moveList, depth - reduction, ply + 1, -currentBeta, -currentBeta + 1, true)
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

        moveList.startPly()
        var movesPerformed = 0
        var phase = if (ttEntry) {
            PHASE_TT
        } else {
            PHASE_ATTACK
        }
        while (phase > PHASE_END) {
            when (phase) {
                PHASE_TT -> {
                    for (index in 0 until TranspositionTable.MAX_MOVES) {
                        val ttMove = TranspositionTable.getMove(foundMoves, index)
                        if (ttMove != Move.NONE) {
                            moveList.addMove(ttMove)
                        }
                        ttMoves[ply][index] = ttMove
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

                movesPerformed++

                val searchDepth = depth - 1

                board.doMove(move)
                val score = -search(board, moveList, searchDepth, ply + 1, -currentBeta, -currentAlpha)
                board.undoMove(move)

                currentAlpha = max(currentAlpha, score)

                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }

                if (currentAlpha >= currentBeta) {
                    phase = PHASE_END
                    break
                }
            }
            phase--
        }
        moveList.endPly()

        if (movesPerformed == 0) {
            bestMove = Move.NONE
            bestScore = if (board.basicEvalInfo.checkBitboard[board.colorToMove] == Bitboard.EMPTY) {
                // STALEMATE
                EvalConstants.SCORE_DRAW
            } else {
                // MATED
                EvalConstants.SCORE_MIN + ply
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

        var depth = 1
        var alpha = EvalConstants.SCORE_MIN
        var beta = EvalConstants.SCORE_MAX
        var score = EvalConstants.SCORE_MIN
        val startTime = Utils.specific.currentTimeMillis()
        searchTimeLimit = startTime + SearchOptions.searchTimeLimit
        panicTimeLimit = searchTimeLimit + SearchOptions.extraPanicTimeLimit

        while (PrincipalVariation.bestMove == Move.NONE || depth < SearchOptions.depth) {
            if (SearchOptions.stop) {
                break
            }
            var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE

            val previousScore = score
            while (true) {
                score = search(board, moveList, depth, 1, alpha, beta)
                if (SearchOptions.stop) {
                    break
                }
                PrincipalVariation.save(board)

                val currentTime = Utils.specific.currentTimeMillis()
                UciOutput.searchInfo(depth, currentTime - startTime)

                SearchOptions.panic = score < previousScore - SearchConstants.PANIC_WINDOW &&
                    SearchOptions.panicEnabled &&
                    abs(score) < EvalConstants.SCORE_MATE

                if ((SearchOptions.panic && panicTimeLimit < currentTime) ||
                    (!SearchOptions.panic && searchTimeLimit < currentTime)) {
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

        UciOutput.bestMove(PrincipalVariation.bestMove)
    }
}