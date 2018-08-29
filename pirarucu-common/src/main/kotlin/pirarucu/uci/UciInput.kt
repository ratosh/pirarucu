package pirarucu.uci

import pirarucu.hash.TranspositionTable
import pirarucu.search.History
import pirarucu.util.Utils

class UciInput(private val inputHandler: IInputHandler) {

    fun process(command: String) {
        val tokens = command.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        when (tokens[0]) {
            "uci" -> {
                UciOutput.println("id name Pirarucu v" + Utils.specific.getVersion())
                UciOutput.println("option name Hash type spin default 256 min 1 max 32768")
                UciOutput.println("id author Raoni Campos")
                UciOutput.println("uciok")
            }
            "isready" -> {
                inputHandler.isReady()
            }
            "ucinewgame" -> {
                TranspositionTable.reset()
                History.reset()
            }
            "position" -> inputHandler.position(tokens)
            "setoption" -> inputHandler.setOption(tokens[2], tokens[4])
            "go" -> inputHandler.search(tokens)
            "quit" -> {
                Utils.specific.exit(0)
            }
            "stop" -> {
                inputHandler.stop()
            }
            "perft" -> {
                inputHandler.perft(tokens)
            }
            else -> UciOutput.println("Unknown command: " + tokens[0])
        }
    }
}


