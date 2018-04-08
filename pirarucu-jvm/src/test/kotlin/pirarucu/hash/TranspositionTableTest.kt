package pirarucu.hash

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.move.Move
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TranspositionTableTest {
    @BeforeTest
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
        TranspositionTable.save(board, 5, HashConstants.SCORE_TYPE_EXACT_SCORE, 7, 9, 11)
        TranspositionTable.findEntry(board)
        val score = TranspositionTable.foundScore
        val info = TranspositionTable.foundInfo
        val moves = TranspositionTable.foundMoves
        assertEquals(5, TranspositionTable.getScore(score, 0))
        assertEquals(HashConstants.SCORE_TYPE_EXACT_SCORE, TranspositionTable.getScoreType(info))
        assertEquals(7, TranspositionTable.getDepth(info))
        assertEquals(11, TranspositionTable.getFirstMove(moves))
    }

    @Test
    fun testSaveMateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, 0)
        TranspositionTable.findEntry(board)
        val score = TranspositionTable.foundScore
        assertEquals(EvalConstants.SCORE_MAX, TranspositionTable.getScore(score, 0))
        assertEquals(EvalConstants.SCORE_MAX - 1, TranspositionTable.getScore(score, 1))
    }

    @Test
    fun testUpdateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 3, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, 0)
        TranspositionTable.save(board, 5, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, 0)
        assertTrue(TranspositionTable.findEntry(board))
        val score = TranspositionTable.foundScore
        assertEquals(5, TranspositionTable.getScore(score, 0))
    }

    @Test
    fun testUpdateDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 1, 0, 0)
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 2, 0, 0)
        assertTrue(TranspositionTable.findEntry(board))
        val info = TranspositionTable.foundInfo
        assertEquals(2, TranspositionTable.getDepth(info))
    }

    @Test
    fun testUpdateMove() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, Move.NULL)
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, Move.NULL)
        TranspositionTable.save(board, 0, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, 0, 1)
        assertTrue(TranspositionTable.findEntry(board))
        val moves = TranspositionTable.foundMoves
        assertEquals(1, TranspositionTable.getMove(moves, 0))
        assertEquals(Move.NULL, TranspositionTable.getMove(moves, 1))
    }
}