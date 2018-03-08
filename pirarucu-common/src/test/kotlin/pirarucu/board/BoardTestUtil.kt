package pirarucu.board

import kotlin.test.assertEquals

object BoardTestUtil {

    fun testBoard(board: Board) {
        testZobristKey(board)
    }

    private fun testZobristKey(board: Board) {
        val zobristKey = board.zobristKey
        val pawnZobristKey = board.pawnZobristKey

        BoardUtil.updateZobristKeys(board)

        assertEquals(zobristKey, board.zobristKey)
        assertEquals(pawnZobristKey, board.pawnZobristKey)
    }
}

