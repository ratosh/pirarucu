package pirarucu.hash

import org.junit.Before
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.move.Move
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TranspositionTableTest {
    @Before
    fun setup() {
        TranspositionTable.reset()
    }

    @Test
    fun testEmptyTable() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        assertFalse(TranspositionTable.findEntry(board))
    }

    @Test
    fun testSave() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 3, 5, HashConstants.SCORE_TYPE_EXACT_SCORE, 7, 11)
        TranspositionTable.findEntry(board)
        assertEquals(3, TranspositionTable.eval)
        assertEquals(5, TranspositionTable.getScore(0))
        assertEquals(7, TranspositionTable.depth)
        assertEquals(11, TranspositionTable.firstMove)
    }

    @Test
    fun testSaveMateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 3, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0)
        TranspositionTable.findEntry(board)
        assertEquals(EvalConstants.SCORE_MAX, TranspositionTable.getScore(0))
        assertEquals(EvalConstants.SCORE_MAX - 1, TranspositionTable.getScore(1))
    }

    @Test
    fun testUpdateEval() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 3, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0)
        TranspositionTable.save(board, 5, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0)
        assertTrue(TranspositionTable.findEntry(board))
        assertEquals(5, TranspositionTable.eval)
    }

    @Test
    fun testUpdateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, 3, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0)
        TranspositionTable.save(board, 0, 5, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0)
        assertTrue(TranspositionTable.findEntry(board))
        assertEquals(5, TranspositionTable.getScore(0))
    }

    @Test
    fun testUpdateDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 1, 0)
        TranspositionTable.save(board, 0, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 2, 0)
        assertTrue(TranspositionTable.findEntry(board))
        assertEquals(2, TranspositionTable.depth)
    }

    @Test
    fun testUpdateMove() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, Move.NULL)
        TranspositionTable.save(board, 0, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, Move.NULL)
        TranspositionTable.save(board, 0, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 1)
        assertTrue(TranspositionTable.findEntry(board))
        assertEquals(1, TranspositionTable.getMove(1))
        assertEquals(Move.NULL, TranspositionTable.getMove(2))
    }
}