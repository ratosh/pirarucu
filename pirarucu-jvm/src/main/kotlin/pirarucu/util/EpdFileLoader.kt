package pirarucu.util

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.move.MoveGenerator
import pirarucu.move.OrderedMoveList
import pirarucu.search.History
import pirarucu.util.factory.EpdInfoFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Scanner

class EpdFileLoader(inputStream: InputStream) {

    private val epdInfoList = mutableListOf<EpdInfo>()
    private val moveGenerator = MoveGenerator(History())

    init {
        try {
            val scanner = Scanner(inputStream)

            var lines = 0
            val board = Board()
            while (scanner.hasNextLine()) {
                lines++
                val line = scanner.nextLine()
                val epdInfo = EpdInfoFactory.getEpdInfo(line)
                val moveList = OrderedMoveList()
                val attackInfo = AttackInfo()

                BoardFactory.setBoard(epdInfo.fenPosition, board)
                moveGenerator.legalMoves(board, attackInfo, moveList)
                moveGenerator.legalAttacks(board, attackInfo, moveList)
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

    constructor(file: String) : this(FileInputStream(File(file)))

    fun getEpdInfoList(): Collection<EpdInfo> {
        return epdInfoList
    }
}
