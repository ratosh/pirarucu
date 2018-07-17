package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.PrincipalVariation
import pirarucu.search.SearchOptions
import pirarucu.uci.UciOutput
import pirarucu.util.EpdFileLoader
import pirarucu.util.Utils
import java.util.concurrent.ExecutionException

object TestingApplication {

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val startTime = Utils.specific.currentTimeMillis()
        val fileLoader = EpdFileLoader("G:/chess/epds/STS/STS.epd")
        val testScore = test(fileLoader, 3)
        val timeTaken = Utils.specific.currentTimeMillis() - startTime
        println("Time taken (ms) $timeTaken")
        println("Test score $testScore.")
    }

    fun test(testFile: EpdFileLoader, depth: Int): Int {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        var testScore = 0
        SearchOptions.minSearchTimeLimit = 10000L
        SearchOptions.maxSearchTimeLimit = 10000L
        SearchOptions.searchTimeIncrement = 10000L
        SearchOptions.depth = depth
        for (epdInfo in testFile.getEpdInfoList()) {
            val board = BoardFactory.getBoard(epdInfo.fenPosition)
            TranspositionTable.reset()
            SearchOptions.stop = false

            MainSearch.search(board)

            val score = epdInfo.getMoveScore(board, PrincipalVariation.bestMove)

            testScore += score
        }

        return testScore
    }
}
