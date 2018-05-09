package pirarucu.search

import pirarucu.game.GameConstants

object SearchInfo {

    val plyInfoList = Array(GameConstants.MAX_PLIES) { SearchPlyInfo(it) }

    fun reset() {
        for (info in plyInfoList) {
            info.clear()
        }
    }
}