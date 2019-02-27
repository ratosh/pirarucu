package pirarucu.search

import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.AttackInfo
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class QuiescenceSearchTest {

    private var searchInfo = SearchInfo(TranspositionTable(1), History())
    private val pawnCache = PawnEvaluationCache(1)
    private var quiescenceSearch = QuiescenceSearch(searchInfo, pawnCache)

    private fun testSearch(fen: String, minDiff: Int, maxDiff: Int) {
        val board = BoardFactory.getBoard(fen)
        val evalValue =
            Evaluator.evaluate(board, AttackInfo(), pawnCache) * GameConstants.COLOR_FACTOR[board.colorToMove]
        val searchValue = quiescenceSearch.search(
            board,
            0,
            EvalConstants.SCORE_MIN,
            EvalConstants.SCORE_MAX
        )

        println("ev $evalValue")
        println("sv $searchValue")

        assertTrue(evalValue + minDiff <= searchValue)
        assertTrue(evalValue + maxDiff >= searchValue)
    }

    @BeforeTest
    fun setup() {
        searchInfo.reset()
    }

    @Test
    fun testCapture() {
        testSearch("1k6/8/8/4p3/8/8/8/2K1R3 w - -", 50, 250)
    }

    @Test
    fun testBadCapture() {
        testSearch("1k2r3/8/8/4p3/8/8/8/2K1R3 w - -", -20, 20)
    }

    @Test
    fun testCapture2() {
        testSearch("1k2r3/8/5p2/4p3/5P2/8/8/2K1R3 w - -", -60, 60)
    }

    @Ignore
    @Test
    fun testCapture3() {
        testSearch("3r3r/pb2q2k/1ppRppp1/P4Q2/2P5/7P/5PP1/3R2K1 w - -", -60, 60)
    }
}