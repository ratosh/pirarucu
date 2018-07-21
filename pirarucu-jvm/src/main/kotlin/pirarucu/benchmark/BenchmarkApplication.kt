package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchInfo
import pirarucu.search.SearchOptions
import pirarucu.stats.Statistics
import pirarucu.uci.UciOutput
import pirarucu.util.EpdFileLoader
import pirarucu.util.Utils
import java.util.concurrent.ExecutionException

object BenchmarkApplication {

    private val epdFileLoader = EpdFileLoader("G:/chess/epds/benchmark.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        runBenchmark()
    }

    fun runBenchmark() {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        val iterator = epdFileLoader.getEpdInfoList()
        var nodeCount = 0L
        val mainSearch = MainSearch()
        val searchOptions = SearchOptions()
        searchOptions.depth = 13
        searchOptions.minSearchTimeLimit = 60000L
        searchOptions.maxSearchTimeLimit = 60000L
        searchOptions.searchTimeIncrement = 1000L
        val searchInfo = SearchInfo()
        var timeTaken = 0L
        val board = BoardFactory.getBoard()
        for (epdInfo in iterator) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            TranspositionTable.reset()
            searchOptions.stop = false
            println(epdInfo.fenPosition)

            val startTime = Utils.specific.currentTimeMillis()
            mainSearch.search(board, searchInfo, searchOptions)
            timeTaken += Utils.specific.currentTimeMillis() - startTime
            nodeCount += Statistics.searchNodes
            println(Statistics.toString())
        }

        println("Time taken (ms) $timeTaken")
        println("Nodes $nodeCount")
        println("NPS " + (nodeCount * 1000 / timeTaken))
    }
}
