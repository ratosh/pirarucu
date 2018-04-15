package pirarucu.eval

import pirarucu.board.factory.BoardFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EvaluatorTest {

    @BeforeTest
    fun setup() {
        EvalDebug.ENABLED = true
    }

    @AfterTest
    fun tearDown() {
        println(EvalDebug.toString())
    }

    @Test
    fun testEqualKing() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        val eval = Evaluator.evaluate(board)
        assertTrue(eval < 100)
        assertTrue(eval > -100)
    }

    @Test
    fun testPawnAdvantage1() {
        val board = BoardFactory.getBoard("4k3/4p3/8/8/8/8/8/4K3 b - -")
        val eval = Evaluator.evaluate(board)
        println("Eval2 $eval")
        assertTrue(eval < 0)
    }

    @Test
    fun testPawnAdvantage2() {
        var board = BoardFactory.getBoard("4k3/4p3/8/8/8/8/3PP3/4K3 b - -")
        var eval = Evaluator.evaluate(board)
        println("Eval1 $eval")
        assertTrue(eval > 0)
    }

    @Test
    fun testKnightVsPawn() {
        var board = BoardFactory.getBoard("4k3/3pp3/8/8/8/8/4N3/4K3 b - -")
        var eval = Evaluator.evaluate(board)
        println("Eval1 $eval")
        assertTrue(eval > 0)
    }
}