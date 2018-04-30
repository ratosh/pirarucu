package pirarucu.search

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.stats.Statistics
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MainSearchTest {

    private fun testSearch(fen: String, searchTime: Int) {
        val board = BoardFactory.getBoard(fen)
        BoardFactory.setBoard(fen, board)
        SearchOptions.minSearchTimeLimit = searchTime
        SearchOptions.maxSearchTimeLimit = searchTime * 2
        MainSearch.search(board)
    }

    @BeforeTest
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
        Statistics.ENABLED = true
        Statistics.reset()
    }

    @AfterTest
    fun tearDown() {
        println(Statistics.toString())
    }

    @Test
    fun testDraw() {
        testSearch("8/3k4/3P4/7p/7K/8/8/8 w - -", 1000)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_DRAW, PrincipalVariation.bestScore)
    }

    @Test
    fun testShallowMate1() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 b - -", 1000)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_MAX - 1, PrincipalVariation.bestScore)
    }

    @Test
    fun testShallowMate2() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 w - -", 1000)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_MIN + 2, PrincipalVariation.bestScore)
    }

    @Ignore
    @Test
    fun testRandomPosition() {
        testSearch("r3kb1r/ppqn1pp1/4pn1p/8/3N3P/6N1/PPPBQPP1/R3R1K1 b kq -", 1000)
    }

    @Ignore
    @Test
    fun testRandomPosition2() {
        testSearch("2Q5/5k1R/8/5B2/3N4/7P/PP3PP1/1K6 b - -", 1000)
    }

    @Ignore
    @Test
    fun testRandomPosition4() {
        testSearch("8/5kp1/8/8/8/8/5n2/K6q w - - 4 52", 1000)
    }

    @Ignore
    @Test
    fun testRandomPosition5() {
        testSearch("1k6/7R/8/4K3/5r2/8/8/8 b - -", 1000)
    }

    @Ignore
    @Test
    fun testRandomPosition6() {
        testSearch("8/8/2k5/8/8/n4K2/8/6B1 b - -", 1000)
    }
}