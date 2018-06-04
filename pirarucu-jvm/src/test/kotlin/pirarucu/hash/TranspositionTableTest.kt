package pirarucu.hash

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.move.Move
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TranspositionTableTest {
    @BeforeTest
    fun setup() {
        TranspositionTable.reset()
        TranspositionTable.baseDepth = 19
    }

    @Test
    fun testEmptyTable() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        val info = TranspositionTable.findEntry(board)
        assertEquals(TranspositionTable.EMPTY_INFO, info)
    }

    @Test
    fun testSave() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 5, HashConstants.SCORE_TYPE_BOUND_UPPER, 7, 9, 11)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(5, TranspositionTable.getScore(info, 0))
        assertEquals(HashConstants.SCORE_TYPE_BOUND_UPPER, TranspositionTable.getScoreType(info))
        assertEquals(7, TranspositionTable.getDepth(info))
        assertEquals(11, TranspositionTable.getMove(info))
    }

    @Test
    fun testSaveMateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(EvalConstants.SCORE_MAX, TranspositionTable.getScore(info, 0))
        assertEquals(EvalConstants.SCORE_MAX - 1, TranspositionTable.getScore(info, 1))
    }

    @Test
    fun testUpdateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 3, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)
        TranspositionTable.save(board, 5, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(5, TranspositionTable.getScore(info, 0))
    }

    @Test
    fun testUpdateDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, 0, 1, 0, 0)
        TranspositionTable.save(board, 0, 0, 2, 0, 0)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(2, TranspositionTable.getDepth(info))
    }

    @Test
    fun testBaseDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 11, 0, 0)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(11, TranspositionTable.getDepth(info))
        TranspositionTable.baseDepth += 2
        assertEquals(9, TranspositionTable.getDepth(info))
    }

    @Test
    fun testUpdateMove() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, Move.NULL)
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, Move.NULL)
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 1)
        val info = TranspositionTable.findEntry(board)
        assertNotEquals(TranspositionTable.EMPTY_INFO, info)
        assertEquals(1, TranspositionTable.getMove(info))
    }
}