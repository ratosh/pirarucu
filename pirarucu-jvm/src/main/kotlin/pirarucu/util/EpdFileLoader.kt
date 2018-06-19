package pirarucu.util

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
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
                val moveList = MoveList()
                val attackInfo = AttackInfo()

                val board = BoardFactory.getBoard(epdInfo.fenPosition)
                MoveGenerator.legalMoves(board, attackInfo, moveList)
                MoveGenerator.legalAttacks(board, attackInfo, moveList)
                if (moveList.hasNext()) {
                    save(epdInfo)
                }
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
