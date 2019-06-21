package pirarucu.move

import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoveTest {

    @Test
    fun testFrom() {
        assertEquals(Move.getFromSquare(Move.createMove(Square.E2, Square.E4, Piece.PAWN)),
            Square.E2)
    }

    @Test
    fun testTo() {
        assertEquals(Move.getToSquare(Move.createMove(Square.E2, Square.E4, Piece.PAWN)), Square.E4)
    }

    @Test
    fun testMoveType() {
        assertEquals(Move.getMoveType(Move.createMove(Square.E4, Square.F3, MoveType.TYPE_PASSANT)),
            MoveType.TYPE_PASSANT)
    }

    @Test
    fun testGetMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3Pp3/8/8/8/4K3 w - e6")
        assertEquals(Move.getMove(board, "d5e6"),
            Move.createMove(Square.D5, Square.E6, MoveType.TYPE_PASSANT))
    }

    @Test
    fun testMoveCompatiblePawnMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3Pp3/8/8/8/4K3 w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createMove(Square.D5, Square.D6), "d6"))
    }

    @Test
    fun testMoveCompatiblePawnCapture() {
        val board = BoardFactory.getBoard("rqr3k1/4bpp1/pBbp1n1p/Pp2p3/3PPP2/2N1QNP1/1P4P1/R4R1K w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createMove(Square.F4, Square.E5), "fxe5"))
    }

    @Test
    fun testMoveCompatiblePawnCapture2() {
        val board = BoardFactory.getBoard("r1qr2k1/4bp1p/2p1p1p1/2pb4/PP1P1P2/3NQ3/5BPP/R2R2K1 w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createMove(Square.B4, Square.C5), "bxc5"))
    }

    @Test
    fun testMoveCompatibleKnightMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3Np3/8/8/8/4K3 w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createMove(Square.D5, Square.C7), "Nc7"))
    }

    @Test
    fun testMoveCompatibleCastling() {
        val board = BoardFactory.getBoard("5k2/8/8/3Np3/8/8/8/4K3 w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createCastlingMove(Square.E1, Square.G1), "O-O"))
    }

    @Test
    fun testMoveCompatibleKnightAmbiguity() {
        val board = BoardFactory.getBoard("5k2/8/8/1N1Np3/8/8/8/4K3 w - -")
        assertTrue(Move.areMovesCompatibles(board, Move.createMove(Square.D5, Square.C7), "Ndc7"))
    }
}