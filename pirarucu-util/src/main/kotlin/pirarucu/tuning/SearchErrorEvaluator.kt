package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.util.epd.EpdInfo
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SearchErrorEvaluator(var threads: Int = 1) {

    fun evaluate(list: List<EpdInfo>, depth: Int, cacheSize: Int) {
        val threadList = mutableListOf<Future<*>>()
        val executor = Executors.newFixedThreadPool(threads)!!
        val pool = ConcurrentLinkedQueue(list)

        for (index in 0 until threads) {
            threadList.add(executor.submit(ErrorCalculatorThread(pool, depth, cacheSize)))
        }
        for (thread in threadList) {
            thread.get()
        }
        executor.shutdown()
    }

    class ErrorCalculatorThread(
        private val pool: Queue<EpdInfo>,
        depth: Int,
        ttSize: Int) : Runnable {

        private var board: Board = Board()

        private val searchOptions = SearchOptions()
        private val transpositionTable = TranspositionTable()
        private val search = MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable)

        init {
            searchOptions.depth = depth
            searchOptions.minSearchTime = 60000L
            searchOptions.maxSearchTime = 60000L
            transpositionTable.resize(ttSize)
        }

        override fun run() {
            while (pool.isNotEmpty()) {
                val epdInfo = pool.poll()
                if (epdInfo != null) {
                    BoardFactory.setBoard(epdInfo.fenPosition, board)
                    searchOptions.startControl()
                    transpositionTable.reset()
                    search.search(board)
                    epdInfo.eval = search.searchInfo.bestScore *
                        GameConstants.COLOR_FACTOR[board.colorToMove]
                }
            }
        }
    }
}
