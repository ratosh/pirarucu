package pirarucu.uci

import pirarucu.board.factory.BoardFactory
import pirarucu.game.GameConstants
import pirarucu.search.MultiThreadedSearch
import pirarucu.tuning.TunableConstants
import pirarucu.util.Perft
import pirarucu.util.PlatformSpecific

class InputHandler : IInputHandler {
    override fun newGame() {
        MultiThreadedSearch.reset()
    }

    override fun isReady() {
        while (MultiThreadedSearch.isRunning()) {
            Thread.yield()
        }
        UciOutput.println("readyok")
    }

    override fun search(tokens: Array<String>) {
        var index = 1
        MultiThreadedSearch.searchOptions.hasFixedTime = false
        MultiThreadedSearch.searchOptions.hasTimeLimit = true
        MultiThreadedSearch.searchOptions.depth = GameConstants.MAX_PLIES - 1
        while (index < tokens.size) {
            when (tokens[index]) {
                "infinite" -> {
                    MultiThreadedSearch.searchOptions.hasTimeLimit = false
                }
                "depth" -> {
                    MultiThreadedSearch.searchOptions.hasTimeLimit = false
                    MultiThreadedSearch.searchOptions.depth = tokens[index + 1].toInt()
                }
                "movetime" -> {
                    MultiThreadedSearch.searchOptions.hasFixedTime = true
                    val timeLimit = tokens[index + 1].toLong()
                    MultiThreadedSearch.searchOptions.minSearchTime = timeLimit
                    MultiThreadedSearch.searchOptions.maxSearchTime = timeLimit
                }
                "wtime" -> MultiThreadedSearch.searchOptions.whiteTime = tokens[index + 1].toLong()
                "btime" -> MultiThreadedSearch.searchOptions.blackTime = tokens[index + 1].toLong()
                "winc" -> MultiThreadedSearch.searchOptions.whiteIncrement = tokens[index + 1].toLong()
                "binc" -> MultiThreadedSearch.searchOptions.blackIncrement = tokens[index + 1].toLong()
                "movestogo" -> MultiThreadedSearch.searchOptions.movesToGo = tokens[index + 1].toLong()
            }
            index += 2
        }
        MultiThreadedSearch.search()
    }

    override fun stop() {
        MultiThreadedSearch.stop()
    }

    override fun setOption(option: String, value: String) {
        println("Handling setoption $option $value")
        when (option.toLowerCase()) {
            "hash" -> {
                MultiThreadedSearch.transpositionTable.resize(value.toInt())
            }
            "pawnhash" -> {
                MultiThreadedSearch.pawnHash = value.toInt()
            }
            "threads" -> {
                MultiThreadedSearch.threads = value.toInt()
            }
            else -> {
                PlatformSpecific.applyConfig(option, value.toInt())

                TunableConstants.update()
            }
        }
    }

    override fun position(tokens: Array<String>) {
        var index = 1
        var moves = false
        while (index < tokens.size) {
            when (tokens[index]) {
                "startpos" -> {
                    MultiThreadedSearch.setBoard(BoardFactory.STARTER_FEN)
                    index++
                }
                "fen" -> {
                    val fen = tokens[2] + " " + tokens[3] + " " + tokens[4] + " " + tokens[5]
                    MultiThreadedSearch.setBoard(fen)
                    index += 4
                }
                "moves" -> {
                    moves = true
                    index++
                }
                else -> {
                    if (moves) {
                        MultiThreadedSearch.doMove(tokens[index])
                    }
                    index++
                }
            }
        }
    }

    override fun perft(tokens: Array<String>) {
        val startTime = PlatformSpecific.currentTimeMillis()
        val perftResult = Perft.perft(MultiThreadedSearch.mainBoard(), tokens[1].toInt())
        val totalTime = PlatformSpecific.currentTimeMillis() - startTime
        val nps = perftResult * 1000 / totalTime
        UciOutput.println("$perftResult nodes in ${totalTime}ms ($nps nps)")
    }
}