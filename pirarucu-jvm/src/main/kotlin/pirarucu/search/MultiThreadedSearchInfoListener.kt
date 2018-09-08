package pirarucu.search

import pirarucu.uci.UciOutput

class MultiThreadedSearchInfoListener : SearchInfoListener {

    override fun searchInfo(depth: Int, elapsedTime: Long, searchInfo: SearchInfo) {
        UciOutput.searchInfo(depth, elapsedTime, MultiThreadedSearch.countNodes(), searchInfo)
    }

    override fun bestMove(move: Int) {
        UciOutput.bestMove(move)
    }
}
