package pirarucu.search

import org.junit.Before
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.MoveList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuiescenceSearchTest {

    private fun testSearch(fen: String, minDiff: Int, maxDiff: Int) {
        val board = BoardFactory.getBoard(fen)
        val evalValue = Evaluator.evaluate(board) * GameConstants.COLOR_FACTOR[board.colorToMove]
        val searchValue = QuiescenceSearch.search(board,
            MoveList(),
            0,
            EvalConstants.SCORE_MIN,
            EvalConstants.SCORE_MAX)
        assertTrue(evalValue + minDiff <= searchValue)
        assertTrue(evalValue + maxDiff >= searchValue)
    }

    private fun testSearch(fen: String, expectedValue: Int) {
        val board = BoardFactory.getBoard(fen)
        val searchValue = QuiescenceSearch.search(board, MoveList(), 0,
            EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
        assertEquals(expectedValue, searchValue)
    }

    @Before
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
    }

    @Test
    fun testCapture() {
        testSearch("1k6/8/8/4p3/8/8/8/2K1R3 w - -", 90, 120)
    }

    @Test
    fun testBadCapture() {
        testSearch("1k2r3/8/8/4p3/8/8/8/2K1R3 w - -", 0, 0)
    }

    @Test
    fun testCapture2() {
        testSearch("1k2r3/8/5p2/4p3/5P2/8/8/2K1R3 w - -", -20, 20)
    }

    @Test
    fun testStalemate() {
        testSearch("1k6/8/8/8/8/3q4/5r2/2K5 w - -", EvalConstants.SCORE_DRAW)
    }

    @Test
    fun testMated() {
        testSearch("1k6/8/8/8/8/8/5r2/2K3r1 w - -", -EvalConstants.SCORE_MAX)
    }
}