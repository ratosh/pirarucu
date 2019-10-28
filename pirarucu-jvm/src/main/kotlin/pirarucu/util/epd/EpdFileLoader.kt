package pirarucu.util.epd

import pirarucu.util.epd.factory.EpdInfoFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class EpdFileLoader(inputStream: InputStream) {

    private val epdInfoList = mutableMapOf<String, EpdInfo>()

    val epdList: List<EpdInfo>
        get() = ArrayList<EpdInfo>(epdInfoList.values)

    init {
        try {
            val scanner = Scanner(inputStream)

            var lines = 0
            while (scanner.hasNextLine()) {
                lines++
                val line = scanner.nextLine()
                val epdInfo = EpdInfoFactory.getEpdInfo(line)

                val foundEntry = epdInfoList[epdInfo.fenPosition]
                if (foundEntry != null) {
                    foundEntry.result += epdInfo.result
                    foundEntry.result /= 2
                    println("Found duplicate ${foundEntry.fenPosition} -> ${foundEntry.result}")
                } else {
                    epdInfoList[epdInfo.fenPosition] = epdInfo
                }
            }
            println("Found ${epdInfoList.size} good positions in $lines possibilities.")
            inputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    constructor(file: String) : this(FileInputStream(File(file)))
}
