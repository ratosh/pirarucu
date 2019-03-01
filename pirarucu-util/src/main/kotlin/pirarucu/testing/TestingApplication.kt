package pirarucu.testing

import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.History
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.uci.UciOutput
import pirarucu.util.epd.EpdFileLoader
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object TestingApplication {

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val timeTaken = measureTimeMillis {
            val fileLoader = EpdFileLoader("G:/chess/epds/STS/STS.epd")
            val testScore = test(fileLoader, 8)
            println("Test score $testScore.")
        }
        println("Time taken (ms) $timeTaken")
    }

    fun test(testFile: EpdFileLoader, depth: Int): Int {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        var testScore = 0
        val searchOptions = SearchOptions()
        val transpositionTable = TranspositionTable(4)
        val pawnCache = PawnEvaluationCache(1)
        val history = History()
        val mainSearch = MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable, pawnCache, history)
        searchOptions.depth = depth
        searchOptions.minSearchTime = 60000L
        searchOptions.maxSearchTime = 60000L
        searchOptions.searchTimeIncrement = 60000L
        val board = BoardFactory.getBoard()
        var partialScore = 0
        for ((index, epdInfo) in testFile.getEpdInfoList().withIndex()) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            transpositionTable.reset()
            searchOptions.startControl()
            history.reset()

            mainSearch.search(board)

            val score = epdInfo.getMoveScore(board, mainSearch.searchInfo.bestMove)

            /*
            if (score == 0) {
                println("Improvement needed " + epdInfo.comment)
                println("Fen " + epdInfo.fenPosition)
                println("Wanted moves " + epdInfo.moveScoreList?.keys?.joinToString(" "))
                println("Found move " + Move.toString(mainSearch.searchInfo.bestMove))
            }
            */

            partialScore += score

            if (index % 100 == 99) {
                println((index / 100).toString() + " score $partialScore")
                partialScore = 0
                testScore += partialScore
            }
        }

        return testScore
    }
}
