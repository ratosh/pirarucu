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
        if (EvalConstants.PAWN_EVAL_CACHE) {
            val info = pawnEvalCache.findEntry(board)
            if (info != EvalConstants.SCORE_UNKNOWN) {
                return info
            }
        }

        val result = evaluatePawn(board, attackInfo, Color.WHITE, Color.BLACK) -
            evaluatePawn(board, attackInfo, Color.BLACK, Color.WHITE)

        pawnEvalCache.saveEntry(board, result)

        return result
    }

    private fun evaluatePawn(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN]
        val ourPawns = board.pieceBitboard[ourColor][Piece.PAWN]
        val theirPawns = board.pieceBitboard[theirColor][Piece.PAWN]
        var result = 0
        while (tmpPieces != Bitboard.EMPTY) {
            val pawnSquare = Square.getSquare(tmpPieces)
            val relativeRank = Rank.getRelativeRank(ourColor, Rank.getRank(pawnSquare))

            val passed = PASSED_MASK[ourColor][pawnSquare] and theirPawns == Bitboard.EMPTY

            val isolated = NEIGHBOURS_MASK[pawnSquare] and ourPawns == Bitboard.EMPTY
            val phalanx = BitboardMove.NEIGHBOURS[pawnSquare] and ourPawns != Bitboard.EMPTY
            val supported = BitboardMove.PAWN_ATTACKS[theirColor][pawnSquare] and ourPawns != Bitboard.EMPTY
            val stacked = FRONTSPAN_MASK[ourColor][pawnSquare] and ourPawns != Bitboard.EMPTY
            val backward = PASSED_MASK[theirColor][pawnSquare] and ourPawns != Bitboard.EMPTY

            if (supported) {
                result += TunableConstants.PAWN_BONUS[TunableConstants.PAWN_BONUS_SUPPORTED]
            }

            if (phalanx) {
                result += TunableConstants.PAWN_BONUS[TunableConstants.PAWN_BONUS_PHALANX]
            }

            if (isolated) {
                result += TunableConstants.PAWN_BONUS[TunableConstants.PAWN_BONUS_ISOLATED]
            }

            if (stacked) {
                result += TunableConstants.PAWN_BONUS[TunableConstants.PAWN_BONUS_STACKED]
            }

            if (backward) {
                result += TunableConstants.PAWN_BONUS[TunableConstants.PAWN_BONUS_BACKWARD]
            }

            if (passed) {
                result += TunableConstants.PAWN_PASSED[relativeRank]
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }
}
