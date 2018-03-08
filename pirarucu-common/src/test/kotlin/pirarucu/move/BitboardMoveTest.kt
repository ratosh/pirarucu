package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Color
import pirarucu.board.Square
import pirarucu.util.Utils
import kotlin.test.Test
import kotlin.test.assertEquals

class BitboardMoveTest {

    @Test
    fun testPawnMove() {
        assertEquals(BitboardMove.PAWN_MOVES[Color.WHITE][Square.A2], Bitboard.A3)
        assertEquals(BitboardMove.PAWN_MOVES[Color.WHITE][Square.A3], Bitboard.A4)
        assertEquals(BitboardMove.PAWN_MOVES[Color.BLACK][Square.A1], 0)
        assertEquals(BitboardMove.PAWN_MOVES[Color.BLACK][Square.A2], Bitboard.A1)
        assertEquals(BitboardMove.PAWN_MOVES[Color.BLACK][Square.A7], Bitboard.A6)
        assertEquals(BitboardMove.DOUBLE_PAWN_MOVES[Color.WHITE][Square.A2], Bitboard.A4)
        assertEquals(BitboardMove.DOUBLE_PAWN_MOVES[Color.WHITE][Square.A3], 0)
        assertEquals(BitboardMove.DOUBLE_PAWN_MOVES[Color.BLACK][Square.A1], 0)
        assertEquals(BitboardMove.DOUBLE_PAWN_MOVES[Color.BLACK][Square.A2], 0)
        assertEquals(BitboardMove.DOUBLE_PAWN_MOVES[Color.BLACK][Square.A7], Bitboard.A5)
    }

    @Test
    fun testPawnAttack() {
        assertEquals(BitboardMove.PAWN_ATTACKS[Color.WHITE][Square.A2], Bitboard.B3)
        assertEquals(BitboardMove.PAWN_ATTACKS[Color.WHITE][Square.B3], Bitboard.A4 or Bitboard.C4)
        assertEquals(BitboardMove.PAWN_ATTACKS[Color.BLACK][Square.A2], Bitboard.B1)
        assertEquals(BitboardMove.PAWN_ATTACKS[Color.BLACK][Square.B3], Bitboard.A2 or Bitboard.C2)
    }

    @Test
    fun testKnightMoves() {
        assertEquals(BitboardMove.KNIGHT_MOVES[Square.A1], Bitboard.B3 or Bitboard.C2)
        assertEquals(BitboardMove.KNIGHT_MOVES[Square.E5], Bitboard.C4 or Bitboard.D3 or
            Bitboard.F3 or Bitboard.G4 or
            Bitboard.G6 or Bitboard.F7 or
            Bitboard.D7 or Bitboard.C6)
        assertEquals(BitboardMove.KNIGHT_MOVES[Square.H8], Bitboard.G6 or Bitboard.F7)
    }

    @Test
    fun testBishopMoves() {
        assertEquals(BitboardMove.bishopMoves(Square.A1, 0L), Bitboard.B2 or Bitboard.C3 or
            Bitboard.D4 or Bitboard.E5 or Bitboard.F6 or Bitboard.G7 or Bitboard.H8)
        assertEquals(BitboardMove.bishopMoves(Square.E5, 0L),
            Bitboard.A1 or Bitboard.B2 or Bitboard.C3 or Bitboard.D4 or
                Bitboard.F6 or Bitboard.G7 or Bitboard.H8 or
                Bitboard.H2 or Bitboard.G3 or Bitboard.F4 or
                Bitboard.D6 or Bitboard.C7 or Bitboard.B8)
        assertEquals(BitboardMove.bishopMoves(Square.H8, 0L), Bitboard.A1 or
            Bitboard.B2 or Bitboard.C3 or Bitboard.D4 or Bitboard.E5 or Bitboard.F6 or Bitboard.G7)
    }

    @Test
    fun testRookMoves() {
        assertEquals(BitboardMove.rookMoves(Square.A1, 0L), Bitboard.B1 or Bitboard.C1 or
            Bitboard.D1 or Bitboard.E1 or Bitboard.F1 or Bitboard.G1 or Bitboard.H1 or Bitboard.A2 or
            Bitboard.A3 or Bitboard.A4 or Bitboard.A5 or Bitboard.A6 or Bitboard.A7 or Bitboard.A8)
        assertEquals(BitboardMove.rookMoves(Square.E5, 0L), Bitboard.A5 or Bitboard.B5 or
            Bitboard.C5 or Bitboard.D5 or Bitboard.F5 or Bitboard.G5 or Bitboard.H5 or Bitboard.E1 or
            Bitboard.E2 or Bitboard.E3 or Bitboard.E4 or Bitboard.E6 or Bitboard.E7 or Bitboard.E8)
        assertEquals(BitboardMove.rookMoves(Square.H8, 0L), Bitboard.A8 or Bitboard.B8 or
            Bitboard.C8 or Bitboard.D8 or Bitboard.E8 or Bitboard.F8 or Bitboard.G8 or Bitboard.H1 or
            Bitboard.H2 or Bitboard.H3 or Bitboard.H4 or Bitboard.H5 or Bitboard.H6 or Bitboard.H7)
    }

    @Test
    fun testQueenMoves() {
        assertEquals(Utils.specific.bitCount(BitboardMove.queenMoves(Square.A1, 0L)), 21)
        assertEquals(Utils.specific.bitCount(BitboardMove.queenMoves(Square.E5, 0L)), 27)
        assertEquals(Utils.specific.bitCount(BitboardMove.queenMoves(Square.H8, 0L)), 21)
    }

    @Test
    fun testKingMoves() {
        assertEquals(BitboardMove.KING_MOVES[Square.A1], Bitboard.A2 or Bitboard.B2 or Bitboard.B1)
        assertEquals(BitboardMove.KING_MOVES[Square.E5],
            Bitboard.D4 or Bitboard.D5 or Bitboard.D6 or
                Bitboard.E4 or Bitboard.E6 or
                Bitboard.F4 or Bitboard.F5 or Bitboard.F6)
        assertEquals(BitboardMove.KING_MOVES[Square.H8], Bitboard.G7 or Bitboard.G8 or Bitboard.H7)
    }

    @Test
    fun testBetween() {
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.H8], Bitboard.B2 or
            Bitboard.C3 or Bitboard.D4 or Bitboard.E5 or Bitboard.F6 or Bitboard.G7)
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.H8][Square.A1],
            BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.H8])
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.A8], Bitboard.A2 or
            Bitboard.A3 or Bitboard.A4 or Bitboard.A5 or Bitboard.A6 or Bitboard.A7)
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.A8],
            BitboardMove.BETWEEN_BITBOARD[Square.A8][Square.A1])
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.B8], 0)
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.H1], Bitboard.B1 or
            Bitboard.C1 or Bitboard.D1 or Bitboard.E1 or Bitboard.F1 or Bitboard.G1)
        assertEquals(BitboardMove.BETWEEN_BITBOARD[Square.A1][Square.H1],
            BitboardMove.BETWEEN_BITBOARD[Square.H1][Square.A1])
    }

    @Test
    fun testPinnedMask() {
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.H8], Bitboard.B2 or
            Bitboard.C3 or Bitboard.D4 or Bitboard.E5 or Bitboard.F6 or Bitboard.G7 or Bitboard.H8)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.H8] xor Bitboard.H8,
            BitboardMove.PINNED_MOVE_MASK[Square.H8][Square.A1] xor Bitboard.A1)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.A8], Bitboard.A2 or
            Bitboard.A3 or Bitboard.A4 or Bitboard.A5 or Bitboard.A6 or Bitboard.A7 or Bitboard.A8)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.A8] xor Bitboard.A8,
            BitboardMove.PINNED_MOVE_MASK[Square.A8][Square.A1] xor Bitboard.A1)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.B8], 0)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.H1], Bitboard.B1 or
            Bitboard.C1 or Bitboard.D1 or Bitboard.E1 or Bitboard.F1 or Bitboard.G1 or Bitboard.H1)
        assertEquals(BitboardMove.PINNED_MOVE_MASK[Square.A1][Square.H1] xor Bitboard.H1,
            BitboardMove.PINNED_MOVE_MASK[Square.H1][Square.A1] xor Bitboard.A1)
    }
}