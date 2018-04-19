package pirarucu.uci

import pirarucu.hash.TranspositionTable
import pirarucu.search.PrincipalVariation
import pirarucu.search.SearchOptions
import pirarucu.stats.Statistics
import pirarucu.tuning.TunableConstants
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class UciInputTest {

    private val uciInput = UciInput(InputHandler())

    @BeforeTest
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
        Statistics.reset()
        Statistics.ENABLED = true
    }

    @AfterTest
    fun tearDown() {
        var search = false
        while (SearchOptions.running) {
            search = true
            Thread.yield()
        }
        if (search) {
            println(Statistics.toString())
        }
    }

    @Test
    fun testOption1() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("setoption name PHASE_PIECE_VALUE-1 value 100")
        assertEquals(TunableConstants.PHASE_PIECE_VALUE[1], 100)
    }

    @Test
    fun testOption2() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("setoption name MG_PSQT-1-0 value 100")
        assertEquals(TunableConstants.MG_PSQT[1][0], 100)
    }

    @Test
    fun testGame() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("position fen 2rq1rk1/1p2bppp/p1bppn2/P7/4P3/2N1B1P1/1PP2PBP/R2Q1RK1 w - - 0 1 moves d1d2 d6d5 e4d5 e6d5 e3b6")
        uciInput.process("go wtime 10000 btime 10000 winc 2000 binc 2000")
    }

    @Test
    fun testGame2() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("position fen rnbq1rk1/4ppbp/pn2P1p1/1p6/2pP3P/2N2N2/PP3PP1/R1BQKB1R w KQ - 0 1 moves e6f7 f8f7 h4h5 g6h5 h1h5 c8f5 c1e3 f5g4 h5g5 g4f3 g2f3 e7e6 f1h3 d8e7 f3f4 b5b4 c3e4 b6d5 d1g4 h7h6 g5g6 d5e3 f2e3 c4c3 b2c3 b4c3 g4e6 c3c2 e6e7 f7e7 a1c1 e7e4 h3g2 e4e3 e1d2 a8a7 d2e3 a7c7 g2d5 g8h7 d5e4 h7g8 c1c2 c7c2 e4c2 b8d7 g6a6 d7f6 c2b3 g8h7 b3c2 h7g8 a6a8 g7f8 c2b3 g8g7 a8a7 g7h8 a7a8 h8g7 a8a7 g7h8 b3e6 f8b4 a2a3 b4d6 a7a6 d6e7 a6a8 h8g7 a8a7 g7f8 a7b7 f6h5 b7b5 h5f6 b5b7 f6e8 a3a4 e8d6 b7b1 f8g7 b1b6 e7h4 b6d6 h4e1 e6g4 g7h7 g4f5 h7g7 d6g6 g7f7 g6h6 e1a5 f5e6 f7g7 h6h2 a5c3 d4d5 c3b2 h2b2 g7g6 b2b7 g6f6 e6h3 f6g6 h3g4")
        uciInput.process("go wtime 77485 btime 46895 winc 2000 binc 2000")
    }

    @Test
    fun testGame3() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("position fen r1nq1rk1/1p2bppp/3pbn2/p3p3/4P3/1NN1BP2/PPPQB1PP/3RK2R w K - 0 1 moves b3c1 d8c7 c1d3 f6h5 e1f2 e7h4 g2g3 h4f6 f2g2 g7g6 d1f1 c8e7 g3g4 h5f4 e3f4 e5f4 d3f4 f6c3 b2c3 e6a2 f1a1 a2e6 f4e6 f7e6 c3c4 c7c5 h1b1 a5a4 b1b7 d6d5 d2g5 e7c6 e4d5 e6d5 e2d3 c6b4 d3g6 h7g6 g5g6 g8h8")
        uciInput.process("go wtime 120000 btime 120000 winc 2000 binc 2000")
    }

    @Test
    fun testGame4() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("position fen 1r1qkbnr/pppnpp1p/3p2p1/8/2PP4/5Q1P/PP2PPP1/RNB1KB1R w KQk - 0 1 moves b1c3 e7e5 d4e5 d8c8 e5d6 f8h6 f3e4 e8f8 e4d4 h8g7 d6c7 g7d4 c7b8Q")
        uciInput.process("go wtime 10000 btime 10000 winc 2000 binc 2000")
    }

    @Test
    fun testGame5() {
        uciInput.process("uci")
        uciInput.process("ucinewgame")
        uciInput.process("isready")
        uciInput.process("position fen 1n1qkb1r/rp2pppp/p1p2n2/3p1b2/2PP4/1QN1PN2/PP3PPP/R1B1KB1R w KQk - 0 1 moves c4d5 c6d5 e1d2 b8c6 f1d3 f5g4 f3e5 c6d4 e3d4 a7a8 c3d5 d8d5 b3d5 f6d5 e5g4 e7e6 g4e5 f8b4 d2e2 e8g8 h1d1 b4d6 a1b1 d6e5 d4e5 f7f6 e5f6 f8f6 d3a6 b7a6 c1g5 f6g6 g5h4 g6g2 e2f3 g2g6 h4g3 g6g5 b1c1 a8d8 c1c5 d8f8 f3e4 d5f6 e4d4 f8d8 d4e3 g5g3 h2g3 d8d1 c5c8 g8f7 c8c7 d1d7 c7d7 f6d7 e3f3 d7c5 a2a3 c5d3 b2b3 d3e1 f3e4 e1c2 a3a4 c2a1 f2f3 a1b3 e4e3 b3c5 f3f4 c5a4 e3f2 a4b2 g3g4 b2d3 f2e3 d3b4 f4f5 e6f5 g4f5 b4c6 e3e4 c6e7 e4d4 e7f5 d4c5 f5e3 c5b6 h7h5 b6a6 h5h4 a6a5 h4h3 a5a4 h3h2 a4a3 h2h1q a3a2 e3d1 a2a1 d1f2")
        uciInput.process("go wtime 10000 btime 10000 winc 2000 binc 2000")
    }
}

