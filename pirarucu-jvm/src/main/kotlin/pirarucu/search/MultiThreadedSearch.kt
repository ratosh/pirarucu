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

    // Laser based SMP skip
    private val SMP_SKIP_DEPTHS = mutableListOf(1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4)
    private val SMP_SKIP_AMOUNT = mutableListOf(1, 2, 1, 2, 3, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6)

    private val SMP_MAX_CYCLES = SMP_SKIP_AMOUNT.size

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
            createHelperThreads()
        }

    private fun createHelperThreads() {
        synchronized(threadListLock) {
            while (searchThreads.size < threadCount - 1) {
                val helperThread = HelperThread(searchThreads.size)
                UciOutput.info(" creating helper thread ${helperThread.name}")
                searchThreads.add(helperThread)
                helperThread.start()
            }
            while (searchThreads.size > threadCount - 1) {
                val helperThread = searchThreads.last()
                UciOutput.info(" removing helper thread ${helperThread.name}")
                searchThreads.remove(helperThread)
            }
        }
    }

    // Start search
    fun search() {
        searchOptions.setTime(mainBoard().colorToMove)
        searchOptions.startControl()

        synchronized(startLock) {
            mainThread.running = true
            // Let the main search go into search as soon as possible
            startLock.notifyAll()
            if (threadCount > 1) {
                synchronized(threadListLock) {
                    for (searchThread in searchThreads) {
                        searchThread.running = true
                    }
                }
                // Other threads start search
                startLock.notifyAll()
            }
        }
    }

    fun stop() {
        searchOptions.stop = true
        while (isRunning()) {
            Thread.sleep(10)
        }
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

    fun reset() {
        mainThread.reset()
        transpositionTable.reset()
        if (threadCount > 1) {
            synchronized(threadListLock) {
                for (searchThread in searchThreads) {
                    searchThread.reset()
                }
            }
        }
    }

    fun setBoard(fen: String) {
        BoardFactory.setBoard(fen, board)
    }

    fun doMove(moveString: String) {
        board.doMove(Move.getMove(board, moveString))
    }

    fun flushBoard() {
        mainThread.setBoard(board)
        if (threadCount > 1) {
            synchronized(threadListLock) {
                for (searchThread in searchThreads) {
                    searchThread.setBoard(board)
                }
            }
        }
    }

    class HelperThread(innerId: Int) : Thread() {
        private val board = BoardFactory.getBoard()
        private val search = MainSearch(searchOptions, searchInfoListener, transpositionTable)

        private val cycleIndex = innerId % SMP_MAX_CYCLES

        private var searchDepth = 1

        var running = false

        init {
            name = "HelperSearch-$innerId"
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

        private fun helperSearch() {
            var score = EvalConstants.SCORE_MIN
            searchDepth = 0
            search.searchInfo.reset()
            while (!searchOptions.stop) {
                searchDepth++

                if ((searchDepth + cycleIndex) % SMP_SKIP_DEPTHS[cycleIndex] == 0) {
                    searchDepth += SMP_SKIP_AMOUNT[cycleIndex]
                }

                if (searchDepth >= GameConstants.MAX_PLIES) {
                    break
                }
                score = search.searchStep(board, score, searchDepth)
            }
        }

        override fun run() {
            while (true) {
                synchronized(startLock) {
                    while (!running) {
                        startLock.wait()
                    }
                }

                helperSearch()
                synchronized(startLock) {
                    running = false
                }
            }
        }
    }

    class MainThread : Thread() {
        private val board = BoardFactory.getBoard()
        private val search = MainSearch(searchOptions, searchInfoListener, transpositionTable)

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