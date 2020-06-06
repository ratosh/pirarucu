package pirarucu.hash

import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.board.File
import pirarucu.board.Rank
import pirarucu.util.XorShiftRandom

object Zobrist {

    val PIECE_SQUARE_TABLE = Array(Color.SIZE) { Array(Piece.SIZE) { LongArray(Square.SIZE) } }
    val PASSANT_SQUARE = LongArray(Square.SIZE + 1)
    val CASTLING_RIGHT = LongArray(CastlingRights.SIZE)
    val SIDE: Long

    init {

        for (color in 0 until Color.SIZE) {
            for (piece in 1 until Piece.SIZE) {
                for (square in 0 until Square.SIZE) {
                    PIECE_SQUARE_TABLE[color][piece][square] = XorShiftRandom.nextLong()
                }
            }
        }

        for (file in 0 until File.SIZE) {
            val random = XorShiftRandom.nextLong()
            for (rank in 0 until Rank.SIZE) {
                val square = Square.getSquare(file, rank)
                PASSANT_SQUARE[square] = random
            }
        }

        // skip first item: contains only zeros, default value and has no effect when xorring
        for (castlingRight in 1 until CastlingRights.SIZE) {
            CASTLING_RIGHT[castlingRight] = XorShiftRandom.nextLong()
        }

        SIDE = XorShiftRandom.nextLong()
    }
}
