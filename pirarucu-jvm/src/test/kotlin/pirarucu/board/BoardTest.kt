package pirarucu.board

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import pirarucu.move.MoveType
import pirarucu.move.OrderedMoveList
import pirarucu.search.History
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BoardTest {

    private val moveGenerator = MoveGenerator(History())

    @Test
    fun testMove() {
        val board = BoardFactory.getBoard(BoardFactory.STARTER_FEN)
        val move = Move.createMove(Square.E2, Square.E3)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.castlingRights, CastlingRights.ANY_CASTLING)
        assertEquals(board.moveNumber, 1)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.RANK_7 or Bitboard.RANK_8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.RANK_1 or Bitboard.RANK_2 xor
            Bitboard.E2 xor Bitboard.E3)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPassantMove() {
        val board = BoardFactory.getBoard("8/8/1K6/8/7p/4k3/6P1/8 w - -")
        val move = Move.createMove(Square.G2, Square.G4)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.epSquare, Square.G3)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPassantCapture() {
        val board = BoardFactory.getBoard("8/8/1K6/8/6Pp/4k3/8/8 b - g3")
        val move = Move.createMove(Square.H4, Square.G3, MoveType.TYPE_PASSANT)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCaptureMove() {
        val board = BoardFactory.getBoard("4k3/8/8/3p4/4P3/8/8/4K3 w - -")
        val move = Move.createMove(Square.E4, Square.D5)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.D5)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.D5)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPromotionMove() {
        val board = BoardFactory.getBoard("4k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionMove(Square.C7, Square.C8, MoveType.TYPE_PROMOTION_QUEEN)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.C8)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.QUEEN], Bitboard.C8)
        assertEquals(board.pieceTypeBoard[Square.C7], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.C8], Piece.QUEEN)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testPromotionAttack() {
        val board = BoardFactory.getBoard("1b2k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionMove(Square.C7, Square.B8, MoveType.TYPE_PROMOTION_ROOK)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.B8)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.BISHOP], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], 0)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.ROOK], Bitboard.B8)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCastling() {
        val board = BoardFactory.getBoard("r3k2r/8/8/8/8/8/8/R3K2R b kq -")
        val move = Move.createCastlingMove(Square.E8, Square.G8)
        val zobristKey = board.zobristKey
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
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testCheck() {
        val board = BoardFactory.getBoard("6k1/8/8/8/8/8/8/4K2R w - -")
        val move = Move.createMove(Square.H1, Square.G1)
        val zobristKey = board.zobristKey
        board.doMove(move)
        assertEquals(board.colorToMove, Color.BLACK)
        assertEquals(board.basicEvalInfo.checkBitboard, Bitboard.G1)
        assertNotEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoPawn() {
        val board = BoardFactory.getBoard("6k1/8/8/8/8/8/7P/4K3 w - -")
        val move = Move.createMove(Square.H2, Square.H4)
        val zobristKey = board.zobristKey
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.G8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.H2)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.KING], Bitboard.G8)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.KING], Bitboard.E1)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.H2)
        assertEquals(board.pieceTypeBoard[Square.H4], Piece.NONE)
        assertEquals(board.pieceTypeBoard[Square.H2], Piece.PAWN)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoRook() {
        val board = BoardFactory.getBoard("6k1/8/8/8/8/8/8/4K2R w - -")
        val move = Move.createMove(Square.H1, Square.G1)
        val zobristKey = board.zobristKey
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
        assertEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoPromotion() {
        val board = BoardFactory.getBoard("4k3/2P5/8/8/8/8/8/4K3 w - -")
        val move = Move.createPromotionMove(Square.C7, Square.C8, MoveType.TYPE_PROMOTION_QUEEN)
        val zobristKey = board.zobristKey
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.C7)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.C7)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.QUEEN], 0)
        assertEquals(board.pieceTypeBoard[Square.C7], Piece.PAWN)
        assertEquals(board.pieceTypeBoard[Square.C8], Piece.NONE)
        assertEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoCapture() {
        val board = BoardFactory.getBoard("4k3/8/4p3/3P4/8/8/8/4K3 w - -")
        val move = Move.createMove(Square.D5, Square.E6)
        val zobristKey = board.zobristKey
        board.doMove(move)
        board.undoMove(move)
        assertEquals(board.colorToMove, Color.WHITE)
        assertEquals(board.colorBitboard[Color.WHITE], Bitboard.E1 or Bitboard.D5)
        assertEquals(board.pieceBitboard[Color.WHITE][Piece.PAWN], Bitboard.D5)
        assertEquals(board.colorBitboard[Color.BLACK], Bitboard.E8 or Bitboard.E6)
        assertEquals(board.pieceBitboard[Color.BLACK][Piece.PAWN], Bitboard.E6)
        assertEquals(board.pieceTypeBoard[Square.D5], Piece.PAWN)
        assertEquals(board.pieceTypeBoard[Square.E6], Piece.PAWN)
        assertEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoCastling() {
        val board = BoardFactory.getBoard("r3k2r/8/8/8/8/8/8/R3K2R b kq -")
        val move = Move.createCastlingMove(Square.E8, Square.G8)
        val zobristKey = board.zobristKey
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
        assertEquals(zobristKey, board.zobristKey)
        BoardTestUtil.testBoard(board)
    }

    @Test
    fun testUndoPosition1() {
        val board = BoardFactory
            .getBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -")
        var zobristKey = board.zobristKey
        board.doMove(Move.createMove(Square.A2, Square.A4))
        assertNotEquals(zobristKey, board.zobristKey)
        val moveList = OrderedMoveList()
        moveGenerator.legalAttacks(board, AttackInfo(), moveList)
        while (moveList.hasNext()) {
            val move = moveList.next()
            zobristKey = board.zobristKey
            board.doMove(move)
            assertNotEquals(zobristKey, board.zobristKey)
            board.undoMove(move)
            assertEquals(zobristKey, board.zobristKey)
        }
    }

    @Test
    fun testHasNonPawnMaterial() {
        val board = BoardFactory.getBoard("8/7R/1K6/8/7p/4k3/6P1/8 w - -")
        assertFalse(board.hasNonPawnMaterial(Color.BLACK))
        assertTrue(board.hasNonPawnMaterial(Color.WHITE))
    }
}

