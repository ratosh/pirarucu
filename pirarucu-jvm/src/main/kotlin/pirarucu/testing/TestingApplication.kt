package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
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
        val testScore = test(fileLoader, 8)
        val timeTaken = Utils.specific.currentTimeMillis() - startTime
        println("Time taken (ms) $timeTaken")
        println("Test score $testScore.")
    }

    fun test(testFile: EpdFileLoader, depth: Int): Int {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        var testScore = 0
        val searchOptions = SearchOptions()
        val mainSearch = MainSearch(searchOptions, SimpleSearchInfoListener())
        searchOptions.depth = depth
        searchOptions.minSearchTime = 60000L
        searchOptions.maxSearchTime = 60000L
        searchOptions.searchTimeIncrement = 1000L
        val board = BoardFactory.getBoard()
        for (epdInfo in testFile.getEpdInfoList()) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            TranspositionTable.reset()
            searchOptions.startControl()

            mainSearch.search(board)

            val score = epdInfo.getMoveScore(board, mainSearch.searchInfo.bestMove)

            testScore += score
        }

        return testScore
    }
}
