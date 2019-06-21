package pirarucu.util.epd

import pirarucu.util.epd.factory.EpdInfoFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Scanner

class EpdFileLoader(inputStream: InputStream) {

    private val epdInfoList = mutableListOf<EpdInfo>()

    val epdList : List<EpdInfo>
        get() = epdInfoList

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
            println(
                String.format("Found %d good positions in %d possibilities.", epdInfoList.size, lines)
            )
            inputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    constructor(file: String) : this(FileInputStream(File(file)))
}
