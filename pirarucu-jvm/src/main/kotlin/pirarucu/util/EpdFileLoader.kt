package pirarucu.util

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveList
import pirarucu.util.factory.EpdInfoFactory
import java.io.File
import java.util.Scanner

class EpdFileLoader(location: String) {

    private val epdInfoList = mutableListOf<EpdInfo>()

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
                var moves = 0
                while (moveList.hasNext()) {
                    if (board.isLegalMove(moveList.next())) {
                        moves++
                        break
                    }
                }
                if (moves > 0) {
                    epdInfoList.add(epdInfo)
                }
            }
            println(String.format("Found %d good positions in %d possibilities.", epdInfoList.size,
                lines))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getEpdInfoList(): Collection<EpdInfo> {
        return epdInfoList
    }
}
