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
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils
import kotlin.math.max
import kotlin.math.min

class MainSearch(private val searchOptions: SearchOptions, private val searchInfoListener: SearchInfoListener) {

    val searchInfo = SearchInfo()

    private val quiescenceSearch = QuiescenceSearch(searchInfo)

    private fun search(board: Board,
                       moveList: MoveList,
                       depth: Int,
                       ply: Int,
                       alpha: Int,
                       beta: Int,
                       skipNullMove: Boolean = false): Int {
        if (searchOptions.stop) {
            return 0
        }
        val inCheck = board.basicEvalInfo.checkBitboard != Bitboard.EMPTY

        // Search extension
        val extension = when {
            inCheck -> 1
            else -> 0
        }

        val newDepth = depth + extension

        if (newDepth <= 0) {
            return quiescenceSearch.search(board, moveList, ply, alpha, beta)
        }
        val rootNode = ply == 0

        searchInfo.searchNodes++
        if (!rootNode &&
            searchInfo.searchNodes and 0xFFFL == 0xFFFL &&
            searchOptions.maxSearchTimeLimit < Utils.specific.currentTimeMillis()) {
            searchOptions.stop = true
            return 0
        }

        val currentAlpha = max(alpha, EvalConstants.SCORE_MIN + ply)
        val currentBeta = min(beta, EvalConstants.SCORE_MAX - (ply + 1))
        if (currentAlpha >= currentBeta) {
            return currentAlpha
        }
        val pvNode = (alpha != beta - 1)

        val prunable = !inCheck && !pvNode

        var eval = EvalConstants.SCORE_UNKNOWN

        var foundInfo = TranspositionTable.EMPTY_INFO
        if (SearchConstants.USE_TT) {
            foundInfo = TranspositionTable.findEntry(board)
            if (foundInfo != TranspositionTable.EMPTY_INFO) {
                eval = TranspositionTable.getEval(foundInfo)
                if (!pvNode && TranspositionTable.getDepth(foundInfo) >= newDepth) {
                    val ttScore = TranspositionTable.getScore(foundInfo, ply)
                    val ttScoreType = TranspositionTable.getScoreType(foundInfo)
                    when (ttScoreType) {
                        HashConstants.SCORE_TYPE_EXACT_SCORE -> {
                            return ttScore
                        }
                        HashConstants.SCORE_TYPE_BOUND_LOWER -> {
                            if (ttScore >= currentBeta) {
                                return ttScore
                            }
                        }
                        HashConstants.SCORE_TYPE_BOUND_UPPER -> {
                            if (ttScore <= currentAlpha) {
                                return ttScore
                            }
                        }
                    }
                }
            }
        }

        val currentNode = searchInfo.plyInfoList[ply]

        // Prunes
        if (prunable) {
            if (eval == EvalConstants.SCORE_UNKNOWN) {
                eval = GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board, currentNode.attackInfo)
            }

            // Futility pruning
            if (newDepth < TunableConstants.FUTILITY_CHILD_MARGIN.size &&
                eval < EvalConstants.SCORE_KNOW_WIN) {
                if (eval - TunableConstants.FUTILITY_CHILD_MARGIN[newDepth] >= currentBeta) {
                    return eval
                }
            }

            // Razoring
            if (newDepth < TunableConstants.RAZOR_MARGIN.size) {
                val razorAlpha = currentAlpha - TunableConstants.RAZOR_MARGIN[newDepth]
                if (eval < razorAlpha) {
                    val razorSearchValue = search(board, moveList, 0, ply, razorAlpha, razorAlpha + 1, false)
                    if (razorSearchValue <= razorAlpha) {
                        return razorSearchValue
                    }
                }
            }

            // Null move pruning
            if (!skipNullMove &&
                eval >= currentBeta &&
                board.hasNonPawnMaterial(board.colorToMove)) {
                board.doNullMove()
                val reduction = 3 + newDepth / 3
                val score = -search(board, moveList, newDepth - reduction, ply + 1, -currentBeta, -currentBeta + 1,
                    true)
                board.undoNullMove()
                if (score >= currentBeta) {
                    return score
                }
            }
        }

        var bestMove = Move.NONE
        var bestScore = EvalConstants.SCORE_MIN

        if (!moveList.startPly()) {
            return if (eval == EvalConstants.SCORE_UNKNOWN) {
                GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(board, currentNode.attackInfo)
            } else {
                eval
            }
        }

        var movesPerformed = 0
        var searchAlpha = currentAlpha
        var phase = PHASE_TT
        while (phase > PHASE_END) {
            when (phase) {
                PHASE_TT -> {
                    if (SearchConstants.USE_TT &&
                        pvNode &&
                        foundInfo == TranspositionTable.EMPTY_INFO &&
                        newDepth > SearchConstants.IID_DEPTH) {
                        search(board, moveList, newDepth - SearchConstants.IID_DEPTH, ply, currentAlpha,
                            currentBeta, false)
                        foundInfo = TranspositionTable.findEntry(board)
                    }
                    if (foundInfo != TranspositionTable.EMPTY_INFO) {
                        val ttMove = TranspositionTable.getMove(foundInfo)
                        if (ttMove != Move.NONE) {
                            moveList.addMove(ttMove, 0)
                        }
                        currentNode.setTTMove(ttMove)
                    }

                }
                PHASE_ATTACK -> {
                    searchInfo.moveGenerator.legalAttacks(board, currentNode.attackInfo, moveList)
                }
                PHASE_KILLER_1 -> {
                    val killerMove = currentNode.killerMove1
                    if (killerMove != Move.NONE &&
                        !currentNode.isTTMove(killerMove) &&
                        MoveGenerator.isLegalQuietMove(board, currentNode.attackInfo, killerMove)) {
                        moveList.addMove(killerMove, 0)
                    }
                }
                PHASE_KILLER_2 -> {
                    val killerMove = currentNode.killerMove2
                    if (killerMove != Move.NONE &&
                        !currentNode.isTTMove(killerMove) &&
                        MoveGenerator.isLegalQuietMove(board, currentNode.attackInfo, killerMove)) {
                        moveList.addMove(killerMove, 0)
                    }
                }
                PHASE_QUIET -> {
                    searchInfo.moveGenerator.legalMoves(board, currentNode.attackInfo, moveList)
                }
            }
            while (moveList.hasNext()) {
                val move = moveList.next()
                if (foundInfo != TranspositionTable.EMPTY_INFO && phase != PHASE_TT && currentNode.isTTMove(move)) {
                    continue
                }
                if (phase == PHASE_QUIET && currentNode.isKillerMove(move)) {
                    continue
                }
                if (!board.isLegalMove(move)) {
                    continue
                }

                val moveType = Move.getMoveType(move)
                val isPromotion = MoveType.isPromotion(moveType)
                val toSquare = Move.getToSquare(move)
                val capturedPiece = board.pieceTypeBoard[toSquare]

                val isCapture = capturedPiece != Piece.NONE
                val isQuiet = !isCapture && moveType == MoveType.TYPE_NORMAL

                if (prunable &&
                    !isPromotion &&
                    movesPerformed > 0) {

                    if (newDepth < SearchConstants.LMP_DEPTH) {
                        if (movesPerformed > depth * SearchConstants.LMP_MULTIPLIER + SearchConstants.LMP_MIN_MOVES) {
                            if (phase == PHASE_QUIET) {
                                break
                            } else {
                                continue
                            }
                        }
                    }

                    if (!isCapture &&
                        newDepth < TunableConstants.FUTILITY_PARENT_MARGIN.size) {
                        val futilityValue = eval + TunableConstants.FUTILITY_PARENT_MARGIN[newDepth]
                        if (futilityValue <= searchAlpha) {
                            if (futilityValue > bestScore) {
                                bestScore = futilityValue
                                bestMove = move
                            }
                            continue
                        }
                    }

                    if (newDepth < SearchConstants.NEGATIVE_SEE_DEPTH) {
                        if (!StaticExchangeEvaluator.seeInThreshold(board, move, 0)) {
                            continue
                        }
                    }
                }

                movesPerformed++
                board.doMove(move)

                var score = EvalConstants.SCORE_MAX

                if (DrawEvaluator.isDrawByRules(board) || !DrawEvaluator.hasSufficientMaterial(board)) {
                    score = EvalConstants.SCORE_DRAW
                } else {
                    // Reductions
                    var reduction = 1
                    if (newDepth > SearchConstants.LMR_MIN_DEPTH &&
                        movesPerformed > SearchConstants.LMR_MIN_MOVES &&
                        !isCapture &&
                        !isPromotion) {

                        reduction = TunableConstants.LMR_TABLE[min(newDepth, 63)][min(movesPerformed, 63)]
                        if (!pvNode) {
                            reduction += 1
                        }
                        reduction = min(newDepth - 1, max(reduction, 1))
                    }

                    val searchDepth = newDepth - 1

                    // LMR Search
                    if (reduction != 1) {
                        score = -search(board, moveList, newDepth - reduction, ply + 1, -searchAlpha - 1,
                            -searchAlpha, false)
                    }

                    // PVS Search
                    if ((reduction == 1 && (!pvNode || movesPerformed != 1)) || (reduction != 1 && score > searchAlpha)) {
                        score = -search(board, moveList, searchDepth, ply + 1, -searchAlpha - 1, -searchAlpha, false)
                    }

                    // Normal search for nodes with similar score on previous search
                    // Only pvNodes or it will be equal to PVS
                    if (pvNode && score > searchAlpha) {
                        score = -search(board, moveList, searchDepth, ply + 1, -currentBeta, -searchAlpha, false)
                    }
                }

                board.undoMove(move)

                searchAlpha = max(searchAlpha, score)

                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }

                if (searchAlpha >= currentBeta) {
                    if (isQuiet) {
                        currentNode.addKillerMove(move)
                        searchInfo.history.addHistory(board.colorToMove, move, depth * depth)
                    }
                    phase = PHASE_END
                    break
                }
                if (isQuiet) {
                    searchInfo.history.addHistory(board.colorToMove, move, -depth * depth)
                }
            }
            phase--
        }
        moveList.endPly()

        if (movesPerformed == 0) {
            bestMove = Move.NONE
            bestScore = if (inCheck) {
                // MATED
                currentAlpha
            } else {
                // STALEMATE
                EvalConstants.SCORE_DRAW
            }
        }

        val scoreType = when {
            bestScore >= beta -> HashConstants.SCORE_TYPE_BOUND_LOWER
            bestScore <= alpha -> HashConstants.SCORE_TYPE_BOUND_UPPER
            else -> HashConstants.SCORE_TYPE_EXACT_SCORE
        }

        if (!searchOptions.stop) {
            TranspositionTable.save(board, eval, bestScore, scoreType, newDepth, ply, bestMove)
        }

        return bestScore
    }

    // Interactive deepening with aspiration window
    fun search(board: Board) {
        searchInfo.reset()

        val moveList = MoveList()

        TranspositionTable.baseDepth = board.moveNumber

        var depth = 1
        var alpha = EvalConstants.SCORE_MIN
        var beta = EvalConstants.SCORE_MAX
        var score = EvalConstants.SCORE_MIN
        var wantedSearchTime = searchOptions.minSearchTimeLimit

        val searchTimeIncrement = searchOptions.searchTimeIncrement

        while (depth <= searchOptions.depth) {
            if (searchOptions.stop && searchInfo.bestMove != Move.NONE) {
                break
            }
            var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE

            while (true) {
                val previousScore = score

                score = search(board, moveList, depth, 0, alpha, beta, true)

                searchInfo.save(board)

                val currentTime = Utils.specific.currentTimeMillis()
                searchInfoListener.searchInfo(depth, currentTime - searchOptions.startTime, searchInfo)

                if (depth > 4) {
                    if (score < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 10 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 50 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 100 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score > previousScore &&
                        wantedSearchTime > searchOptions.minSearchTimeLimit) {
                        wantedSearchTime -= searchTimeIncrement
                    }
                }

                if (wantedSearchTime < currentTime) {
                    searchOptions.stop = true
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
        searchOptions.stop = true

        searchInfoListener.bestMove(searchInfo.bestMove)
    }

    // Used by helper threads
    fun searchStep(board: Board, moveList: MoveList, previousScore: Int, depth: Int): Int {
        if (previousScore == EvalConstants.SCORE_UNKNOWN) {
            val score = search(board, moveList, depth, 0, EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
            searchInfo.save(board)
            return score
        }

        var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE
        var alpha = max(previousScore - aspirationWindow, EvalConstants.SCORE_MIN)
        var beta = min(previousScore + aspirationWindow, EvalConstants.SCORE_MAX)

        while (true) {
            val score = search(board, moveList, depth, 0, alpha, beta)

            when {
                score <= alpha -> {
                    alpha = if (score < -EvalConstants.SCORE_MATE) {
                        EvalConstants.SCORE_MIN
                    } else {
                        max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                    }
                }
                score >= beta -> {
                    beta = if (score > EvalConstants.SCORE_MATE) {
                        EvalConstants.SCORE_MAX
                    } else {
                        min(score + aspirationWindow, EvalConstants.SCORE_MAX)
                    }
                }
                else -> {
                    searchInfo.save(board)
                    return score
                }
            }

            aspirationWindow += aspirationWindow / 4
        }
    }

    companion object {
        private const val PHASE_END = 0
        private const val PHASE_QUIET = 1
        private const val PHASE_KILLER_2 = 2
        private const val PHASE_KILLER_1 = 3
        private const val PHASE_ATTACK = 4
        private const val PHASE_TT = 5
    }
}