package pirarucu.eval

import pirarucu.board.Piece
import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import pirarucu.tuning.TunableConstants
import kotlin.test.Test
import kotlin.test.assertEquals

class StaticExchangeEvaluatorTest {

    private fun testSee(fen: String, move: String): Int {
        val board = BoardFactory.getBoard(fen)
        val seeValue = StaticExchangeEvaluator.getSeeCaptureScore(board, Move.getMove(board, move))

        println("result1 $seeValue")
        return seeValue
    }

    @Test
    fun testExhange1() {
        val seeValue = testSee("1k6/8/8/4p3/8/8/8/2K1R3 w - -", "e1e5")
        assertEquals(TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN], seeValue)
    }

    @Test
    fun testExhange2() {
        val seeValue = testSee("5K1k/8/8/8/8/8/1r6/Rr6 w - -", "a1b1")
        assertEquals(0, seeValue)
    }

    @Test
    fun testExhange3() {
        val seeValue = testSee("5K1k/8/8/8/8/8/b7/RrR5 w - -", "a1b1")
        assertEquals(TunableConstants.QS_FUTILITY_VALUE[Piece.BISHOP], seeValue)
    }

    @Test
    fun testExhange4() {
        val seeValue = testSee("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -", "d3e5")
        assertEquals(TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN] -
            TunableConstants.QS_FUTILITY_VALUE[Piece.KNIGHT], seeValue)
    }

    @Test
    fun testExhange5() {
        val seeValue = testSee("1k5q/3n4/5b2/4p3/8/3N1N2/4R3/2K1Q3 w - -", "d3e5")
        assertEquals(TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN], seeValue)
    }

    @Test
    fun testExhange6() {
        val seeValue = testSee("1k5q/3n4/3p1b2/4p3/8/3N1N2/4R3/2K1Q3 w - -", "d3e5")
        assertEquals(TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN] -
            TunableConstants.QS_FUTILITY_VALUE[Piece.KNIGHT], seeValue)
    }

    @Test
    fun testExhangeQuiet() {
        val seeValue = testSee("8/8/1p1k4/1P6/8/3p3P/1r4P1/5K2 w - -", "g2g4")
        assertEquals(0, seeValue)
    }

    @Test
    fun testExhangeQuietLosing() {
        val seeValue = testSee("8/8/1p1k4/1P6/3p4/1r5P/6P1/5K2 w - -", "g2g3")
        assertEquals(-TunableConstants.QS_FUTILITY_VALUE[Piece.PAWN], seeValue)
    }
}