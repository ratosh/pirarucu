package pirarucu.util.factory

import pirarucu.util.EpdInfo
import java.text.NumberFormat
import java.text.ParseException
import java.util.Arrays
import java.util.HashSet

object EpdInfoFactory {

    fun getEpdInfo(fenLine: String): EpdInfo {
        var tempLine = fenLine

        var fenPosition: String
        var bestMoveList: Set<String>?
        var avoidMoveList: Set<String>?
        var result = 0.toDouble()
        var comment = ""

        val commentIndex = tempLine.indexOf(";")
        if (commentIndex >= 0) {

            comment = tempLine.substring(commentIndex + 1, tempLine.length)
            tempLine = tempLine.substring(0, commentIndex)
        }

        val c9Index = tempLine.indexOf("c9")

        if (c9Index >= 0) {
            val c9String = tempLine.substring(c9Index + 3, tempLine.length)
            tempLine = tempLine.substring(0, c9Index)

            when {
                c9String.contains("1/2") -> result = 0.5
                c9String.contains("1-0") -> result = 1.0
                c9String.contains("0-1") -> result = 0.0
                else -> {
                    val nf = NumberFormat.getInstance()
                    try {
                        result = nf.parse(c9String).toDouble()
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            result = 0.5
        }

        val bmIndex = tempLine.indexOf(" bm ")

        if (bmIndex >= 0) {
            val bmString = tempLine.substring(bmIndex + 3, tempLine.length)
            tempLine = tempLine.substring(0, bmIndex)

            bestMoveList = HashSet<String>(Arrays.asList(*bmString.split(" ".toRegex())
                .dropLastWhile({ it.isEmpty() }).toTypedArray()))
        } else {
            bestMoveList = null
        }

        val amIndex = tempLine.indexOf("am ")

        if (amIndex >= 0) {
            val amString = tempLine.substring(amIndex + 3, tempLine.length)
            tempLine = tempLine.substring(0, amIndex)

            avoidMoveList = HashSet<String>(Arrays.asList(*amString.split(" ".toRegex())
                .dropLastWhile({ it.isEmpty() }).toTypedArray()))
        } else {
            avoidMoveList = null
        }
        if (!tempLine.isEmpty()) {
            fenPosition = tempLine.trim({ it <= ' ' })
        } else {
            fenPosition = ""
        }

        return EpdInfo(fenPosition, bestMoveList, avoidMoveList, result, comment)
    }
}