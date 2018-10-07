package pirarucu.search

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.uci.UciOutput
import kotlin.math.max

object MultiThreadedSearch {
    private val startLock = java.lang.Object()
    private val threadListLock = java.lang.Object()

    private val searchInfoListener = MultiThreadedSearchInfoListener()

    val transpositionTable = TranspositionTable()

    private val board = BoardFactory.getBoard()
    val searchOptions = SearchOptions()

    private val mainThread = MainThread()
    private val searchThreads = mutableListOf<HelperThread>()
    private var threadCount = 1

    fun mainBoard(): Board {
        return board
    }

    init {
        mainThread.start()
    }

    fun isRunning(): Boolean {
        if (mainThread.running) {
            return true
        }
        synchronized(threadListLock) {
            for (thread in searchThreads) {
                if (thread.running) {
                    return true
                }
            }
        }
        return false
    }

    var threads: Int
        get() = threadCount
        set(value) {
            threadCount = max(1, value)
        }

    private fun createHelperThreads() {
        synchronized(threadListLock) {
            while (searchThreads.size < threadCount - 1) {
                val helperThread = HelperThread(searchThreads.size)
                UciOutput.info(" creating helper thread ${helperThread.name}")
                searchThreads.add(helperThread)
            }
        }
    }

    // Start search
    fun search() {
        searchOptions.setTime(mainBoard().colorToMove)
        searchOptions.startControl()

        synchronized(startLock) {
            mainThread.running = true
            startLock.notifyAll()
        }

        createHelperThreads()
        for (thread in searchThreads) {
            thread.setBoard(mainBoard())
            thread.start()
        }
    }

    fun stop() {
        searchOptions.stop = true
        while (isRunning()) {
            Thread.sleep(10)
        }
    }

    fun nextHelperDepth(depth: Int, index: Int): Int {
        var result = depth + 1
        var threadCount = 0
        if (mainThread.searchDepth >= result) {
            threadCount++
        }
        synchronized(threadListLock) {
            for (searchThread in searchThreads) {
                if (searchThread.innerId != index && searchThread.searchDepth >= result) {
                    threadCount++
                }
            }
        }
        // Skip one depth if more than half of threads are on that depth or above
        if (threadCount >= threads * 0.5) {
            result++
        }
        return result
    }

    fun countNodes(): Long {
        var result = mainThread.nodeCount()
        if (threadCount > 1) {
            synchronized(threadListLock) {
                for (searchThread in searchThreads) {
                    result += searchThread.nodeCount()
                }
            }
        }
        return result
    }

    private fun removeThread(helperThread: HelperThread) {
        synchronized(threadListLock) {
            UciOutput.info(" removing helper thread ${helperThread.name}")
            searchThreads.remove(helperThread)
        }
    }

    fun reset() {
        mainThread.reset()
        transpositionTable.reset()
    }

    fun setBoard(fen: String) {
        BoardFactory.setBoard(fen, board)
    }

    fun doMove(moveString: String) {
        board.doMove(Move.getMove(board, moveString))
    }

    fun flushBoard() {
        mainThread.setBoard(board)
    }

    class HelperThread(val innerId: Int) : Thread() {
        private val board = BoardFactory.getBoard()
        private val search = MainSearch(searchOptions, searchInfoListener, transpositionTable)

        var searchDepth = 4
            private set

        var running = false

        init {
            name = "HelperSearch-$innerId"
        }

        fun nodeCount(): Long {
            return search.searchInfo.searchNodes
        }

        fun setBoard(board: Board) {
            this.board.copy(board)
        }

        private fun helperSearch() {
            var score = EvalConstants.SCORE_MIN
            while (!searchOptions.stop) {
                searchDepth = nextHelperDepth(searchDepth, innerId)
                if (searchDepth >= GameConstants.MAX_PLIES) {
                    break
                }
                score = search.searchStep(board, score, searchDepth)
            }
        }

        override fun run() {
            running = true
            helperSearch()
            removeThread(this)
            running = false
        }
    }

    class MainThread : Thread() {
        private val board = BoardFactory.getBoard()
        private val search = MainSearch(searchOptions, searchInfoListener, transpositionTable)

        var searchDepth = 1
            private set

        var running = false

        init {
            name = "MainSearch"
            isDaemon = true
            priority = Thread.MAX_PRIORITY
        }

        fun reset() {
            search.searchInfo.history.reset()
        }

        fun setBoard(board: Board) {
            this.board.copy(board)
        }

        fun nodeCount(): Long {
            return search.searchInfo.searchNodes
        }

        override fun run() {
            while (true) {
                synchronized(startLock) {
                    while (!running) {
                        startLock.wait()
                    }
                }

                search.search(board)
                synchronized(startLock) {
                    running = false
                }
            }
        }
    }
}