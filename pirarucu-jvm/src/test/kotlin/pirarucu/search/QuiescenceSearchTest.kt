package pirarucu.search

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuiescenceSearchTest {

    private fun testSearch(fen: String, minDiff: Int, maxDiff: Int) {
        val board = BoardFactory.getBoard(fen)
        val evalValue = Evaluator.evaluate(board) * GameConstants.COLOR_FACTOR[board.colorToMove]
        val searchValue = QuiescenceSearch.search(board,
            MoveList(),
            0,
            EvalConstants.SCORE_MIN,
            EvalConstants.SCORE_MAX)

        println("ev $evalValue")
        println("sv $searchValue")

        assertTrue(evalValue + minDiff <= searchValue)
        assertTrue(evalValue + maxDiff >= searchValue)
    }

    private fun testSearch(fen: String, expectedValue: Int) {
        val board = BoardFactory.getBoard(fen)
        val searchValue = QuiescenceSearch.search(board, MoveList(), 0,
            EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
        assertEquals(expectedValue, searchValue)
    }

    private fun testResearch(fen: String, depth: Int) {
        val board = BoardFactory.getBoard(fen)
        if (depth == 0) {
            QuiescenceSearch.search(board, MoveList(), 0,
                EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
        } else {
            testResearch(board, MoveList(), depth)
        }
    }

    private fun testResearch(board: Board, moveList: MoveList, depth: Int) {
        if (depth == 0) {
            QuiescenceSearch.search(board, moveList, 0,
                EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX)
        } else {
            moveList.startPly()
            MoveGenerator.legalAttacks(board, moveList)
            MoveGenerator.legalMoves(board, moveList)
            while (moveList.hasNext()) {
                val move = moveList.next()
                board.doMove(move)
                testResearch(board, moveList, depth - 1)
                board.undoMove(move)
            }
            moveList.endPly()
        }
    }


    @BeforeTest
    fun setup() {
        TranspositionTable.reset()
        PrincipalVariation.reset()
    }

    @Test
    fun testCapture() {
        testSearch("1k6/8/8/4p3/8/8/8/2K1R3 w - -", 90, 120)
    }

    @Test
    fun testBadCapture() {
        testSearch("1k2r3/8/8/4p3/8/8/8/2K1R3 w - -", 0, 0)
    }

    @Test
    fun testCapture2() {
        testSearch("1k2r3/8/5p2/4p3/5P2/8/8/2K1R3 w - -", -60, 60)
    }

    @Test
    fun testRandomPosition() {
        testResearch("r3kb1r/ppqn1pp1/4pn1p/8/3N3P/6N1/PPPBQPP1/R3R1K1 b kq -", 3)
    }
}