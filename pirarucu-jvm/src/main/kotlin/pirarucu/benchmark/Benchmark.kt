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

object Benchmark {

    private val epdFileLoader = EpdFileLoader(Benchmark::class.java.getResourceAsStream("/benchmark.epd"))
    const val DEFAULT_BENCHMARK_DEPTH = 13

    fun runBenchmark(depth: Int): Long {
        println("Running benchmark depth $depth")
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        val iterator = epdFileLoader.getEpdInfoList()
        var nodeCount = 0L
        val searchOptions = SearchOptions()
        val transpositionTable = TranspositionTable()
        transpositionTable.resize(16)
        val mainSearch = MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable)
        searchOptions.depth = depth
        searchOptions.minSearchTime = 60000L
        searchOptions.maxSearchTime = 60000L
        searchOptions.searchTimeIncrement = 1000L
        val board = BoardFactory.getBoard()
        val startTime = Utils.specific.currentTimeMillis()
        for (epdInfo in iterator) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            transpositionTable.reset()
            searchOptions.startControl()

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
