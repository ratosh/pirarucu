package pirarucu.uci

import pirarucu.hash.TranspositionTable
import pirarucu.search.PrincipalVariation
import pirarucu.util.Utils

class UciInput(private val inputHandler: IInputHandler) {

    private val lock = Any()

    fun process(command: String) {
        val tokens = command.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        when (tokens[0]) {
            "uci" -> {
                UciOutput.println("id name Pirarucu v" + Utils.specific.getVersion())
                UciOutput.println("id author Raoni Campos")
                UciOutput.println("uciok")
            }
            "isready" -> {
                inputHandler.isReady()
            }
            "ucinewgame" -> {
                PrincipalVariation.reset()
                TranspositionTable.reset()
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
            else -> UciOutput.println("Unknown command: " + tokens[0])
        }
    }
}


