package pirarucu.search

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.MoveList
import kotlin.test.BeforeTest
import kotlin.test.Test
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

        println("ev $evalValue")
        println("sv $searchValue")

        assertTrue(evalValue + minDiff <= searchValue)
        assertTrue(evalValue + maxDiff >= searchValue)
    }

    @BeforeTest
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
    }

    @Test
    fun testCapture() {
        testSearch("1k6/8/8/4p3/8/8/8/2K1R3 w - -", 100, 200)
    }

    @Test
    fun testBadCapture() {
        testSearch("1k2r3/8/8/4p3/8/8/8/2K1R3 w - -", -20, 20)
    }

    @Test
    fun testCapture2() {
        testSearch("1k2r3/8/5p2/4p3/5P2/8/8/2K1R3 w - -", -60, 60)
    }
}