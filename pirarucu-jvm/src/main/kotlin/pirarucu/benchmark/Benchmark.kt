package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.History
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.uci.UciOutput
import pirarucu.util.PlatformSpecific
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo

object Benchmark {

    private val epdFileLoader = EpdFileLoader(Benchmark::class.java.getResourceAsStream("/benchmark.epd"))
    const val DEFAULT_BENCHMARK_DEPTH = 13

    fun runBenchmark(epdList :List<EpdInfo> = epdFileLoader.epdList, depth: Int = DEFAULT_BENCHMARK_DEPTH): Long {
        println("Running benchmark depth $depth")
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        var nodeCount = 0L
        val searchOptions = SearchOptions()
        val transpositionTable = TranspositionTable(16)
        var history = History()
        val mainSearch =
            MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable, PawnEvaluationCache(4), history)
        searchOptions.depth = depth
        searchOptions.hasTimeLimit = false
        val board = BoardFactory.getBoard()
        val startTime = PlatformSpecific.currentTimeMillis()
        for (epdInfo in epdList) {
            BoardFactory.setBoard(epdInfo.fenPosition, board)
            transpositionTable.reset()
            history.reset()
            searchOptions.startControl()

            mainSearch.search(board)
            nodeCount += mainSearch.searchInfo.searchNodes
            println("Board (" + epdInfo.fenPosition + ") | Nodes: " + mainSearch.searchInfo.searchNodes)
        }
        val timeTaken = PlatformSpecific.currentTimeMillis() - startTime

        println("-")
        println("Time  : ${timeTaken}ms")
        println("Nodes : $nodeCount")
        println("NPS   : " + (nodeCount * 1000 / timeTaken))
        return timeTaken
    }
}
