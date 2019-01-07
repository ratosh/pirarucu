package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.cache.PawnEvaluationCache
import pirarucu.move.BitboardMove
import pirarucu.tuning.TunableConstants

object PawnEvaluator {

    private val FRONTSPAN_MASK = Array(Color.SIZE) { LongArray(Square.SIZE) }
    private val PASSED_MASK = Array(Color.SIZE) { LongArray(Square.SIZE) }
    private val NEIGHBOURS_MASK = LongArray(Square.SIZE)

    private val pawnEvalCache = PawnEvaluationCache(32)

    init {
        initializeFrontSpan()
        initializePassed()
        initializeNeighbours()
    }

    fun reset() {
        pawnEvalCache.reset()
    }

    private fun initializeFrontSpan() {
        for (square in Square.A1 until Square.SIZE) {
            FRONTSPAN_MASK[Color.WHITE][square] = createFrontSpan(Color.WHITE, square)
            FRONTSPAN_MASK[Color.BLACK][square] = createFrontSpan(Color.BLACK, square)
        }
    }

    private fun createFrontSpan(color: Int, square: Int): Long {
        var result = 0L
        var movingSquare = square + BitboardMove.PAWN_FORWARD[color]
        while (Square.isValid(movingSquare)) {
            result = result or Bitboard.getBitboard(movingSquare)
            movingSquare += BitboardMove.PAWN_FORWARD[color]
        }
        return result
    }

    private fun initializePassed() {
        for (square in Square.A1 until Square.SIZE) {
            PASSED_MASK[Color.WHITE][square] = createPassedMask(Color.WHITE, square)
            PASSED_MASK[Color.BLACK][square] = createPassedMask(Color.BLACK, square)
        }
    }

    private fun createPassedMask(color: Int, square: Int): Long {
        return when (File.getFile(square)) {
            File.FILE_A -> {
                FRONTSPAN_MASK[color][square] or
                    FRONTSPAN_MASK[color][square + BitboardMove.EAST]
            }
            File.FILE_H -> {
                FRONTSPAN_MASK[color][square] or
                    FRONTSPAN_MASK[color][square + BitboardMove.WEST]
            }
            else -> {
                (FRONTSPAN_MASK[color][square] or
                    FRONTSPAN_MASK[color][square + BitboardMove.EAST] or
                    FRONTSPAN_MASK[color][square + BitboardMove.WEST])
            }
        }
    }

    private fun initializeNeighbours() {
        for (square in Square.A1 until Square.SIZE) {
            NEIGHBOURS_MASK[square] = createNeighboursMask(square)
        }
    }

    private fun createNeighboursMask(square: Int): Long {
        return Bitboard.FILES_ADJACENT[File.getFile(square)]
    }

    fun evaluate(board: Board, attackInfo: AttackInfo): Int {
        var structureScore = EvalConstants.SCORE_UNKNOWN
        if (EvalConstants.PAWN_EVAL_CACHE) {
            structureScore = pawnEvalCache.findEntry(board)
        }
        if (structureScore == EvalConstants.SCORE_UNKNOWN) {
            structureScore = evaluatePawnStructure(board, attackInfo, Color.WHITE, Color.BLACK) -
                evaluatePawnStructure(board, attackInfo, Color.BLACK, Color.WHITE)
            pawnEvalCache.saveEntry(board, structureScore, board.evalInfo.passedPawnBitboard)
        }

        return structureScore +
            evaluatePassedPawn(board, attackInfo, Color.WHITE, Color.BLACK) -
            evaluatePassedPawn(board, attackInfo, Color.BLACK, Color.WHITE)
    }

    private fun evaluatePawnStructure(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN]
        val ourPawns = board.pieceBitboard[ourColor][Piece.PAWN]
        val theirPawns = board.pieceBitboard[theirColor][Piece.PAWN]
        var result = 0
        while (tmpPieces != Bitboard.EMPTY) {
            val pawnSquare = Square.getSquare(tmpPieces)

            val stoppers = PASSED_MASK[ourColor][pawnSquare] and theirPawns
            val supporters = PASSED_MASK[theirColor][pawnSquare] and ourPawns
            val stack = FRONTSPAN_MASK[ourColor][pawnSquare] and ourPawns
            val neighbours = NEIGHBOURS_MASK[pawnSquare] and ourPawns
            val defended = BitboardMove.PAWN_ATTACKS[theirColor][pawnSquare] and ourPawns
            val phalanx = BitboardMove.NEIGHBOURS[pawnSquare] and ourPawns

            if (stack != Bitboard.EMPTY) {
                result += TunableConstants.PAWN_STRUCTURE[TunableConstants.PAWN_STRUCTURE_STACKED]
            } else if (stoppers == Bitboard.EMPTY) {
                board.evalInfo.passedPawnBitboard = board.evalInfo.passedPawnBitboard or
                    Bitboard.getBitboard(pawnSquare)
            }

            if (neighbours == Bitboard.EMPTY) {
                result += TunableConstants.PAWN_STRUCTURE[TunableConstants.PAWN_STRUCTURE_ISOLATED]
            } else {
                if (defended != Bitboard.EMPTY) {
                    result += TunableConstants.PAWN_STRUCTURE[TunableConstants.PAWN_STRUCTURE_DEFENDED]
                } else if (supporters == Bitboard.EMPTY) {
                    result += TunableConstants.PAWN_STRUCTURE[TunableConstants.PAWN_STRUCTURE_BACKWARD]
                }
                if (phalanx != Bitboard.EMPTY) {
                    result += TunableConstants.PAWN_STRUCTURE[TunableConstants.PAWN_STRUCTURE_PHALANX]
                }
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    private fun evaluatePassedPawn(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var result = 0

        var tmpPieces = board.colorBitboard[ourColor] and board.evalInfo.passedPawnBitboard

        while (tmpPieces != Bitboard.EMPTY) {
            val pawnSquare = Square.getSquare(tmpPieces)
            val bitboard = Bitboard.getBitboard(pawnSquare)

            val pawnAdvance = BitboardMove.PAWN_MOVES[ourColor][pawnSquare]
            val theirAttacks = attackInfo.attacksBitboard[theirColor][Piece.NONE]
            val ourAttacks = attackInfo.attacksBitboard[ourColor][Piece.NONE]

            val safe = bitboard and theirAttacks == Bitboard.EMPTY
            val canAdvance = pawnAdvance and board.gameBitboard == Bitboard.EMPTY
            val safeAdvance = pawnAdvance and theirAttacks == Bitboard.EMPTY
            val safePath = FRONTSPAN_MASK[ourColor][pawnSquare] and theirAttacks and
                pawnAdvance.inv() == Bitboard.EMPTY
            val defended = bitboard and ourAttacks != Bitboard.EMPTY
            val defendedAdvance = pawnAdvance and ourAttacks != Bitboard.EMPTY

            val relativeRank = Rank.getRelativeRank(ourColor, Rank.getRank(pawnSquare))

            if (safe) {
                result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_SAFE]
            }

            if (canAdvance) {
                result += TunableConstants.PASSED_PAWN[relativeRank]
            } else {
                result += TunableConstants.PASSED_PAWN_BLOCKED[relativeRank]
            }

            if (safeAdvance) {
                result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_SAFE_ADVANCE]
            }

            if (safePath) {
                result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_SAFE_PATH]
            }

            if (defended) {
                result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_DEFENDED]
            }

            if (defendedAdvance) {
                result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_DEFENDED_ADVANCE]
            }

            result += TunableConstants.PASSED_PAWN_BONUS[TunableConstants.PASSED_PAWN_KING_DISTANCE] *
                (Square.SQUARE_DISTANCE[pawnSquare][board.kingSquare[theirColor]] -
                    Square.SQUARE_DISTANCE[pawnSquare][board.kingSquare[ourColor]])

            tmpPieces = tmpPieces and tmpPieces - 1
        }

        return result
    }
}
