package pirarucu.uci

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.move.Move
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils

class InputHandler : Runnable, IInputHandler {

    private val thread: Thread = Thread(this)

    private val board = Board()

    private val lock = java.lang.Object()

    @Volatile
    private var running = false

    init {
        thread.name = "Search"
        thread.isDaemon = true
        thread.priority = Thread.MAX_PRIORITY
        thread.start()
    }

    override fun run() {
        while (true) {
            synchronized(lock) {
                lock.wait()
            }
            SearchOptions.setTime(board.colorToMove)
            MainSearch.search(board)
            running = false
        }
    }

    override fun search(tokens: Array<String>) {
        running = true
        var index = 1
        while (index < tokens.size) {
            when (tokens[index]) {
                "wtime" -> SearchOptions.whiteTime = tokens[index + 1].toInt()
                "btime" -> SearchOptions.blackTime = tokens[index + 1].toInt()
                "winc" -> SearchOptions.whiteIncrement = tokens[index + 1].toInt()
                "binc" -> SearchOptions.blackIncrement = tokens[index + 1].toInt()
            }
            index += 2
        }
        synchronized(lock) {
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
}