package pirarucu.board

import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import pirarucu.move.MoveType
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardTest {

    @Test
    fun testMove() {
        val board = BoardFactory.getBoard(BoardFactory.STARTER_FEN)
        val move = Move.createMove(Square.E2, Square.E3, Piece.PAWN)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.currentState.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.moveNumber, 1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.RANK_7 or Bitboard.RANK_8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.RANK_1 or Bitboard.RANK_2 xor Bitboard.E2 xor Bitboard.E3)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPassantMove() {
        val board = BoardFactory.getBoard(BoardFactory.STARTER_FEN)
        val move = Move.createMove(Square.E2, Square.E4, Piece.PAWN)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.currentState.epSquare, Square.E3)
        assertEquals(board.currentState.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.moveNumber, 1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.RANK_7 or Bitboard.RANK_8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.RANK_1 or Bitboard.RANK_2 xor Bitboard.E2 xor Bitboard.E4)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCaptureMove() {
        val board = BoardFactory.getBoard("4k3/8/8/3p4/4P3/8/8/4K3 w - -")
        val move = Move.createMove(Square.E4, Square.D5, Piece.PAWN, Piece.PAWN)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.D5)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.D5)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPromotionMove() {
        val board = BoardFactory.getBoard("4k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionMove(Square.C7, Square.C8, MoveType.TYPE_PROMOTION_QUEEN)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.C8)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.QUEEN], Bitboard.C8)
        assertEquals(board.pieceTypeBoard[Square.C7], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.C8], Piece.QUEEN)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPromotionAttack() {
        val board = BoardFactory.getBoard("1b2k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionAttack(Square.C7, Square.B8, Piece.BISHOP, MoveType.TYPE_PROMOTION_ROOK)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.B8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.BISHOP], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.B8)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCastling() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K2R w K -")
        val move = Move.createCastlingMove(Square.E1, Square.G1)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.F1 or Bitboard.G1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.G1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.F1)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCheck() {
        val board = BoardFactory.getBoard("6k1/8/8/8/8/8/8/4K2R w - -")
        val move = Move.createMove(Square.H1, Square.G1, Piece.ROOK)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.basicEvalInfo.checkBitboard[Color.BLACK], Bitboard.G1)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndo() {
        val board = BoardFactory.getBoard("6k1/8/8/8/8/8/8/4K2R w - -")
        val move = Move.createMove(Square.H1, Square.G1, Piece.ROOK)
        board.doMove(move)
        board.undoMove()
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.G8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.H1)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.KING], Bitboard.G8)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.E1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.H1)
        assertEquals(board.colorToMove, Color.WHITE)
        BoardTestUtil.testBoard(board)
    }
}

