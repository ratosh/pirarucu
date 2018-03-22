package pirarucu.eval

import pirarucu.board.factory.BoardFactory
import kotlin.test.Test
import kotlin.test.assertTrue

class EvaluatorTest {

    @Test
    fun testEqualKing() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        val eval = Evaluator.evaluate(board)
        assertTrue(eval < 100)
        assertTrue(eval > -100)
    }

    @Test
    fun testPawnAdvantage() {
        var board = BoardFactory.getBoard("4k3/4p3/8/8/8/8/3PP3/4K3 b - -")
        var eval = Evaluator.evaluate(board)
        println("Eval1 $eval")
        assertTrue(eval > 0)
        board = BoardFactory.getBoard("4k3/4p3/8/8/8/8/8/4K3 b - -")
        eval = Evaluator.evaluate(board)
        println("Eval2 $eval")
        assertTrue(eval < 0)
    }
}