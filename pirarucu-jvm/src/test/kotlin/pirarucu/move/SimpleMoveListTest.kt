package pirarucu.move

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleMoveListTest {

    @Test
    fun testAddMove() {
        val moveList = SimpleMoveList()
        val move = Move.createMove(1, 1, 3)
        moveList.addMove(move)
        assertEquals(moveList.next(), move)
    }

    @Test
    fun testHasNext() {
        val moveList = SimpleMoveList()
        assertFalse(moveList.hasNext())
        val move = Move.createMove(1, 1, 3)
        moveList.addMove(move)
        assertTrue(moveList.hasNext())
        assertEquals(move, moveList.next())
        assertFalse(moveList.hasNext())
    }
}