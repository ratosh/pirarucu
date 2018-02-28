package pirarucu.hash

import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.util.Utils

object Zobrist {

    val PIECE_SQUARE_TABLE = Array(Color.SIZE) { Array(Piece.SIZE) { LongArray(Square.SIZE) } }
    val PASSANT_FILE = LongArray(File.SIZE)
    val CASTLING_RIGHT = LongArray(CastlingRights.SIZE)
    val SIDE: Long

    init {

        for (color in 0 until Color.SIZE) {
            for (piece in 0 until Piece.SIZE) {
                for (square in 0 until Square.SIZE) {
                    PIECE_SQUARE_TABLE[color][piece][square] = Utils.specific.randomLong()
                }
            }
        }
        for (file in 0 until File.SIZE) {
            PASSANT_FILE[file] = Utils.specific.randomLong()
        }

        // skip first item: contains only zeros, default value and has no effect when xorring
        for (castlingRight in 0 until CastlingRights.SIZE) {
            CASTLING_RIGHT[castlingRight] = Utils.specific.randomLong()
        }

        SIDE = Utils.specific.randomLong()
    }
}
