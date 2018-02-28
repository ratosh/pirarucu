package pirarucu.move

import pirarucu.board.Piece
import pirarucu.board.Square
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTest {

    @Test
    fun testFrom() {
        assertEquals(Move.getFromSquare(Move.createMove(Square.E2, Square.E4, Piece.PAWN)), Square.E2)
    }

    @Test
    fun testTo() {
        assertEquals(Move.getToSquare(Move.createMove(Square.E2, Square.E4, Piece.PAWN)), Square.E4)
    }

    @Test
    fun testMovedPiece() {
        assertEquals(Move.getMovedPieceType(Move.createMove(Square.E2, Square.E4, Piece.PAWN)), Piece.PAWN)
    }

    @Test
    fun testAttackedPiece() {
        assertEquals(Move.getAttackedPieceType(Move.createMove(Square.E2, Square.E4, Piece.PAWN, Piece.BISHOP)), Piece.BISHOP)
    }

    @Test
    fun testMoveType() {
        assertEquals(Move.getMoveType(Move.createMove(Square.E2, Square.E4, Piece.PAWN, Piece.BISHOP, MoveType.TYPE_PASSANT)), MoveType.TYPE_PASSANT)
    }
}