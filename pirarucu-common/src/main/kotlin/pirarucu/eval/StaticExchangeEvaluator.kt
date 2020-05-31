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

object StaticExchangeEvaluator {

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
            if (nextVictim == Piece.PAWN || nextVictim == Piece.BISHOP || nextVictim == Piece.QUEEN) {
                // Remove possible bishop/queen piece
                bishops = bishops and fromBitboard.inv()
                // Update attackers if any bishop/queen attacker left
                if (bishops != Bitboard.EMPTY) {
                    attackers = attackers or (BitboardMove.bishopMoves(toSquare, occupied) and bishops)
                }
            }

            // A vertical or horizontal attack may reveal discovered attacks from rooks and queens
            if (nextVictim == Piece.ROOK || nextVictim == Piece.QUEEN) {
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
        val toBitboard = Bitboard.getBitboard(toSquare)
        return result or
            (BitboardMove.pawnAttacks(Color.BLACK, toBitboard) and board.pieceBitboard[Color.WHITE][Piece.PAWN]) or
            (BitboardMove.pawnAttacks(Color.WHITE, toBitboard) and board.pieceBitboard[Color.BLACK][Piece.PAWN]) or
            (BitboardMove.KNIGHT_MOVES[toSquare] and
                (board.pieceBitboard[Color.WHITE][Piece.KNIGHT] or board.pieceBitboard[Color.BLACK][Piece.KNIGHT])) or
            (BitboardMove.KING_MOVES[toSquare] and
                (board.pieceBitboard[Color.WHITE][Piece.KING] or board.pieceBitboard[Color.BLACK][Piece.KING]))
    }
}