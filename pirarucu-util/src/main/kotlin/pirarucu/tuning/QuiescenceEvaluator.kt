package pirarucu.tuning

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.QuiescenceSearch
import pirarucu.search.SearchInfo
import pirarucu.util.epd.EpdInfo
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future

class QuiescenceEvaluator(var threads: Int = 1) {

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

        private val quiescenceSearch = QuiescenceSearch(SearchInfo(TranspositionTable()))

        override fun run() {
            while (pool.isNotEmpty()) {
                val epdInfo = pool.poll()
                if (epdInfo != null) {
                    BoardFactory.setBoard(epdInfo.fenPosition, board)
                    epdInfo.eval =
                        quiescenceSearch.search(board, 0, EvalConstants.SCORE_MIN, EvalConstants.SCORE_MAX) *
                        GameConstants.COLOR_FACTOR[board.colorToMove]
                }
            }
        }
    }
}
