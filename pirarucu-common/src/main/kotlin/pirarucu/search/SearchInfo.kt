package pirarucu.search

import pirarucu.game.GameConstants

object SearchInfo {

    val plyInfoList = Array(GameConstants.MAX_PLIES + 4) { SearchPlyInfo(it) }

    fun reset() {
        for (info in plyInfoList) {
            info.clear()
        }
    }
}