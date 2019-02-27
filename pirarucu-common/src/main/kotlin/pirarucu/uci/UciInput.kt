package pirarucu.uci

import pirarucu.util.PlatformSpecific

class UciInput(private val inputHandler: IInputHandler) {

    fun process(command: String) {
        val tokens = command.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        when (tokens[0]) {
            "uci" -> {
                UciOutput.println("id name Pirarucu v" + PlatformSpecific.getVersion())
                UciOutput.println("option name Hash type spin default 256 min 1 max 8192")
                UciOutput.println("option name PawnHash type spin default 32 min 1 max 1024")
                UciOutput.println("option name Threads type spin default 1 min 1 max 128")
                UciOutput.println("id author Raoni Campos")
                UciOutput.println("uciok")
            }
            "isready" -> {
                inputHandler.isReady()
            }
            "ucinewgame" -> {
                inputHandler.newGame()
            }
            "position" -> inputHandler.position(tokens)
            "setoption" -> inputHandler.setOption(tokens[2], tokens[4])
            "go" -> inputHandler.search(tokens)
            "quit" -> {
                PlatformSpecific.exit(0)
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


