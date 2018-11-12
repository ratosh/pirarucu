package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.uci.UciOutput
import pirarucu.util.epd.BasicWorkSplitter
import pirarucu.util.epd.EpdInfo
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Phaser

class SearchErrorEvaluator(threads: Int = 1) {

    private val forkJoinPool = ForkJoinPool(threads)

    init {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true
    }

    init {
        EvalConstants.PAWN_EVAL_CACHE = false
    }

    fun evaluate(list: List<EpdInfo>, depth: Int, cacheSize: Int) {
        val phaser = Phaser()
        phaser.register()
        val worker = WorkerThread(list, phaser, depth, cacheSize)
        forkJoinPool.invoke(worker)
        phaser.arriveAndAwaitAdvance()
    }

    class WorkerThread(
        list: List<EpdInfo>,
        start: Int,
        end: Int,
        workload: Int,
        phaser: Phaser,
        private val depth: Int,
        private val ttSize: Int
    ) : BasicWorkSplitter(list, start, end, workload, phaser) {

        constructor(list: List<EpdInfo>, phaser: Phaser, depth: Int, ttSize: Int) : this(
            list,
            0,
            list.size,
            WORKLOAD,
            phaser,
            depth,
            ttSize
        )

        override fun createSubTask(
            list: List<EpdInfo>,
            start: Int,
            end: Int,
            workload: Int,
            phaser: Phaser
        ): WorkerThread {
            return WorkerThread(list, start, end, workload, phaser, depth, ttSize)
        }

        override fun evaluate() {
            val searchOptions = SearchOptions()
            searchOptions.depth = depth
            searchOptions.minSearchTime = 60000L
            searchOptions.maxSearchTime = 60000L

            val transpositionTable = TranspositionTable(ttSize)
            val search = MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable)
            val board = Board()

            for (index in start until end) {
                val epdInfo = list[index]
                BoardFactory.setBoard(epdInfo.fenPosition, board)
                searchOptions.startControl()
                transpositionTable.reset()
                search.search(board)
                epdInfo.eval = search.searchInfo.bestScore * GameConstants.COLOR_FACTOR[board.colorToMove]
            }
        }
    }
}
