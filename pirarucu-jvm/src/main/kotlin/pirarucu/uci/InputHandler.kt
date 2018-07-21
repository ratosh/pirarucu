package pirarucu.uci

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import pirarucu.search.MainSearch
import pirarucu.search.SearchInfo
import pirarucu.search.SearchOptions
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils

class InputHandler : IInputHandler {

    override fun search(tokens: Array<String>) {
        var index = 1
        while (index < tokens.size) {
            when (tokens[index]) {
                "wtime" -> searchOptions.whiteTime = tokens[index + 1].toLong()
                "btime" -> searchOptions.blackTime = tokens[index + 1].toLong()
                "winc" -> searchOptions.whiteIncrement = tokens[index + 1].toLong()
                "binc" -> searchOptions.blackIncrement = tokens[index + 1].toLong()
                "movestogo" -> searchOptions.movesToGo = tokens[index + 1].toLong()
            }
            index += 2
        }
        searchOptions.setTime(board.colorToMove)
        synchronized(lock) {
            running = true
            lock.notifyAll()
        }
    }

    override fun stop() {
        searchOptions.stop = true
        while (running) {
            Thread.sleep(10)
        }
    }

    override fun setOption(option: String, value: String) {
        Utils.specific.applyConfig(option, value.toInt())

        TunableConstants.update()
    }

    override fun position(tokens: Array<String>) {
        var index = 1
        var moves = false
        while (index < tokens.size) {
            when (tokens[index]) {
                "startpos" -> {
                    BoardFactory.setBoard(BoardFactory.STARTER_FEN, board)
                    index++
                }
                "fen" -> {
                    val fen = tokens[2] + " " + tokens[3] + " " + tokens[4] + " " + tokens[5]
                    BoardFactory.setBoard(fen, board)
                    index += 4
                }
                "moves" -> {
                    moves = true
                    index++
                }
                else -> {
                    if (moves) {
                        board.doMove(Move.getMove(board, tokens[index]))
                    }
                    index++
                }
            }
        }
    }

    override fun isReady() {
        UciOutput.println("readyok")
    }

    class SearchThread : Runnable {


        override fun run() {
            while (true) {
                synchronized(lock) {
                    while (!running) {
                        lock.wait()
                    }
                }
                searchOptions.stop = false
                running = false
                mainSearch.search(board, searchInfo, searchOptions)
            }
        }
    }

    companion object {

        private val lock = java.lang.Object()

        val mainSearch = MainSearch()
        val searchInfo = SearchInfo()
        val searchOptions = SearchOptions()

        @Volatile
        private var running = false

        private val board = Board()

        private val searchThread = Thread(SearchThread())

        init {
            searchThread.name = "Search"
            searchThread.isDaemon = true
            searchThread.priority = Thread.MAX_PRIORITY
            searchThread.start()
        }
    }
}