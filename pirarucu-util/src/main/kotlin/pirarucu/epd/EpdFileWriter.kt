package pirarucu.epd

import pirarucu.tuning.ErrorUtil
import pirarucu.util.epd.EpdInfo
import java.io.File

class EpdFileWriter(private val file: File) {

    constructor(file: String) : this(File(file))

    fun flush(epdInfoList: List<EpdInfo>) {
        if (epdInfoList.isEmpty()) {
            return
        }
        try {
            val tempFile = createTempFile()
            tempFile.printWriter().use { writer ->
                file.forEachLine { line ->
                    val epdInfo = findPosition(line, epdInfoList)
                    writer.println(
                        when {
                            epdInfo != null -> "${epdInfo.fenPosition} c9 ${Math.round(ErrorUtil.calculateSigmoid(epdInfo.eval))}.0"
                            else -> line
                        }
                    )
                }
            }
            check(file.delete() && tempFile.renameTo(file)) { "failed to replace file" }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun findPosition(line: String, epdInfoList: List<EpdInfo>): EpdInfo?? {
        epdInfoList.forEach {
            if (line.contains(it.fenPosition)) {
                return it
            }
        }
        return null
    }
}