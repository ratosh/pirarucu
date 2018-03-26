package pirarucu.board

import kotlin.test.assertEquals
import kotlin.test.assertTrue

object BoardTestUtil {

    fun testBoard(board: Board) {
        testZobristKey(board)

        val psq = BoardUtil.calculatePsqtScore(board)
        assertEquals(psq, board.psqScore)

        val materialScore = BoardUtil.calculateMaterialScore(board)
        assertEquals(materialScore, board.materialScore)

        val phase = BoardUtil.calculatePhase(board)
        assertEquals(phase, board.phase)

        assertTrue(BoardUtil.validBoard(board))
    }

    private fun testZobristKey(board: Board) {
        val zobristKey = board.zobristKey
        val pawnZobristKey = board.pawnZobristKey

        BoardUtil.updateZobristKeys(board)

        assertEquals(zobristKey, board.zobristKey)
        assertEquals(pawnZobristKey, board.pawnZobristKey)
    }
}

