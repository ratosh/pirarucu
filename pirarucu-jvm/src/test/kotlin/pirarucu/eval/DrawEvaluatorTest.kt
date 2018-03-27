package pirarucu.eval

import pirarucu.board.factory.BoardFactory
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
        var board = BoardFactory.getBoard("4k3/4n3/8/8/8/8/4N3/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKBKB() {
        var board = BoardFactory.getBoard("4k3/4b3/8/8/8/8/4B3/4K3 b - -")
        assertFalse(DrawEvaluator.hasSufficientMaterial(board))
    }

    @Test
    fun testKRKR() {
        var board = BoardFactory.getBoard("4k3/4r3/8/8/8/8/4R3/4K3 b - -")
        assertTrue(DrawEvaluator.hasSufficientMaterial(board))
    }
}