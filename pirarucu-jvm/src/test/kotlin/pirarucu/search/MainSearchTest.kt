package pirarucu.search

import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.stats.Statistics
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MainSearchTest {

    private fun testSearch(fen: String, searchTime: Long) {
        SearchOptions.stop = false
        TranspositionTable.reset()
        PrincipalVariation.reset()
        Statistics.reset()
        val board = BoardFactory.getBoard(fen)
        BoardFactory.setBoard(fen, board)
        SearchOptions.minSearchTimeLimit = searchTime
        SearchOptions.maxSearchTimeLimit = searchTime * 2
        MainSearch.search(board)
    }

    @AfterTest
    fun tearDown() {
        println(Statistics.toString())
    }

    @Test
    fun testDraw() {
        testSearch("8/3k4/3P4/7p/7K/8/8/8 w - -", 1000L)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_DRAW, PrincipalVariation.bestScore)
    }

    @Test
    fun testShallowMate1() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 b - -", 1000L)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_MAX - 1, PrincipalVariation.bestScore)
    }

    @Test
    fun testShallowMate2() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 w - -", 1000L)
        println(PrincipalVariation.toString())
        assertEquals(EvalConstants.SCORE_MIN + 2, PrincipalVariation.bestScore)
    }

    @Test
    fun testFine70() {
        testSearch("8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w - -", 10000L)
        assertEquals(Move.createMove(Square.A1, Square.B1), PrincipalVariation.bestMove)
    }

    @Ignore
    @Test
    fun testRandomPosition() {
        testSearch("r3kb1r/ppqn1pp1/4pn1p/8/3N3P/6N1/PPPBQPP1/R3R1K1 b kq -", 1000L)
    }

    @Ignore
    @Test
    fun testRandomPosition2() {
        testSearch("2Q5/5k1R/8/5B2/3N4/7P/PP3PP1/1K6 b - -", 1000L)
    }

    @Ignore
    @Test
    fun testRandomPosition4() {
        testSearch("8/5kp1/8/8/8/8/5n2/K6q w - - 4 52", 1000L)
    }

    @Ignore
    @Test
    fun testRandomPosition5() {
        testSearch("1k6/7R/8/4K3/5r2/8/8/8 b - -", 1000L)
    }

    @Ignore
    @Test
    fun testRandomPosition6() {
        testSearch("2b5/1p3k2/7R/4p1rP/1qpnR3/8/P4PP1/3Q2K1 w - -", 60000L)
    }

    @Ignore
    @Test
    fun testRandomPosition7() {
        testSearch("r4k1r/3nbp1p/p1b1Nn2/q3p1B1/Np2P3/1Q3B2/PPP3PP/2KRR3 b - -", 60000L)
    }

    @Ignore
    @Test
    fun testDiv8G1() {
        // Board state
        testSearch("3k2r1/1r1n1p2/1b2p3/p1ppP1p1/P4q1p/1P1P1P1P/3NQBP1/2R1RK2 b - -", 10000L)
        // B6C7
        testSearch("3k2r1/1rbn1p2/4p3/p1ppP1p1/P4q1p/1P1P1P1P/3NQBP1/2R1RK2 w - -", 10000L)
        // F4H2 Search
        testSearch("3k2r1/1r1n1p2/1b2p3/p1ppP1p1/P6p/1P1P1P1P/3NQBPq/2R1RK2 w - -", 10000L)
    }

    @Ignore
    @Test
    fun testDiv8G2() {
        // Board state - Found e6e5 - BM d6d5
        testSearch("rn1q1rk1/p3bp2/bppppn1p/6p1/Q1PP4/2N2NP1/PP1BPPBP/R3R1K1 b - -", 10000L)
        // E6E5 Search
        testSearch("rn1q1rk1/p3bp2/bppp1n1p/4p1p1/Q1PP4/2N2NP1/PP1BPPBP/R3R1K1 w - -", 10000L)
        // D6D5 Search
        testSearch("rn1q1rk1/p3bp2/bpp1pn1p/3p2p1/Q1PP4/2N2NP1/PP1BPPBP/R3R1K1 w - -", 10000L)
    }
}