package pirarucu.move

import pirarucu.board.factory.BoardFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveGeneratorTest {

    @Test
    fun testPawnMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3P4/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 6)
    }

    @Test
    fun testPawnDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/3P4/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 7)
    }

    @Test
    fun testPromotion() {
        val board = BoardFactory.getBoard("5k2/2P5/8/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 9)
    }

    @Test
    fun testPawnBlockedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3p4/3P4/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 5)
    }

    @Test
    fun testPawnBlockedDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/3p4/3P4/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 4)
    }

    @Test
    fun testPawnBlockedDoubleMove2() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3p4/8/3P4/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 6)
    }

    @Test
    fun testKnightMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/2N5/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 13)
    }

    @Test
    fun testKnightPinned() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6N1/7K w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 2)
    }

    @Test
    fun testBishopMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3B4/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 18)
    }

    @Test
    fun testBishopPinnedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6B1/7K w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 4)
    }

    @Test
    fun testRookMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3R4/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 19)
    }

    @Test
    fun testRookPinnedMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5R2/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 8)
    }

    @Test
    fun testQueenMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3Q4/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 32)
    }

    @Test
    fun testQueenPinnedMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5Q2/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 8)
    }

    @Test
    fun testKingMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 5)
    }

    @Test
    fun testKingSafeMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 4)
    }

    @Test
    fun testCastlingMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/R3K3 w Q -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 16)
    }

    @Test
    fun testCastlingMoveInCheck() {
        val board = BoardFactory.getBoard("3r1k2/8/8/8/8/8/8/R3K3 w Q -")
        val moveList = MoveList()
        MoveGenerator.legalMoves(board, moveList)
        assertEquals(moveList.movesLeft(), 13)
    }

    @Test
    fun testEpCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/3Pp3/8/8/8/4K3 w - e6")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testEpCapturePinned() {
        val board = BoardFactory.getBoard("5kb1/8/8/3Pp3/2K5/8/8/8 w - e6")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testNoEpCapturePinned() {
        val board = BoardFactory.getBoard("2r2k2/8/8/2P1p3/2K5/8/8/8 w - e6")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 0)
    }

    @Test
    fun testCapturePromotion() {
        val board = BoardFactory.getBoard("3r1k2/2P5/8/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 4)
    }

    @Test
    fun testKnightCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/2N5/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testBishopCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/1B6/8/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testRookCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/8/3R4/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testQueenCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/8/3Q4/8/8/8/5K2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testKingCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/4qK2 w - -")
        val moveList = MoveList()
        MoveGenerator.legalAttacks(board, moveList)
        assertEquals(moveList.movesLeft(), 1)
    }
}