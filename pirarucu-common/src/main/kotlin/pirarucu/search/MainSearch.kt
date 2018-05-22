package pirarucu.search

import pirarucu.board.Bitboard
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
import pirarucu.uci.UciOutput
import pirarucu.util.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object MainSearch {

    private const val PHASE_END = 0
    private const val PHASE_QUIET = 1
    private const val PHASE_KILLER_2 = 2
    private const val PHASE_KILLER_1 = 3
    private const val PHASE_ATTACK = 4
    private const val PHASE_TT = 5

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
        val rootNode = pvNode && ply == 0

        if (!rootNode &&
            (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board))) {
            return EvalConstants.SCORE_DRAW
        }

        if (Statistics.ENABLED) {
            Statistics.abSearch++
        }

        val currentAlpha = max(alpha, EvalConstants.SCORE_MIN + ply)
        val currentBeta = min(beta, EvalConstants.SCORE_MAX - (ply + 1))
        if (currentAlpha >= currentBeta) {
            return currentAlpha
        }
        if (Statistics.ENABLED && pvNode) {
            Statistics.pvSearch++
        }
        val inCheck = board.basicEvalInfo.checkBitboard != Bitboard.EMPTY

        var eval: Int

        var foundMoves = 0L

        val prunable = !inCheck

        if (SearchConstants.ENABLE_TT && TranspositionTable.findEntry(board)) {
            if (Statistics.ENABLED) {
                Statistics.TTEntry++
            }
            foundMoves = TranspositionTable.foundMoves
            val foundInfo = TranspositionTable.foundInfo
            val foundScore = TranspositionTable.foundScore
            eval = TranspositionTable.getScore(foundScore, ply)
            if (foundMoves != 0L && TranspositionTable.getDepth(foundInfo) >= depth) {
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
                    Statistics.childFutility[depth]++
                }
                if (eval - TunableConstants.FUTILITY_CHILD_MARGIN[depth] >= currentBeta) {
                    if (Statistics.ENABLED) {
                        Statistics.childFutilityHit[depth]++
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
                    val razorSearchValue = search(board, moveList, 0, ply, razorAlpha, razorAlpha + 1, false)
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

        val currentNode = SearchInfo.plyInfoList[ply]

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
                    if (foundMoves == 0L && depth >= 5) {
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
                            currentNode.addTTMove(index, ttMove)
                        }
                    }
                }
                PHASE_ATTACK -> {
                    MoveGenerator.legalAttacks(board, currentNode.attackInfo, moveList)
                }
                PHASE_KILLER_1 -> {
                    val killerMove = currentNode.killerMove1
                    if (Statistics.ENABLED) {
                        Statistics.killer1++
                    }
                    if (killerMove != Move.NONE &&
                        !currentNode.isTTMove(killerMove) &&
                        MoveGenerator.isLegalQuietMove(board, currentNode.attackInfo, killerMove)) {
                        moveList.addMove(killerMove)
                        if (Statistics.ENABLED) {
                            Statistics.killer1Hit++
                        }
                    }
                }
                PHASE_KILLER_2 -> {
                    val killerMove = currentNode.killerMove2
                    if (Statistics.ENABLED) {
                        Statistics.killer2++
                    }
                    if (killerMove != Move.NONE &&
                        !currentNode.isTTMove(killerMove) &&
                        MoveGenerator.isLegalQuietMove(board, currentNode.attackInfo, killerMove)) {
                        moveList.addMove(killerMove)
                        if (Statistics.ENABLED) {
                            Statistics.killer2Hit++
                        }
                    }
                }
                PHASE_QUIET -> {
                    MoveGenerator.legalMoves(board, currentNode.attackInfo, moveList)
                }
            }
            while (moveList.hasNext()) {
                val move = moveList.next()
                if (foundMoves != 0L && phase != PHASE_TT && currentNode.isTTMove(move)) {
                    continue
                }
                if (phase == PHASE_QUIET && currentNode.isKillerMove(move)) {
                    continue
                }
                val moveType = Move.getMoveType(move)
                val isPromotion = MoveType.isPromotion(moveType)
                val toSquare = Move.getToSquare(move)
                val capturedPiece = board.pieceTypeBoard[toSquare]

                val isCapture = capturedPiece != Piece.NONE

                if (prunable &&
                    !rootNode &&
                    !isPromotion &&
                    movesPerformed > 0) {

                    if (SearchConstants.ENABLE_SEARCH_PARENT_FUTILITY &&
                        !isCapture &&
                        depth < TunableConstants.FUTILITY_PARENT_MARGIN.size) {
                        if (Statistics.ENABLED) {
                            Statistics.parentFutility++
                        }
                        val futilityValue = eval + TunableConstants.FUTILITY_PARENT_MARGIN[depth]
                        if (futilityValue <= alpha) {
                            if (Statistics.ENABLED) {
                                Statistics.parentFutilityHit++
                            }
                            if (futilityValue > bestScore) {
                                bestScore = futilityValue
                                bestMove = move
                            }
                            continue
                        }
                    }

                    if (SearchConstants.ENABLE_NEGATIVE_SEE_PRUNING &&
                        depth < SearchConstants.NEGATIVE_SEE_DEPTH) {
                        if (Statistics.ENABLED) {
                            Statistics.negativeSee++
                        }
                        if (StaticExchangeEvaluator.getSeeCaptureScore(board, move) < 0) {
                            if (Statistics.ENABLED) {
                                Statistics.negativeSeeHit++
                            }
                            continue
                        }
                    }
                }

                movesPerformed++

                board.doMove(move)

                var reduction = when {
                    inCheck -> 0
                    else -> 1
                }

                val searchDepth = depth - reduction

                // Reductions
                if (depth >= 3 &&
                    movesPerformed > 1 &&
                    !isCapture &&
                    !isPromotion) {
                    reduction += 1 + depth / 6
                }

                val reducedDepth = depth - reduction

                var score = EvalConstants.SCORE_MAX

                if (reducedDepth < searchDepth) {
                    score = -search(board, moveList, reducedDepth, ply + 1, -searchAlpha - 1, -searchAlpha, false)
                    if (Statistics.ENABLED) {
                        Statistics.lmr++
                        if (score <= searchAlpha) {
                            Statistics.lmrHit++
                        }
                    }
                }

                if (reducedDepth >= searchDepth || score > searchAlpha) {
                    score = -search(board, moveList, searchDepth, ply + 1, -searchAlpha - 1, -searchAlpha, false)
                    if (Statistics.ENABLED) {
                        Statistics.pvs++
                        if (score <= searchAlpha) {
                            Statistics.pvsHit++
                        }
                    }
                }

                if (score > searchAlpha) {
                    score = -search(board, moveList, searchDepth, ply + 1, -currentBeta, -searchAlpha, true)
                }
                board.undoMove(move)

                searchAlpha = max(searchAlpha, score)

                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }

                if (searchAlpha >= currentBeta) {
                    val isNormal = Move.getMoveType(bestMove) == MoveType.TYPE_NORMAL
                    if (!isCapture && isNormal) {
                        currentNode.addKillerMove(bestMove)
                    }
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
                currentAlpha
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
        var searchTimeLimit = startTime + SearchOptions.minSearchTimeLimit
        var minSearchTimeLimit = startTime + SearchOptions.minSearchTimeLimit
        val maxSearchTimeLimit = startTime + SearchOptions.maxSearchTimeLimit

        val searchTimeIncrement = SearchOptions.searchTimeIncrement

        var currentTime = startTime
        while (depth <= SearchOptions.depth) {
            if (SearchOptions.stop && PrincipalVariation.bestMove != Move.NONE) {
                break
            }
            var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE

            while (true) {
                val previousScore = score
                val previousTime = currentTime

                score = search(board, moveList, depth, 0, alpha, beta, true)

                PrincipalVariation.save(board)

                currentTime = Utils.specific.currentTimeMillis()
                UciOutput.searchInfo(depth, currentTime - startTime)

                if (score < previousScore &&
                    searchTimeLimit < maxSearchTimeLimit &&
                    abs(score) < EvalConstants.SCORE_MATE) {
                    searchTimeLimit += searchTimeIncrement
                }
                if (score > previousScore &&
                    searchTimeLimit > minSearchTimeLimit &&
                    abs(score) < EvalConstants.SCORE_MATE) {
                    searchTimeLimit -= searchTimeIncrement
                }
                val timeDiff = currentTime - previousTime
                val nextIterationTime = currentTime + (timeDiff shl 2)

                if (searchTimeLimit < currentTime || maxSearchTimeLimit < nextIterationTime) {
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