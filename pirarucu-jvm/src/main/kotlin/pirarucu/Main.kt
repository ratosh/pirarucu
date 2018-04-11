package pirarucu

import pirarucu.uci.UciInput

fun main(args: Array<String>) {
    while (true) {
        val line = readLine()
        line ?: return
        try {
            UciInput.process(line)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}