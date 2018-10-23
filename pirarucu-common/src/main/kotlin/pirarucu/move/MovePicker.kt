package pirarucu.move

import pirarucu.board.Board
import pirarucu.eval.AttackInfo
import pirarucu.eval.StaticExchangeEvaluator

class MovePicker {

    private var searchType = MAIN_SEARCH
    var phase = PHASE_END
        private set

    private var ttMove = Move.NONE

    private var killerMove1 = Move.NONE
    private var killerMove2 = Move.NONE

    private lateinit var board: Board
    private lateinit var attackInfo: AttackInfo
    private lateinit var moveGenerator: MoveGenerator

    private var threshold = 0

    private val exchangeMoveList = OrderedMoveList()
    private val quietMoveList = OrderedMoveList()
    private val badExchangeMoveList = SimpleMoveList()

    fun setup(board: Board,
              attackInfo: AttackInfo,
              moveGenerator: MoveGenerator,
              threshold: Int,
              ttMove: Int,
              killerMove1: Int,
              killerMove2: Int) {
        this.board = board
        this.attackInfo = attackInfo
        this.moveGenerator = moveGenerator

        this.phase = PHASE_TT

        this.searchType = MAIN_SEARCH

        this.ttMove = ttMove

        this.killerMove1 = killerMove1
        this.killerMove2 = killerMove2

        this.threshold = threshold

        exchangeMoveList.reset()
        quietMoveList.reset()
        badExchangeMoveList.reset()
    }

    fun setup(board: Board,
              attackInfo: AttackInfo,
              moveGenerator: MoveGenerator,
              threshold: Int) {
        this.board = board
        this.attackInfo = attackInfo
        this.moveGenerator = moveGenerator

        this.phase = PHASE_GEN_MATERIAL_EXCHANGE_MOVES

        this.searchType = Q_SEARCH

        this.ttMove = Move.NONE

        this.killerMove1 = Move.NONE
        this.killerMove2 = Move.NONE

        this.threshold = threshold

        exchangeMoveList.reset()
        quietMoveList.reset()
        badExchangeMoveList.reset()
    }

    fun next(skipQuiets: Boolean): Int {
        while (true) {
            when (phase) {
                PHASE_TT -> {
                    phase--
                    if (ttMove != Move.NONE) {
                        return ttMove
                    }
                }
                PHASE_GEN_MATERIAL_EXCHANGE_MOVES -> {
                    generateExchangeMoves(board, attackInfo)
                    phase--
                }
                PHASE_GOOD_MATERIAL_EXCHANGE -> {
                    while (exchangeMoveList.hasNext()) {
                        val currentMove = exchangeMoveList.next()
                        if (currentMove == ttMove) {
                            continue
                        }
                        if (!StaticExchangeEvaluator.seeInThreshold(board, currentMove, threshold)) {
                            if (searchType == MAIN_SEARCH) {
                                badExchangeMoveList.addMove(currentMove)
                            }
                            continue
                        }
                        if (currentMove == killerMove1) {
                            killerMove1 = Move.NONE
                        }
                        if (currentMove == killerMove2) {
                            killerMove2 = Move.NONE
                        }
                        return currentMove
                    }
                    phase = when {
                        searchType != MAIN_SEARCH -> PHASE_END
                        skipQuiets -> PHASE_BAD_MATERIAL_EXCHANGE
                        else -> PHASE_KILLER1
                    }
                }
                PHASE_KILLER1 -> {
                    phase--
                    if (!skipQuiets &&
                        killerMove1 != Move.NONE &&
                        killerMove1 != ttMove &&
                        MoveGenerator.isLegalQuietMove(board, attackInfo, killerMove1)) {
                        return killerMove1
                    }
                }
                PHASE_KILLER2 -> {
                    phase--
                    if (!skipQuiets &&
                        killerMove2 != Move.NONE &&
                        killerMove2 != ttMove &&
                        MoveGenerator.isLegalQuietMove(board, attackInfo, killerMove2)) {
                        return killerMove2
                    }
                }
                PHASE_GEN_QUIET -> {
                    if (!skipQuiets) {
                        generateQuietMoves(board, attackInfo)
                    }
                    phase--
                }
                PHASE_QUIET -> {
                    if (!skipQuiets) {
                        while (quietMoveList.hasNext()) {
                            val currentMove = quietMoveList.next()
                            if (currentMove == ttMove ||
                                currentMove == killerMove1 ||
                                currentMove == killerMove2) {
                                continue
                            }
                            return currentMove
                        }
                    }
                    phase--
                }
                PHASE_BAD_MATERIAL_EXCHANGE -> {
                    while (badExchangeMoveList.hasNext()) {
                        val currentMove = badExchangeMoveList.next()
                        if (currentMove == killerMove1 ||
                            currentMove == killerMove2) {
                            continue
                        }
                        return currentMove
                    }
                    phase--
                }
                PHASE_END -> {
                    return Move.NONE
                }
            }
        }
    }

    private fun generateExchangeMoves(board: Board, attackInfo: AttackInfo) {
        moveGenerator.legalAttacks(board, attackInfo, exchangeMoveList)
    }

    private fun generateQuietMoves(board: Board, attackInfo: AttackInfo) {
        moveGenerator.legalMoves(board, attackInfo, quietMoveList)
    }

    companion object {
        const val MAIN_SEARCH = 0
        const val Q_SEARCH = 1

        const val PHASE_TT = 8
        const val PHASE_GEN_MATERIAL_EXCHANGE_MOVES = 7
        const val PHASE_GOOD_MATERIAL_EXCHANGE = 6
        const val PHASE_KILLER1 = 5
        const val PHASE_KILLER2 = 4
        const val PHASE_GEN_QUIET = 3
        const val PHASE_QUIET = 2
        const val PHASE_BAD_MATERIAL_EXCHANGE = 1
        const val PHASE_END = 0
    }
}