package pirarucu.eval

import pirarucu.board.Color
import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrawEvaluatorTest {

    @Test
    fun testKK() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKNK() {
        var board = BoardFactory.getBoard("4k3/4n3/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4N3/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKBK() {
        var board = BoardFactory.getBoard("4k3/4b3/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4B3/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKPK() {
        var board = BoardFactory.getBoard("4k3/4p3/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4P3/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKNNK() {
        var board = BoardFactory.getBoard("4k3/4nn2/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4NN2/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKNBK() {
        var board = BoardFactory.getBoard("4k3/4nb2/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4NB2/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKBBK() {
        var board = BoardFactory.getBoard("4k3/4bb2/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
        board = BoardFactory.getBoard("4k3/4BB2/8/8/8/8/8/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKNKN() {
        val board = BoardFactory.getBoard("4k3/4n3/8/8/8/8/4N3/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKBKB() {
        val board = BoardFactory.getBoard("4k3/4b3/8/8/8/8/4B3/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKRKR() {
        val board = BoardFactory.getBoard("4k3/4r3/8/8/8/8/4R3/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKRK() {
        val board = BoardFactory.getBoard("4k3/4r3/8/8/8/8/8/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board, Color.WHITE))
        assertTrue(DrawEvaluator.hasSufficientMaterial(board, Color.BLACK))
    }

    @Test
    fun testKNKP() {
        val board = BoardFactory.getBoard("4k3/4n3/8/8/8/8/4P3/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board, Color.WHITE))
        assertFalse(DrawEvaluator.hasSufficientMaterial(board, Color.BLACK))
    }

    @Test
    fun testRule50() {
        val board = BoardFactory.getBoard("4k3/4r3/8/8/8/8/4R3/4K3 b - - 101 10")
        assertTrue(DrawEvaluator.isDrawByRules(board))
    }

    @Test
    fun testRepetition() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/P7/4K3 w - - 0 0")

        val move0 = Move.createMove(Square.A2, Square.A3)
        val move1 = Move.createMove(Square.E1, Square.D1)
        val move2 = Move.createMove(Square.E8, Square.D8)
        val move3 = Move.createMove(Square.D1, Square.E1)
        val move4 = Move.createMove(Square.D8, Square.E8)

        board.doMove(move0)
        assertFalse(DrawEvaluator.isDrawByRules(board))
        board.doMove(move1)
        assertFalse(DrawEvaluator.isDrawByRules(board))
        board.doMove(move2)
        assertFalse(DrawEvaluator.isDrawByRules(board))
        board.doMove(move3)
        assertFalse(DrawEvaluator.isDrawByRules(board))
        board.doMove(move4)
        assertTrue(DrawEvaluator.isDrawByRules(board))
        board.doMove(move1)
        assertTrue(DrawEvaluator.isDrawByRules(board))
        board.doMove(move2)
        assertTrue(DrawEvaluator.isDrawByRules(board))
        board.doMove(move3)
        assertTrue(DrawEvaluator.isDrawByRules(board))
        board.doMove(move4)
        assertTrue(DrawEvaluator.isDrawByRules(board))
    }
}