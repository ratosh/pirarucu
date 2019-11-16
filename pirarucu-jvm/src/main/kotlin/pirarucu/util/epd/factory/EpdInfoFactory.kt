package pirarucu.util.epd.factory

import pirarucu.util.epd.EpdInfo
import java.text.NumberFormat
import java.text.ParseException
import java.util.Arrays
import java.util.HashSet

object EpdInfoFactory {

    fun getEpdInfo(fenLine: String): EpdInfo {
        var tempLine = fenLine

        val fenPosition: String
        val bestMoveList: MutableSet<String>?
        val avoidMoveList: Set<String>?
        val moveScoreList: MutableMap<String, Int>?
        var result = 0.toDouble()
        var comment = ""

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
        val c1Index = tempLine.indexOf(" c1 ")
        if (c1Index >= 0) {
            tempLine = tempLine.substring(0, c1Index)
        }

        val c0Index = tempLine.indexOf(" c0 ")
        if (c0Index >= 0) {
            var c0String = tempLine.substring(c0Index + 3, tempLine.length).replace("\"", "").replace(" ", "")
            val c0End = c0String.indexOf(";")
            val p1 = tempLine.substring(0, c0Index)
            val p2 = c0String.substring(c0End, c0String.length)

            c0String = c0String.substring(0, c0End)
            tempLine = p1 + p2

            val moveList = c0String.split(",")
            moveScoreList = mutableMapOf()
            for (entry in moveList) {
                val split = entry.split("=")
                moveScoreList[split[0]] = Integer.parseInt(split[1])
            }
        } else {
            moveScoreList = null
        }

        val commentIndex = tempLine.indexOf(";")
        if (commentIndex >= 0) {
            comment = tempLine.substring(commentIndex + 1, tempLine.length).replace(";", "")
            tempLine = tempLine.substring(0, commentIndex)
        }

        val bmIndex = tempLine.indexOf(" bm ")

        if (bmIndex >= 0) {
            val bmString = tempLine.substring(bmIndex + 4, tempLine.length)
            tempLine = tempLine.substring(0, bmIndex)

            bestMoveList = HashSet<String>(Arrays.asList(*bmString.split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()))
        } else {
            bestMoveList = null
        }

        val amIndex = tempLine.indexOf("am ")

        if (amIndex >= 0) {
            val amString = tempLine.substring(amIndex + 4, tempLine.length)
            tempLine = tempLine.substring(0, amIndex)

            avoidMoveList = HashSet<String>(Arrays.asList(*amString.split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()))
        } else {
            avoidMoveList = null
        }
        fenPosition = if (!tempLine.isEmpty()) {
            tempLine.trim { it <= ' ' }
        } else {
            ""
        }

        return EpdInfo(fenPosition, bestMoveList, avoidMoveList, moveScoreList, result, comment)
    }
}