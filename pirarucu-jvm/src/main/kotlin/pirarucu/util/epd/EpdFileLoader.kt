package pirarucu.util.epd

import pirarucu.move.MoveGenerator
import pirarucu.search.History
import pirarucu.util.epd.factory.EpdInfoFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Scanner

class EpdFileLoader(inputStream: InputStream) {

    private val epdInfoList = mutableListOf<EpdInfo>()

    init {
        try {
            val scanner = Scanner(inputStream)

            var lines = 0
            while (scanner.hasNextLine()) {
                lines++
                val line = scanner.nextLine()
                val epdInfo = EpdInfoFactory.getEpdInfo(line)

                epdInfoList.add(epdInfo)
            }
            println(String.format("Found %d good positions in %d possibilities.", epdInfoList.size,
                lines))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    constructor(file: String) : this(FileInputStream(File(file)))

    fun getEpdInfoList(): List<EpdInfo> {
        return epdInfoList
    }
}
