package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove

class BasicEvalInfo {

    var dangerBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }
        private set

    var checkBitboard = LongArray(Color.SIZE)
        private set

    var pinnersBitboard = LongArray(Color.SIZE)
        private set

    var pinnedBitboard = LongArray(Color.SIZE)
        private set

    var kingSquare = IntArray(Color.SIZE)
        private set

    fun update(board: Board) {
        for (ourColor in Color.WHITE until Color.SIZE) {
            val ourColorBitboard = board.colorBitboard[ourColor]
            val theirColor = Color.invertColor(ourColor)
            val theirColorBitboard = board.colorBitboard[theirColor]

            val kingSquarePosition = Square.getSquare(board.pieceBitboard[ourColor][Piece.KING])
            kingSquare[ourColor] = kingSquarePosition
            pinnedBitboard[ourColor] = 0L
            pinnersBitboard[theirColor] = 0L

            dangerBitboard[ourColor][Piece.PAWN] = BitboardMove.PAWN_ATTACKS[ourColor][kingSquarePosition]
            dangerBitboard[ourColor][Piece.KNIGHT] = BitboardMove.KNIGHT_MOVES[kingSquarePosition]
            dangerBitboard[ourColor][Piece.BISHOP] = BitboardMove.bishopMoves(kingSquarePosition, theirColorBitboard)
            dangerBitboard[ourColor][Piece.ROOK] = BitboardMove.rookMoves(kingSquarePosition, theirColorBitboard)
            dangerBitboard[ourColor][Piece.QUEEN] = dangerBitboard[ourColor][Piece.BISHOP] or dangerBitboard[ourColor][Piece.ROOK]

            for (piece in Piece.PAWN until Piece.SIZE) {
                val possibleCheck = dangerBitboard[ourColor][piece] and board.pieceBitboard[theirColor][piece]
                if (possibleCheck != 0L) {
                    if (piece == Piece.BISHOP || piece == Piece.ROOK || piece == Piece.QUEEN) {
                        val between = BitboardMove.BETWEEN_BITBOARD[Square.getSquare(possibleCheck)][kingSquarePosition]
                        if (between and ourColorBitboard == 0L) {
                            checkBitboard[ourColor] = checkBitboard[ourColor] or possibleCheck
                        } else if (Bitboard.oneElement(between and ourColorBitboard)) {
                            pinnedBitboard[ourColor] = pinnedBitboard[ourColor] or between
                            pinnersBitboard[theirColor] = pinnersBitboard[theirColor] or possibleCheck
                        }
                    } else {
                        checkBitboard[ourColor] = checkBitboard[ourColor] or possibleCheck
                    }
                }
            }
        }
    }
}