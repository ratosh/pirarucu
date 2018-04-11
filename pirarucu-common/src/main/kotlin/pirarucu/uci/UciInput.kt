package pirarucu.uci

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.hash.TranspositionTable
import pirarucu.move.Move
import pirarucu.search.MainSearch
import pirarucu.search.PrincipalVariation
import pirarucu.search.SearchOptions
import pirarucu.tuning.TunableConstants
import pirarucu.util.Utils

object UciInput {
    private val board = Board()

    fun process(command: String) {
        val tokens = command.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        when (tokens[0]) {
            "uci" -> {
                UciOutput.println("id name Pirarucu 1.0")
                UciOutput.println("id author Raoni Campos")
                UciOutput.println("uciok")
            }
            "isready" -> {
                UciOutput.println("readyok")
            }
            "ucinewgame" -> {
                PrincipalVariation.reset()
                TranspositionTable.reset()
            }
            "position" -> position(tokens)
            "setoption" -> setOption(tokens[2], tokens[4])
            "go" -> search(tokens)
            "quit" -> {
                SearchOptions.stop = true
                Utils.specific.exit(0)
            }
            "stop" -> SearchOptions.stop = true
            else -> UciOutput.println("Unknown command: " + tokens[0])
        }
    }

    private fun setOption(option: String, value: String) {
        Utils.specific.applyConfig(option, value.toInt())

        TunableConstants.update()
    }

    private fun position(tokens: Array<String>) {
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

    private fun search(tokens: Array<String>) {
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
        SearchOptions.setTime(board.colorToMove)
        MainSearch.search(board)
    }
}


