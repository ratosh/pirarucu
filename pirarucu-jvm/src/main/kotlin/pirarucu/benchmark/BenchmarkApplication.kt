package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
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
        SearchOptions.depth = 13
        SearchOptions.minSearchTimeLimit = 60000L
        SearchOptions.maxSearchTimeLimit = 60000L
        SearchOptions.searchTimeIncrement = 1000L
        var timeTaken = 0L
        for (epdInfo in iterator) {
            val board = BoardFactory.getBoard(epdInfo.fenPosition)
            TranspositionTable.reset()
            SearchOptions.stop = false
            println(epdInfo.fenPosition)

            val startTime = Utils.specific.currentTimeMillis()
            MainSearch.search(board)
            timeTaken += Utils.specific.currentTimeMillis() - startTime
            nodeCount += Statistics.searchNodes
            println(Statistics.toString())
        }

        println("Time taken (ms) $timeTaken")
        println("Nodes $nodeCount")
        println("NPS " + (nodeCount * 1000 / timeTaken))
    }
}
