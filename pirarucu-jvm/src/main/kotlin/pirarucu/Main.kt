package pirarucu

import pirarucu.uci.InputHandler
import pirarucu.uci.UciInput

fun main(args: Array<String>) {
    val uciInput = UciInput(InputHandler())
    while (true) {
        try {
            val line = readLine()
            line ?: return
            uciInput.process(line)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}