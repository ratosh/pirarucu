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