package pirarucu.board.factory

import pirarucu.board.Bitboard
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardFactoryTest {

    @Test
    fun testGetBoard() {
        val board = BoardFactory.getBoard(BoardFactory.STARTER_FEN)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.NONE], Bitboard.RANK_1 or Bitboard.RANK_2)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.NONE], Bitboard.RANK_7 or Bitboard.RANK_8)

        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.RANK_2)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.PAWN], Bitboard.RANK_7)

        assertEquals(board.pieceTypeBoard[Square.E1], Piece.KING)

        assertEquals(board.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.epSquare, Square.NONE)

        assertEquals(board.colorToMove, Color.WHITE)
    }
}

