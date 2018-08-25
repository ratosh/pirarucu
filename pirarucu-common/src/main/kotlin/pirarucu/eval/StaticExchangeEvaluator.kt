package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.move.MoveType
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

        return allPieces and
            board.pieceBitboard[colorToMove][Piece.KING] and
            BitboardMove.KING_MOVES[toSquare]
    }

    fun getSeeCaptureScore(board: Board, move: Int): Int {
        val gain = IntArray(32)
        var depth = 0

        var fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        var fromBitboard = Bitboard.getBitboard(fromSquare)

        var occupied = board.gameBitboard

        val target = if (Move.getMoveType(move) == MoveType.TYPE_PASSANT) {
            Piece.PAWN
        } else {
            board.pieceTypeBoard[toSquare]
        }

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

    // Based on Stockfish and Ethereal SEE
    fun seeInThreshold(board: Board, move: Int, threshold: Int): Boolean {
        val moveType = Move.getMoveType(move)
        // Castling moves should not allow piece trades
        if (moveType == MoveType.TYPE_CASTLING) {
            return 0 >= threshold
        }

        // Get move information
        var fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)

        // Get victims
        var firstVictim = board.pieceTypeBoard[toSquare]
        var nextVictim = board.pieceTypeBoard[fromSquare]

        // Get occupied board but remove pieces from the current move squares
        var occupied = board.gameBitboard xor
            Bitboard.getBitboard(toSquare) xor Bitboard.getBitboard(fromSquare)

        // Remove En passant piece
        if (moveType == MoveType.TYPE_PASSANT) {
            occupied = occupied xor Bitboard.getBitboard(board.epSquare)
            firstVictim = Piece.PAWN
        }

        // The best possible balance is not losing the moved piece
        var balance = TunableConstants.SEE_VALUE[firstVictim] - threshold

        // Test threshold
        if (balance < 0) {
            return false
        }

        // Worst balance is losing the moved piece
        balance -= TunableConstants.SEE_VALUE[nextVictim]

        // Test threshold
        if (balance >= 0) {
            return true
        }

        // Save sliders so we can update attack bitboard
        var bishops = (board.pieceBitboard[Color.WHITE][Piece.BISHOP] or
            board.pieceBitboard[Color.BLACK][Piece.BISHOP] or
            board.pieceBitboard[Color.WHITE][Piece.QUEEN] or
            board.pieceBitboard[Color.BLACK][Piece.QUEEN]) and occupied
        var rooks = (board.pieceBitboard[Color.WHITE][Piece.ROOK] or
            board.pieceBitboard[Color.BLACK][Piece.ROOK] or
            board.pieceBitboard[Color.WHITE][Piece.QUEEN] or
            board.pieceBitboard[Color.BLACK][Piece.QUEEN]) and occupied

        // Attacker bitboard - Pieces that can attack the square
        var attackers = attackersToSquare(board, occupied, toSquare, bishops, rooks) and occupied

        // Change turn
        var colorToMove = board.nextColorToMove

        while (true) {
            // Look for attackers with current color
            val colorAttackers = attackers and board.colorBitboard[colorToMove]
            // Stop if no attackers left
            if (colorAttackers == Bitboard.EMPTY) {
                break
            }

            // Find the weakest piece to attack with
            nextVictim = Piece.KING
            for (currentAttacker in Piece.PAWN until Piece.KING) {
                if (colorAttackers and board.pieceBitboard[colorToMove][currentAttacker] != Bitboard.EMPTY) {
                    nextVictim = currentAttacker
                    break
                }
            }

            // Remove one attacker from occupied bitboard
            fromSquare = Square.getSquare(colorAttackers and board.pieceBitboard[colorToMove][nextVictim])
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            occupied = occupied xor fromBitboard

            // Diagonal attacks may reveal discovered attacks from bishops and queens
            if (bishops != Bitboard.EMPTY &&
                (nextVictim == Piece.PAWN || nextVictim == Piece.BISHOP || nextVictim == Piece.QUEEN)) {
                // Remove possible bishop/queen piece
                bishops = bishops and fromBitboard.inv()
                // Update attackers if any bishop/queen attacker left
                if (bishops != Bitboard.EMPTY) {
                    attackers = attackers or (BitboardMove.bishopMoves(toSquare, occupied) and bishops)
                }
            }

            // A vertical or horizontal attack may reveal discovered attacks from rooks and queens
            if (rooks != Bitboard.EMPTY && (nextVictim == Piece.ROOK || nextVictim == Piece.QUEEN)) {
                // Remove possible rook/queen piece
                rooks = rooks and fromBitboard.inv()
                // Update attackers if any rook/queen attacker left
                if (rooks != Bitboard.EMPTY) {
                    attackers = attackers or (BitboardMove.rookMoves(toSquare, occupied) and rooks)
                }
            }

            // Remove attacker from possible attacker list
            attackers = attackers and occupied

            // Change color
            colorToMove = Color.invertColor(colorToMove)

            // Negamax the balance and add current attacker's (next victim) value
            balance = -balance - 1 - TunableConstants.SEE_VALUE[nextVictim]

            // If balance is non-negative after giving away nextVictim then we win.
            if (balance >= 0) {

                // No other piece to attack besides the king and the opponent still have attackers left.
                // This is illegal and should not be done.
                if (nextVictim == Piece.KING && (attackers and board.colorBitboard[colorToMove] != Bitboard.EMPTY)) {
                    colorToMove = Color.invertColor(colorToMove)
                }
                break
            }
        }

        // Side to move after the loop loses
        return board.colorToMove != colorToMove

    }

    private fun attackersToSquare(board: Board, occupied: Long, toSquare: Int, bishops: Long, rooks: Long): Long {
        var result = Bitboard.EMPTY
        if (bishops != Bitboard.EMPTY) {
            result = result or (BitboardMove.bishopMoves(toSquare, occupied) and bishops)
        }
        if (rooks != Bitboard.EMPTY) {
            result = result or (BitboardMove.rookMoves(toSquare, occupied) and rooks)
        }
        return result or
            (BitboardMove.PAWN_ATTACKS[Color.BLACK][toSquare] and board.pieceBitboard[Color.WHITE][Piece.PAWN]) or
            (BitboardMove.PAWN_ATTACKS[Color.WHITE][toSquare] and board.pieceBitboard[Color.BLACK][Piece.PAWN]) or
            (BitboardMove.KNIGHT_MOVES[toSquare] and
                (board.pieceBitboard[Color.WHITE][Piece.KNIGHT] or board.pieceBitboard[Color.BLACK][Piece.KNIGHT])) or
            (BitboardMove.KING_MOVES[toSquare] and
                (board.pieceBitboard[Color.WHITE][Piece.KING] or board.pieceBitboard[Color.BLACK][Piece.KING]))
    }
}