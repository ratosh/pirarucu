package pirarucu.search

import org.junit.Before
import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import pirarucu.hash.HashConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import kotlin.test.Test
import kotlin.test.assertEquals

class PrincipalVariationTest {

    private fun testSearch(fen: String, expectedMoveList: IntArray) {
        val board = BoardFactory.getBoard(fen)
        for (expectedMove in expectedMoveList) {
            TranspositionTable.save(board, 100, 1, HashConstants.SCORE_TYPE_EXACT_SCORE, 0, expectedMove)
            board.doMove(expectedMove)
        }
        BoardFactory.setBoard(fen, board)
        PrincipalVariation.save(board)
        assertEquals(expectedMoveList.size, PrincipalVariation.maxDepth)
        for (index in expectedMoveList.indices) {
            assertEquals(expectedMoveList[index], PrincipalVariation.bestMoveList[index])
        }
    }

    @Before
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
    }

    @Test
    fun testSave() {
        testSearch("1k6/8/8/4p3/8/8/8/2K1R3 w - -",
            intArrayOf(Move.createMove(Square.C1, Square.C2)))
    }
}