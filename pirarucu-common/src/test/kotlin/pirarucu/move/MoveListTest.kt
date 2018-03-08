package pirarucu.move

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveListTest {

    @Test
    fun testAddNext() {
        val moveList = MoveList()
        val move = Move.createMove(1, 1, 3)
        moveList.addMove(move)
        assertEquals(moveList.next(), move)
    }

    @Test
    fun testCurrentPly() {
        val moveList = MoveList()
        assertEquals(moveList.currentPly, 0)
        moveList.startPly()
        assertEquals(moveList.currentPly, 1)
        moveList.endPly()
        assertEquals(moveList.currentPly, 0)
    }

    @Test
    fun testHasNext() {
        val moveList = MoveList()
        assertFalse(moveList.hasNext())
        val move = Move.createMove(1, 1, 3)
        moveList.addMove(move)
        assertTrue(moveList.hasNext())
        moveList.startPly()
        assertFalse(moveList.hasNext())
        moveList.endPly()
        assertTrue(moveList.hasNext())
    }

    @Test
    fun testMovesLeft() {
        val moveList = MoveList()
        assertEquals(moveList.movesLeft(), 0)
        val move = Move.createMove(1, 1, 3)
        moveList.addMove(move)
        assertEquals(moveList.movesLeft(), 1)
        moveList.startPly()
        assertEquals(moveList.movesLeft(), 0)
        moveList.endPly()
        assertEquals(moveList.movesLeft(), 1)
    }

    @Test
    fun testSkipMoves() {
        val moveList = MoveList()
        assertEquals(moveList.movesLeft(), 0)
        val move = Move.createMove(1, 1, 3)
        moveList.startPly()
        assertEquals(moveList.movesLeft(), 0)
        moveList.skipMoves()
        assertEquals(moveList.movesLeft(), 0)
        moveList.addMove(move)
        assertEquals(moveList.movesLeft(), 1)
        moveList.startPly()
        assertEquals(moveList.movesLeft(), 0)
        moveList.endPly()
        assertEquals(moveList.movesLeft(), 1)
        moveList.skipMoves()
        assertEquals(moveList.movesLeft(), 0)
    }

    @Test
    fun testSort() {
        val moveList = MoveList()
        assertEquals(moveList.movesLeft(), 0)
        val move1 = Move.createMove(2, 3, 4)
        val move2 = Move.createMove(1, 2, 3)
        val move3 = Move.createMove(0, 1, 2)
        moveList.addMove(move1)
        moveList.addMove(move2)
        moveList.sort()
        moveList.startPly()
        moveList.addMove(move3)
        moveList.sort()
        assertEquals(moveList.next(), move3)
        moveList.endPly()
        assertEquals(moveList.next(), move2)
        assertEquals(moveList.next(), move1)
    }
}