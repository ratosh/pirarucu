package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
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
        var speed = LongArray(1)
        for (tries in 0 until speed.size) {
            speed[tries] = runBenchmark(DEFAULT_BENCHMARK_DEPTH)
        }
        println("Time taken " + speed.sum())
        println("Max " + speed.max()!!)
        println("Min " + speed.min()!!)
    }

    fun runBenchmark(depth: Int): Long {
        println("Running benchmark depth $depth")
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        val iterator = epdFileLoader.getEpdInfoList()
        var nodeCount = 0L
        val searchOptions = SearchOptions()
        val mainSearch = MainSearch(searchOptions)
        searchOptions.depth = depth
        searchOptions.minSearchTimeLimit = 60000L
        searchOptions.maxSearchTimeLimit = 60000L
        searchOptions.searchTimeIncrement = 1000L
        TranspositionTable.resize(16)
        val board = BoardFactory.getBoard()
        val startTime = Utils.specific.currentTimeMillis()
        for (epdInfo in iterator) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            TranspositionTable.reset()
            searchOptions.stop = false

            mainSearch.search(board)
            nodeCount += mainSearch.searchInfo.searchNodes
            println("Board (" + epdInfo.fenPosition + ") | Nodes: " + mainSearch.searchInfo.searchNodes)
        }
        val timeTaken = Utils.specific.currentTimeMillis() - startTime

        println("-")
        println("Time  : ${timeTaken}ms")
        println("Nodes : $nodeCount")
        println("NPS   : " + (nodeCount / (timeTaken / 1000)))
        return timeTaken
    }
}
