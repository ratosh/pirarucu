package pirarucu.tuning.evaluator

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.History
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.uci.UciOutput
import pirarucu.util.epd.BasicWorkSplitter
import pirarucu.util.epd.EpdInfo
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Phaser
import kotlin.math.max
import kotlin.math.min

class MainSearchEvaluator(private val threads: Int = 1,
                          private val depth: Int = 1,
                          private val cacheSize: Int = 1) : IEvaluator {

    private val forkJoinPool = ForkJoinPool(threads)

    init {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true
    }

    init {
        EvalConstants.PAWN_EVAL_CACHE = false
    }

    override fun evaluate(list: List<EpdInfo>) {
        val phaser = Phaser()
        phaser.register()
        val worker = WorkerThread(list, phaser, threads, depth, cacheSize)
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

        constructor(list: List<EpdInfo>, phaser: Phaser, threads: Int, depth: Int, ttSize: Int) : this(
                list,
                0,
                list.size,
                min(max(1, list.size / threads / 100), WORKLOAD),
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
            searchOptions.hasTimeLimit = false

            val transpositionTable = TranspositionTable(ttSize)
            val pawnEvaluationCache = PawnEvaluationCache(ttSize)
            val history = History()
            val search =
                    MainSearch(searchOptions, SimpleSearchInfoListener(), transpositionTable, pawnEvaluationCache, history)
            val board = Board()

            for (index in start until end) {
                val epdInfo = list[index]
                BoardFactory.setBoard(epdInfo.fenPosition, board)
                history.reset()
                transpositionTable.reset()
                searchOptions.startControl()
                search.search(board)
                epdInfo.eval = search.searchInfo.bestScore * GameConstants.COLOR_FACTOR[board.colorToMove]
                epdInfo.moveScore = epdInfo.getMoveScore(board, search.searchInfo.bestMove)
                epdInfo.time = System.currentTimeMillis() - searchOptions.startTime
                epdInfo.nodes = search.searchInfo.searchNodes
            }
        }
    }
}
