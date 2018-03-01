package pirarucu.board

import kotlin.test.assertEquals

object BoardTestUtil {

    fun testBoard(board: Board) {
        testZobristKey(board)
    }

    private fun testZobristKey(board: Board) {
        val zobristKey = board.currentState.zobristKey
        val pawnZobristKey = board.currentState.pawnZobristKey

        BoardUtil.updateZobristKeys(board)

        assertEquals(zobristKey, board.currentState.zobristKey)
        assertEquals(pawnZobristKey, board.currentState.pawnZobristKey)
    }
}

