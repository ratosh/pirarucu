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
        assertEquals(board.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.moveNumber, 1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.RANK_7 or Bitboard.RANK_8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.RANK_1 or Bitboard.RANK_2 xor
            Bitboard.E2 xor Bitboard.E3)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPassantMove() {
        val board = BoardFactory.getBoard(BoardFactory.STARTER_FEN)
        val move = Move.createMove(Square.E2, Square.E4, Piece.PAWN)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.epSquare, Square.E3)
        assertEquals(board.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.moveNumber, 1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.RANK_7 or Bitboard.RANK_8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.RANK_1 or Bitboard.RANK_2 xor
            Bitboard.E2 xor Bitboard.E4)
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
        val board = BoardFactory.getBoard("r3k2r/8/8/8/8/8/8/R3K2R b kq -")
        val move = Move.createCastlingMove(Square.E8, Square.G8)
        board.doMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.A1 or Bitboard.E1 or Bitboard.H1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.E1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.A1 or Bitboard.H1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.A8 or Bitboard.F8 or Bitboard.G8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.KING], Bitboard.G8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.ROOK], Bitboard.A8 or Bitboard.F8)
        assertEquals(board.pieceTypeBoard[Square.E8], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.F8], Piece.ROOK)
        assertEquals(board.pieceTypeBoard[Square.G8], Piece.KING)
        assertEquals(board.pieceTypeBoard[Square.H8], Piece.NONE)
        assertEquals(board.castlingRights, 0)
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
        board.undoMove(move)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.G8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.H1)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.KING], Bitboard.G8)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.E1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.H1)
        assertEquals(board.pieceTypeBoard[Square.G1], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.H1], Piece.ROOK)
        assertEquals(board.colorToMove, Color.WHITE)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoPromotion() {
        val board = BoardFactory.getBoard("4k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionMove(Square.C7, Square.C8, MoveType.TYPE_PROMOTION_QUEEN)
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.C7)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.C7)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.QUEEN], 0)
        assertEquals(board.pieceTypeBoard[Square.C7], Piece.PAWN)
        assertEquals(board.pieceTypeBoard[Square.C8], Piece.NONE)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoCapture() {
        val board = BoardFactory.getBoard("4k3/8/4p3/3P4/8/8/8/4K3 w - -")
        val move = Move.createAttackMove(Square.D5, Square.E6, Piece.PAWN, Piece.PAWN)
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.D5)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.D5)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8 or Bitboard.E6)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.PAWN], Bitboard.E6)
        assertEquals(board.pieceTypeBoard[Square.D5], Piece.PAWN)
        assertEquals(board.pieceTypeBoard[Square.E6], Piece.PAWN)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoCastling() {
        val board = BoardFactory.getBoard("r3k2r/8/8/8/8/8/8/R3K2R b kq -")
        val move = Move.createCastlingMove(Square.E8, Square.G8)
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.A1 or Bitboard.E1 or Bitboard.H1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.E1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.A1 or Bitboard.H1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.A8 or Bitboard.E8 or Bitboard.H8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.KING], Bitboard.E8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.ROOK], Bitboard.A8 or Bitboard.H8)

        assertEquals(board.pieceTypeBoard[Square.E8], Piece.KING)
        assertEquals(board.pieceTypeBoard[Square.H8], Piece.ROOK)
        assertEquals(board.pieceTypeBoard[Square.G8], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.F8], Piece.NONE)
        assertEquals(board.castlingRights, CastlingRights.BLACK_CASTLING_RIGHTS)
        BoardTestUtil.testBoard(board)
    }
}

