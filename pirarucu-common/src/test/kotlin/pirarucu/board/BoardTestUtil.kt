package pirarucu.board

import kotlin.test.assertEquals

object BoardTestUtil {

    fun testBoard(board: Board) {
        testZobristKey(board)

        val psq = BoardUtil.calculatePsqtScore(board)
        assertEquals(psq, board.psqScore)

        val pieceScore = BoardUtil.calculatePieceScore(board)
        assertEquals(pieceScore, board.pieceScore)

        val phase = BoardUtil.calculatePhase(board)
        assertEquals(phase, board.phase)
    }

    private fun testZobristKey(board: Board) {
        val zobristKey = board.zobristKey
        val pawnZobristKey = board.pawnZobristKey

        BoardUtil.updateZobristKeys(board)

        assertEquals(zobristKey, board.zobristKey)
        assertEquals(pawnZobristKey, board.pawnZobristKey)
    }
}

