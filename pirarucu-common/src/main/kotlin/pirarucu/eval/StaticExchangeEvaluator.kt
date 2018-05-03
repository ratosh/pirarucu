package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.stats.Statistics
import pirarucu.tuning.TunableConstants
import kotlin.math.max

object StaticExchangeEvaluator {


    private fun getSmallestAttackBitboard(board: Board,
                                          colorToMove: Int,
                                          toSquare: Int,
                                          allPieces: Long): Long {
        var attackBitboard = allPieces and
            board.pieceBitboard[colorToMove][Piece.PAWN] and
            BitboardMove.PAWN_ATTACKS[Color.invertColor(colorToMove)][toSquare]

        if (attackBitboard != Bitboard.EMPTY) {
            return attackBitboard
        }

        attackBitboard = allPieces and
            board.pieceBitboard[colorToMove][Piece.KNIGHT] and
            BitboardMove.KNIGHT_MOVES[toSquare]

        if (attackBitboard != Bitboard.EMPTY) {
            return attackBitboard
        }

        val pseudoBishops = allPieces and BitboardMove.BISHOP_PSEUDO_MOVES[toSquare]
        if (pseudoBishops != Bitboard.EMPTY) {
            val bishops = pseudoBishops and board.pieceBitboard[colorToMove][Piece.BISHOP]
            if (bishops != Bitboard.EMPTY) {
                attackBitboard = bishops and BitboardMove.bishopMoves(toSquare, allPieces)
            }
        }

        if (attackBitboard != Bitboard.EMPTY) {
            return attackBitboard
        }

        val pseudoRooks = allPieces and BitboardMove.ROOK_PSEUDO_MOVES[toSquare]
        if (pseudoRooks != Bitboard.EMPTY) {
            val rooks = pseudoRooks and board.pieceBitboard[colorToMove][Piece.ROOK]
            if (rooks != Bitboard.EMPTY) {
                attackBitboard = rooks and BitboardMove.rookMoves(toSquare, allPieces)
            }
        }

        if (attackBitboard != Bitboard.EMPTY) {
            return attackBitboard
        }

        val pseudoQueen = pseudoBishops or pseudoRooks
        if (pseudoQueen != Bitboard.EMPTY) {
            val queens = pseudoQueen and board.pieceBitboard[colorToMove][Piece.QUEEN]
            if (queens != Bitboard.EMPTY) {
                attackBitboard = queens and
                    (BitboardMove.bishopMoves(toSquare, allPieces) or BitboardMove.rookMoves(toSquare, allPieces))
            }
        }

        if (attackBitboard != Bitboard.EMPTY) {
            return attackBitboard
        }

        attackBitboard = allPieces and
            board.pieceBitboard[colorToMove][Piece.KING] and
            BitboardMove.KING_MOVES[toSquare]

        return attackBitboard
    }

    fun getSeeCaptureScore(board: Board, move: Int): Int {
        if (Statistics.ENABLED) {
            Statistics.seeNodes++
        }
        val gain = IntArray(32)
        var depth = 0

        var fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        var fromBitboard = Bitboard.getBitboard(fromSquare)

        var occupied = board.gameBitboard

        val target = board.pieceTypeBoard[toSquare]
        gain[depth] = TunableConstants.SEE_VALUE[target]
        var attackingPiece = board.pieceTypeBoard[fromSquare]
        var colorToMove = board.nextColorToMove
        do {
            depth++
            gain[depth] = TunableConstants.SEE_VALUE[attackingPiece] - gain[depth - 1]
            if (max(-gain[depth - 1], gain[depth]) < 0) {
                break
            }
            occupied = occupied xor fromBitboard
            fromBitboard = getSmallestAttackBitboard(board, colorToMove, toSquare, occupied)
            if (fromBitboard == Bitboard.EMPTY) {
                break
            }
            fromSquare = Square.getSquare(fromBitboard)
            fromBitboard = Bitboard.getBitboard(fromSquare)
            attackingPiece = board.pieceTypeBoard[fromSquare]
            colorToMove = Color.invertColor(colorToMove)
        } while (true)
        while (--depth > 0) {
            gain[depth - 1] = -max(-gain[depth - 1], gain[depth])
        }
        return gain[0]
    }
}