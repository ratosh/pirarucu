package pirarucu.util

data class EpdInfo(val fenPosition: String,
    val bestMoveList: Set<String>?,
    val avoidMoveList: Set<String>?,
    private var result: Double,
    val comment: String?) {

    val averageResult: Double
        get() = result / fenCount

    var fenCount = 1

    fun merge(epdInfo: EpdInfo) {
        this.result += epdInfo.result
        this.fenCount++
    }
}
