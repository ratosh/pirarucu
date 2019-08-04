package pirarucu.epd

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
                epdInfoList.forEach {
                    writer.println("${it.fenPosition} c9 ${it.result}")
                }
            }
            check((!file.exists() || file.delete()) && tempFile.renameTo(file)) { "failed to replace file" }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}