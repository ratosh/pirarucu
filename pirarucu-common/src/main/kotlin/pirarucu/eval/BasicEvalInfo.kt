package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove

class BasicEvalInfo {
    private val dangerBitboard = LongArray(Piece.SIZE)

    var checkBitboard = LongArray(Color.SIZE)
        private set

    var pinnerType = IntArray(Square.SIZE)
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

            val kingSquarePosition =
                Square.getSquare(board.pieceBitboard[ourColor][Piece.KING])
            kingSquare[ourColor] = kingSquarePosition
            pinnedBitboard[ourColor] = Bitboard.EMPTY
            pinnerType[theirColor] = Piece.NONE
            checkBitboard[ourColor] = Bitboard.EMPTY

            dangerBitboard[Piece.PAWN] = BitboardMove.PAWN_ATTACKS[ourColor][kingSquarePosition]
            dangerBitboard[Piece.KNIGHT] = BitboardMove.KNIGHT_MOVES[kingSquarePosition]
            dangerBitboard[Piece.BISHOP] = BitboardMove.bishopMoves(kingSquarePosition,
                theirColorBitboard)
            dangerBitboard[Piece.ROOK] = BitboardMove.rookMoves(kingSquarePosition,
                theirColorBitboard)
            dangerBitboard[Piece.QUEEN] = dangerBitboard[Piece.BISHOP] or dangerBitboard[Piece.ROOK]

            for (piece in Piece.PAWN until Piece.KING) {
                var possibleCheck = dangerBitboard[piece] and
                    board.pieceBitboard[theirColor][piece]
                while (possibleCheck != Bitboard.EMPTY) {
                    val checkSquare = Square.getSquare(possibleCheck)
                    val bitboard = Bitboard.getBitboard(checkSquare)
                    if (piece == Piece.BISHOP || piece == Piece.ROOK || piece == Piece.QUEEN) {
                        val between =
                            BitboardMove.BETWEEN_BITBOARD[checkSquare][kingSquarePosition] and
                                ourColorBitboard
                        if (between == Bitboard.EMPTY) {
                            checkBitboard[ourColor] = checkBitboard[ourColor] or bitboard
                        } else if (Bitboard.oneElement(between)) {
                            pinnedBitboard[ourColor] = pinnedBitboard[ourColor] or between
                            pinnerType[Square.getSquare(between)] = piece
                        }
                    } else {
                        checkBitboard[ourColor] = checkBitboard[ourColor] or bitboard
                    }
                    possibleCheck = possibleCheck and possibleCheck - 1
                }
            }
        }
    }
}
