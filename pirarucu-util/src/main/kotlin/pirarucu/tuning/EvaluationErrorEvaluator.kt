package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.cache.PawnEvaluationCache
import pirarucu.eval.AttackInfo
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.util.epd.BasicWorkSplitter
import pirarucu.util.epd.EpdInfo
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Phaser


class EvaluationErrorEvaluator(threads: Int = 1) {
    private val forkJoinPool = ForkJoinPool(threads)

    init {
        EvalConstants.PAWN_EVAL_CACHE = false
    }

    fun evaluate(list: List<EpdInfo>) {
        val phaser = Phaser()
        phaser.register()
        val worker = WorkerThread(list, phaser)
        forkJoinPool.invoke(worker)
        phaser.arriveAndAwaitAdvance()
    }

    class WorkerThread(
        list: List<EpdInfo>,
        start: Int,
        end: Int,
        workload: Int,
        phaser: Phaser
    ) : BasicWorkSplitter(list, start, end, workload, phaser) {

        constructor(list: List<EpdInfo>, phaser: Phaser) : this(list, 0, list.size, WORKLOAD, phaser)

        override fun createSubTask(
            list: List<EpdInfo>,
            start: Int,
            end: Int,
            workload: Int,
            phaser: Phaser
        ): WorkerThread {
            return WorkerThread(list, start, end, workload, phaser)
        }

        override fun evaluate() {
            val board = Board()
            val attackInfo = AttackInfo()
            val pawnEvalCache = PawnEvaluationCache(1)
            for (index in start until end) {
                val entry = list[index]
                BoardFactory.setBoard(entry.fenPosition, board)
                entry.eval = Evaluator.evaluate(board, attackInfo, pawnEvalCache)
            }
        }
    }
}
