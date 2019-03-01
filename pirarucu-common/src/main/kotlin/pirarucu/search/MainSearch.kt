package pirarucu.search

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Piece
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.eval.StaticExchangeEvaluator
import pirarucu.game.GameConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.move.MovePicker
import pirarucu.move.MoveType
import pirarucu.tuning.TunableConstants
import pirarucu.util.PlatformSpecific
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainSearch(
    private val searchOptions: SearchOptions,
    private val searchInfoListener: SearchInfoListener,
    private val transpositionTable: TranspositionTable,
    private val pawnEvaluationCache: PawnEvaluationCache,
    val history: History
) {

    val searchInfo = SearchInfo(transpositionTable, history)

    private val quiescenceSearch = QuiescenceSearch(searchInfo, pawnEvaluationCache)

    private fun search(
        board: Board,
        depth: Int,
        ply: Int,
        alpha: Int,
        beta: Int,
        skipNullMove: Boolean = false
    ): Int {
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
            return quiescenceSearch.search(board, ply, alpha, beta)
        }
        val rootNode = ply == 0

        searchInfo.searchNodes++
        if (!rootNode &&
            searchInfo.searchNodes and 0xFFFL == 0xFFFL &&
            searchOptions.maxSearchTimeLimit < PlatformSpecific.currentTimeMillis()
        ) {
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

        var foundInfo = transpositionTable.findEntry(board)
        var ttScore = EvalConstants.SCORE_UNKNOWN
        var ttScoreType = HashConstants.SCORE_TYPE_EXACT_SCORE
        var ttDepth = 0
        if (foundInfo != HashConstants.EMPTY_INFO) {
            eval = transpositionTable.getEval(foundInfo)
            ttScore = transpositionTable.getScore(foundInfo, ply)
            ttScoreType = transpositionTable.getScoreType(foundInfo)
            ttDepth = transpositionTable.getDepth(foundInfo)
            if (!pvNode && ttDepth >= newDepth) {
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

        val currentNode = searchInfo.plyInfoList[ply]

        if (ply >= GameConstants.MAX_PLIES) {
            return if (eval == EvalConstants.SCORE_UNKNOWN) {
                GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(
                    board,
                    currentNode.attackInfo,
                    pawnEvaluationCache
                )
            } else {
                eval
            }
        }

        // Prunes
        if (prunable) {
            if (eval == EvalConstants.SCORE_UNKNOWN) {
                eval = GameConstants.COLOR_FACTOR[board.colorToMove] * Evaluator.evaluate(
                    board, currentNode.attackInfo,
                    pawnEvaluationCache
                )
            }

            // Futility pruning
            if (newDepth < TunableConstants.FUTILITY_CHILD_MARGIN.size &&
                eval < EvalConstants.SCORE_KNOW_WIN
            ) {
                if (eval - TunableConstants.FUTILITY_CHILD_MARGIN[newDepth] >= currentBeta) {
                    return eval
                }
            }

            // Razoring
            if (newDepth < TunableConstants.RAZOR_MARGIN.size) {
                val razorAlpha = currentAlpha - TunableConstants.RAZOR_MARGIN[newDepth]
                if (eval < razorAlpha) {
                    val razorSearchValue = search(board, 0, ply, razorAlpha, razorAlpha + 1, false)
                    if (razorSearchValue <= razorAlpha) {
                        return razorSearchValue
                    }
                }
            }

            // Null move pruning
            if (!skipNullMove &&
                eval >= currentBeta &&
                board.hasNonPawnMaterial(board.colorToMove)
            ) {
                board.doNullMove()
                val reduction = 3 + newDepth / 3
                val score = -search(
                    board, newDepth - reduction, ply + 1, -currentBeta, -currentBeta + 1,
                    true
                )
                board.undoNullMove()
                if (score >= currentBeta) {
                    return score
                }
            }

            // ProbCut
            // Prune the previous move if we find a capture above prob cut in a lower search.
            if (newDepth > SearchConstants.PROB_CUT_DEPTH &&
                abs(beta) < EvalConstants.SCORE_MATE
            ) {

                // Cut beta bound
                val probBeta = min(beta + SearchConstants.PROB_CUT_MARGIN, EvalConstants.SCORE_MATE - 1)

                // Use good capture move picker
                currentNode.setupMovePicker(board, probBeta - eval)

                while (true) {
                    val move = currentNode.next(true)
                    // Move picker finish
                    if (move == Move.NONE) {
                        break
                    }

                    // Move is not legal
                    if (!board.isLegalMove(move)) {
                        continue
                    }

                    board.doMove(move)

                    // Verify the move using a low depth search
                    var value = -search(board, SearchConstants.PROB_CUT_LOW_DEPTH, ply + 1, -probBeta, -probBeta + 1)

                    // Verify the move using a deeper search
                    if (value >= probBeta &&
                        newDepth > SearchConstants.PROB_CUT_DEPTH + SearchConstants.PROB_CUT_LOW_DEPTH
                    ) {
                        value =
                            -search(board, newDepth - SearchConstants.PROB_CUT_DEPTH, ply + 1, -probBeta, -probBeta + 1)
                    }
                    board.undoMove(move)

                    // Value is above beta cut
                    if (value >= probBeta) {
                        return value
                    }
                }
            }
        }

        var ttMove = Move.NONE
        var ttMoveSingular = false

        if (foundInfo != HashConstants.EMPTY_INFO) {
            ttMove = transpositionTable.getMove(foundInfo)
            // Singular TT move detection
            if (!rootNode &&
                ttMove != Move.NONE &&
                newDepth > SearchConstants.SINGULAR_DETECTION_DEPTH &&
                ttScoreType == HashConstants.SCORE_TYPE_BOUND_LOWER &&
                ttDepth >= newDepth - 3
            ) {

                board.doMove(ttMove)
                if (board.basicEvalInfo.checkBitboard == Bitboard.EMPTY) {
                    ttMoveSingular = true
                }
                board.undoMove(ttMove)

                if (ttMoveSingular) {
                    // Singular bound
                    val singularCut = max(ttScore - 2 * newDepth, -EvalConstants.SCORE_MATE)

                    // Use move picker
                    currentNode.setupMovePicker(board, 0, Move.NONE)

                    var singularMoveCount = 0

                    while (singularMoveCount < SearchConstants.SINGULAR_DETECTION_MOVES) {
                        val move = currentNode.next(false)
                        // Move picker finish
                        if (move == Move.NONE) {
                            break
                        }

                        // Move is not legal
                        if (move == ttMove || !board.isLegalMove(move)) {
                            continue
                        }

                        singularMoveCount++

                        board.doMove(move)

                        // Verify the move using a low depth search
                        val value = -search(board, newDepth / 2, ply + 1, -singularCut - 1, -singularCut)

                        board.undoMove(move)

                        // Value is above beta cut
                        if (value > singularCut) {
                            ttMoveSingular = false
                            break
                        }
                    }
                }
            }
            // IID
        } else if (pvNode && newDepth > SearchConstants.IID_DEPTH) {
            search(
                board, newDepth - SearchConstants.IID_DEPTH, ply, currentAlpha,
                currentBeta, false
            )
            foundInfo = transpositionTable.findEntry(board)

            if (foundInfo != HashConstants.EMPTY_INFO) {
                ttMove = transpositionTable.getMove(foundInfo)
            }
        }

        currentNode.setupMovePicker(board, 0, ttMove)

        val futilityValue = if (newDepth < TunableConstants.FUTILITY_PARENT_MARGIN.size) {
            eval + TunableConstants.FUTILITY_PARENT_MARGIN[newDepth]
        } else {
            0
        }

        var bestMove = Move.NONE
        var bestScore = EvalConstants.SCORE_MIN

        var movesPerformed = 0
        var searchAlpha = currentAlpha
        var skipQuiets = false

        val powerDepth = newDepth * newDepth

        while (true) {
            val move = currentNode.next(skipQuiets)
            if (move == Move.NONE) {
                break
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
                movesPerformed > 0
            ) {

                if (newDepth < SearchConstants.LMP_DEPTH) {
                    if (movesPerformed > newDepth * SearchConstants.LMP_MULTIPLIER + SearchConstants.LMP_MIN_MOVES) {
                        skipQuiets = true
                    }
                }

                if (!isCapture &&
                    newDepth < TunableConstants.FUTILITY_PARENT_MARGIN.size &&
                    futilityValue <= searchAlpha
                ) {
                    skipQuiets = true
                }

                if (!isCapture &&
                    newDepth < TunableConstants.FUTILITY_HISTORY_MARGIN.size &&
                    history.getHistoryScore(board.colorToMove, move) <
                    TunableConstants.FUTILITY_HISTORY_MARGIN[newDepth]
                ) {
                    skipQuiets = true
                }

                if (newDepth < SearchConstants.NEGATIVE_SEE_DEPTH &&
                    currentNode.phase < MovePicker.PHASE_GOOD_MATERIAL_EXCHANGE &&
                    !StaticExchangeEvaluator.seeInThreshold(
                        board, move,
                        SearchConstants.NEGATIVE_SEE_MARGIN * powerDepth
                    )
                ) {
                    continue
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
                    !isPromotion
                ) {

                    reduction = TunableConstants.LMR_TABLE[min(newDepth, 63)][min(movesPerformed, 63)]

                    if (currentNode.isKillerMove(move)) {
                        reduction -= 1
                    }
                    if (!pvNode) {
                        reduction += 1
                    }
                    reduction = min(newDepth - 1, max(reduction, 1))
                }

                var searchDepth = newDepth

                if (move == ttMove && ttMoveSingular) {
                    searchDepth += 1
                }

                // LMR Search
                if (reduction != 1 || !pvNode || movesPerformed != 1) {
                    score = -search(
                        board, searchDepth - reduction, ply + 1, -searchAlpha - 1,
                        -searchAlpha, false
                    )
                }

                // PVS Search
                if (reduction != 1 && score > searchAlpha) {
                    score = -search(board, searchDepth - 1, ply + 1, -searchAlpha - 1, -searchAlpha, false)
                }

                // Normal search for nodes with similar score on previous search
                // Only pvNodes or it will be equal to PVS
                if (pvNode && score > searchAlpha) {
                    score = -search(board, searchDepth - 1, ply + 1, -currentBeta, -searchAlpha, false)
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
                    history.addHistory(board.colorToMove, move, powerDepth)
                }
                break
            }
            if (isQuiet) {
                history.addHistory(board.colorToMove, move, -powerDepth)
            }
        }

        val scoreType = when {
            movesPerformed == 0 -> {
                bestMove = Move.NONE
                bestScore = if (inCheck) {
                    // MATED
                    EvalConstants.SCORE_MIN + ply
                } else {
                    // STALEMATE
                    EvalConstants.SCORE_DRAW
                }
                HashConstants.SCORE_TYPE_EXACT_SCORE
            }
            bestScore >= beta -> HashConstants.SCORE_TYPE_BOUND_LOWER
            bestScore <= alpha -> HashConstants.SCORE_TYPE_BOUND_UPPER
            else -> HashConstants.SCORE_TYPE_EXACT_SCORE
        }

        if (!searchOptions.stop) {
            transpositionTable.save(board, eval, bestScore, scoreType, newDepth, ply, bestMove)
        }

        return bestScore
    }

    // Interactive deepening with aspiration window
    fun search(board: Board) {
        searchInfo.reset()

        transpositionTable.baseDepth = board.moveNumber

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
            var aspirationWindow = SearchConstants.ASPIRATION_WINDOW_SIZE[0]
            if (depth > 4) {
                alpha = max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                beta = min(score + aspirationWindow, EvalConstants.SCORE_MAX)
            }

            while (true) {
                val previousScore = score

                score = search(board, depth, 0, alpha, beta, true)
                if (searchOptions.stop) {
                    break
                }

                if (depth > 4) {
                    if (score < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit
                    ) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 10 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit
                    ) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 50 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit
                    ) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score + 100 < previousScore &&
                        wantedSearchTime < searchOptions.maxSearchTimeLimit
                    ) {
                        wantedSearchTime += searchTimeIncrement
                    }
                    if (score > previousScore &&
                        wantedSearchTime > searchOptions.minSearchTimeLimit
                    ) {
                        wantedSearchTime -= searchTimeIncrement
                    }
                }

                val currentTime = PlatformSpecific.currentTimeMillis()

                searchInfo.save(board)
                searchInfoListener.searchInfo(depth, currentTime - searchOptions.startTime, searchInfo)

                if (wantedSearchTime < currentTime) {
                    searchOptions.stop = true
                    break
                }

                // fail low
                if (score <= alpha) {
                    alpha = max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                    beta = (alpha + beta) / 2
                    // fail high
                } else if (score >= beta) {
                    beta = min(score + aspirationWindow, EvalConstants.SCORE_MAX)
                    // Search result inside window
                } else {
                    break
                }

                aspirationWindow += aspirationWindow / 4
            }
            depth++
        }
        searchOptions.stop = true
    }

    // Used by helper threads
    fun searchStep(board: Board, previousScore: Int, depth: Int, initialAspirationWindow: Int): Int {
        if (previousScore == EvalConstants.SCORE_UNKNOWN) {
            val score = search(board, depth, 0, EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
            searchInfo.save(board)
            return score
        }

        var aspirationWindow = initialAspirationWindow
        var alpha = max(previousScore - aspirationWindow, EvalConstants.SCORE_MIN)
        var beta = min(previousScore + aspirationWindow, EvalConstants.SCORE_MAX)

        while (true) {
            val score = search(board, depth, 0, alpha, beta)

            if (score <= alpha) {
                alpha = max(score - aspirationWindow, EvalConstants.SCORE_MIN)
                beta = (alpha + beta) / 2
                // fail high
            } else if (score >= beta) {
                beta = min(score + aspirationWindow, EvalConstants.SCORE_MAX)
                // Search result inside window
            } else {
                searchInfo.save(board)
                return score
            }

            aspirationWindow += aspirationWindow / 4
        }
    }
}