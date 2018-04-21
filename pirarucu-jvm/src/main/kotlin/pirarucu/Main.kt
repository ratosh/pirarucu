package pirarucu

import pirarucu.uci.InputHandler
import pirarucu.uci.UciInput

fun main(args: Array<String>) {
    val uciInput = UciInput(InputHandler())
    while (true) {
        val line = readLine()
        line ?: return
        try {
            uciInput.process(line)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}