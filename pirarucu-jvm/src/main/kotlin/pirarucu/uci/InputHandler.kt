package pirarucu.uci

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils
import kotlin.jvm.Volatile

class InputHandler : IInputHandler {

    override fun search(tokens: Array<String>) {
        var index = 1
        while (index < tokens.size) {
            when (tokens[index]) {
                "wtime" -> SearchOptions.whiteTime = tokens[index + 1].toInt()
                "btime" -> SearchOptions.blackTime = tokens[index + 1].toInt()
                "winc" -> SearchOptions.whiteIncrement = tokens[index + 1].toInt()
                "binc" -> SearchOptions.blackIncrement = tokens[index + 1].toInt()
                "movestogo" -> SearchOptions.movesToGo = tokens[index + 1].toInt()
            }
            index += 2
        }
        SearchOptions.setTime(board.colorToMove)
        synchronized(lock) {
            running = true
            lock.notifyAll()
        }
    }

    override fun stop() {
        SearchOptions.stop = true
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
                SearchOptions.stop = false
                running = false
                MainSearch.search(board)
            }
        }
    }

    companion object {

        private val lock = java.lang.Object()

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