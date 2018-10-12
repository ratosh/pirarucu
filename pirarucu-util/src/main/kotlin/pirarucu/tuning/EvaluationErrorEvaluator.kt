package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.eval.Evaluator
import pirarucu.util.epd.EpdInfo
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future

class EvaluationErrorEvaluator(var threads: Int = 1) {

    fun evaluate(list: List<EpdInfo>) {
        val threadList = mutableListOf<Future<*>>()
        val executor = Executors.newFixedThreadPool(threads)!!
        val pool = ConcurrentLinkedQueue(list)

        for (index in 0 until threads) {
            threadList.add(executor.submit(ErrorCalculatorThread(pool)))
        }
        for (thread in threadList) {
            thread.get()
        }
        executor.shutdown()
    }

    class ErrorCalculatorThread(
        private val pool: Queue<EpdInfo>) : Runnable {

        private var board: Board = Board()

        private val attackInfo = AttackInfo()

        override fun run() {
            while (pool.isNotEmpty()) {
                val epdInfo = pool.poll()
                if (epdInfo != null) {
                    BoardFactory.setBoard(epdInfo.fenPosition, board)
                    epdInfo.eval = Evaluator.evaluate(board, attackInfo)
                }
            }
        }
    }
}
