package pirarucu

import pirarucu.uci.UciInput

fun main(args: Array<String>) {
    while (true) {
        val line = readLine()
        line ?: return
        UciInput.process(line)
    }
}