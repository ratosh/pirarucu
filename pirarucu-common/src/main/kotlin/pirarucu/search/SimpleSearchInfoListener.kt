package pirarucu.search

import pirarucu.uci.UciOutput

class SimpleSearchInfoListener : SearchInfoListener {

    override fun searchInfo(depth: Int, elapsedTime: Long, searchInfo: SearchInfo) {
        UciOutput.searchInfo(depth, elapsedTime, searchInfo.searchNodes, searchInfo)
    }

    override fun bestMove(move: Int) {
        UciOutput.bestMove(move)
    }
}