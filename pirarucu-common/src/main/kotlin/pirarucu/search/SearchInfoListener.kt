package pirarucu.search

interface SearchInfoListener {
    fun searchInfo(depth: Int, elapsedTime: Long, searchInfo: SearchInfo)

    fun bestMove(move: Int)
}
