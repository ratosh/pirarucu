package pirarucu.util

import pirarucu.util.factory.EpdInfoFactory
import java.io.File
import java.util.HashMap
import java.util.Scanner

class EpdFileLoader(location: String) {

    private val epdInfoList = HashMap<String, EpdInfo>()

    init {
        try {
            val scanner = Scanner(File(location))

            var lines = 0
            while (scanner.hasNextLine()) {
                lines++
                val line = scanner.nextLine()
                val epdInfo = EpdInfoFactory.getEpdInfo(line)
                save(epdInfo)
            }
            println(String.format("Found %d good positions in %d possibilities.", epdInfoList.size,
                lines))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun save(epdInfo: EpdInfo) {
        if (epdInfoList.containsKey(epdInfo.fenPosition)) {
            epdInfoList[epdInfo.fenPosition]!!.merge(epdInfo)
        } else {
            epdInfoList[epdInfo.fenPosition] = epdInfo
        }
    }

    fun getEpdInfoList(): Collection<EpdInfo> {
        return epdInfoList.values
    }
}
