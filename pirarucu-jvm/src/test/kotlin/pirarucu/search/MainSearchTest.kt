package pirarucu.search

import pirarucu.board.Board
import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import pirarucu.board.factory.FenFactory
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MainSearchTest {

    private var searchOptions = SearchOptions()
    private var mainSearch = MainSearch(searchOptions)

    fun testSearch(fen: String, searchTime: Long) {
        searchOptions.stop = false
        TranspositionTable.reset()
        val board = BoardFactory.getBoard(fen)
        BoardFactory.setBoard(fen, board)
        searchOptions.minSearchTimeLimit = searchTime
        searchOptions.maxSearchTimeLimit = searchTime * 2
        mainSearch.search(board)
    }

    private fun moveInfo(fen: String, move: String) {
        val board = BoardFactory.getBoard(fen)

        printMoveInfo(board, move)
    }

    private fun printMoveInfo(board: Board, move: String) {
        var ply = 0
        val pvInfo = IntArray(GameConstants.GAME_MAX_LENGTH)
        val infoMove = Move.getMove(board, move)
        board.doMove(infoMove)
        while (true) {
            val info = TranspositionTable.findEntry(board)
            if (info == TranspositionTable.EMPTY_INFO) {
                break
            }

            val score = TranspositionTable.getEval(info)
            val fen = FenFactory.getFen(board)
            println("Fen $fen $score")
            val ttMove = TranspositionTable.getMove(info)
            if (ttMove != Move.NONE) {
                pvInfo[ply] = ttMove
                board.doMove(ttMove)
            } else {
                break
            }
            ply++
        }
        while (ply > 0) {
            ply--
            board.undoMove(pvInfo[ply])
        }
        board.undoMove(infoMove)
    }

    @Test
    fun testDraw() {
        testSearch("8/3k4/3P4/7p/7K/8/8/8 w - -", 1000L)
        println(mainSearch.searchInfo.toString())
        assertEquals(EvalConstants.SCORE_DRAW, mainSearch.searchInfo.bestScore)
    }

    @Test
    fun testShallowMate1() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 b - -", 1000L)
        println(mainSearch.searchInfo.toString())
        assertEquals(EvalConstants.SCORE_MAX - 1, mainSearch.searchInfo.bestScore)
    }

    @Test
    fun testShallowMate2() {
        testSearch("7K/8/3P4/3r4/8/2k3r1/7p/8 w - -", 1000L)
        println(mainSearch.searchInfo.toString())
        assertEquals(EvalConstants.SCORE_MIN + 2, mainSearch.searchInfo.bestScore)
    }

    @Test
    fun testFine70() {
        testSearch("8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w - -", 10000L)
        assertEquals(Move.createMove(Square.A1, Square.B1), mainSearch.searchInfo.bestMove)
    }

    @Ignore
    @Test
    fun testStartPosition() {
        testSearch(BoardFactory.STARTER_FEN, 10000L)
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
    fun testRandomPosition8() {
        testSearch("8/8/5Qp1/7k/2pP4/2P5/q7/1r3NK1 w - -", 15000L)
    }

    @Ignore
    @Test
    fun testRandomPosition9() {
        testSearch("r4bk1/1p4pr/3p1RQp/p2P2p1/5pP1/8/PPP4P/5RK1 b - -", 60000L)
    }

    @Ignore
    @Test
    fun testRandomPosition10() {
        testSearch("1kr5/p1pnq2p/1p5r/1P1B1p2/3Pp3/P3P3/2Q2P1P/2R1K1R1 w - - 5 25", 5000L)
        moveInfo("1kr5/p1pnq2p/1p5r/1P1B1p2/3Pp3/P3P3/2Q2P1P/2R1K1R1 w - - 5 25", "c2a4")
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

    @Ignore
    @Test
    fun testDS() {
        // Found f3d2
        testSearch("r2r3k/3qb1pp/5p2/2pP4/np6/2nB1NB1/2Q2PPP/R3R1K1 w - -", 10000L)
        // Found a1e1
        testSearch("3r1b1k/6p1/1n3p2/2n5/1p6/1Np3B1/2B2PPP/R5K1 w - -", 10000L)
    }
}