package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
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
        test("G:/chess/epds/STS/STS.epd", 100L)
    }

    fun test(testFile: String, searchTime: Long) {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true
        val fileLoader = EpdFileLoader(testFile)

        var testScore = 0
        SearchOptions.minSearchTimeLimit = searchTime
        SearchOptions.maxSearchTimeLimit = searchTime
        SearchOptions.searchTimeIncrement = 100L
        var timeTaken = 0L
        var entryCount = 0
        for (epdInfo in fileLoader.getEpdInfoList()) {
            val board = BoardFactory.getBoard(epdInfo.fenPosition)
            TranspositionTable.reset()
            SearchOptions.stop = false
            entryCount++

            val startTime = Utils.specific.currentTimeMillis()
            MainSearch.search(board)
            timeTaken += Utils.specific.currentTimeMillis() - startTime

            val score = epdInfo.getMoveScore(board, PrincipalVariation.bestMove)

            if (score == 0) {
                println("Improve $entryCount")
                println("Fen: " + epdInfo.fenPosition)
                println("FoundMove: " + Move.toString(PrincipalVariation.bestMove))
            }

            testScore += score
        }

        println("Time taken (ms) $timeTaken")
        println("Test score $testScore.")
    }
}
