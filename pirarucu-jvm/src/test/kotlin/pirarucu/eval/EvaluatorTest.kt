package pirarucu.eval

import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EvaluatorTest {

    val attackInfo = AttackInfo()

    @BeforeTest
    fun setup() {
    }

    @AfterTest
    fun tearDown() {
    }

    private fun evaluate(fen: String): Int {
        val board = BoardFactory.getBoard(fen)
        return Evaluator.evaluate(board, AttackInfo(), PawnEvaluationCache(1))
    }

    @Test
    fun testEqualKing() {
        val eval = evaluate("4k3/8/8/8/8/8/8/4K3 b - -")
        println("eval $eval")
        assertTrue(eval < 0)
    }

    @Test
    fun testPawnAdvantage1() {
        val eval = evaluate("4k3/4p3/8/8/8/8/8/4K3 b - -")
        println("eval $eval")
        assertTrue(eval < 0)
    }

    @Test
    fun testPawnAdvantage2() {
        val eval = evaluate("4k3/4p3/8/8/8/8/3PP3/4K3 b - -")
        println("eval $eval")
        assertTrue(eval > 0)
    }

    @Test
    fun testKnightVsPawn() {
        val eval = evaluate("4k3/3pp3/8/8/8/8/4N3/4K3 b - -")
        println("eval $eval")
        assertTrue(eval >= 0)
    }

    @Test
    fun testEval() {
        println(evaluate("6r1/8/8/1p1p3R/p1nB1r2/P1Pkp3/1PR4K/8 w - -"))
    }
}