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

    private val epdFileLoader = EpdFileLoader(BenchmarkApplication::class.java.getResourceAsStream("/benchmark.epd"))
    const val DEFAULT_BENCHMARK_DEPTH = 13

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var timeTaken = 0L
        for (index in 0 until 5) {
            timeTaken += runBenchmark(DEFAULT_BENCHMARK_DEPTH)
        }
        println("Total time taken $timeTaken")

    }

    fun runBenchmark(depth: Int): Long {
        println("Running benchmark depth $depth")
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        val iterator = epdFileLoader.getEpdInfoList()
        var nodeCount = 0L
        val mainSearch = MainSearch()
        val searchOptions = SearchOptions()
        searchOptions.depth = depth
        searchOptions.minSearchTimeLimit = 60000L
        searchOptions.maxSearchTimeLimit = 60000L
        searchOptions.searchTimeIncrement = 1000L
        val searchInfo = SearchInfo()
        val board = BoardFactory.getBoard()
        val startTime = Utils.specific.currentTimeMillis()
        for (epdInfo in iterator) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            TranspositionTable.reset()
            searchOptions.stop = false

            mainSearch.search(board, searchInfo, searchOptions)
            nodeCount += Statistics.searchNodes
        }
        val timeTaken = Utils.specific.currentTimeMillis() - startTime

        println("-")
        println("Time  : ${timeTaken}ms")
        println("Nodes : $nodeCount")
        println("NPS   : " + (nodeCount / (timeTaken / 1000)))
        return timeTaken
    }
}
