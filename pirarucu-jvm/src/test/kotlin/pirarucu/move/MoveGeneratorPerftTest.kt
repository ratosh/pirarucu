package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Piece
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveGeneratorPerftTest {
    private val attackInfo = AttackInfo()

    @Ignore
    @Test
    fun testInitialPosition() {
        val moveInfoList = Array(6) { MoveInfo() }
        val board = BoardFactory.getBoard()

        recursive(board, moveInfoList, 6)

        assertEquals(20, moveInfoList[0].moveCount)
        assertEquals(0, moveInfoList[0].captures)
        assertEquals(0, moveInfoList[0].passantCaptures)
        assertEquals(0, moveInfoList[0].castles)
        assertEquals(0, moveInfoList[0].promotions)
        assertEquals(0, moveInfoList[0].checks)

        assertEquals(400, moveInfoList[1].moveCount)
        assertEquals(0, moveInfoList[1].captures)
        assertEquals(0, moveInfoList[1].passantCaptures)
        assertEquals(0, moveInfoList[1].castles)
        assertEquals(0, moveInfoList[1].promotions)
        assertEquals(0, moveInfoList[1].checks)

        assertEquals(8902, moveInfoList[2].moveCount)
        assertEquals(34, moveInfoList[2].captures)
        assertEquals(0, moveInfoList[2].passantCaptures)
        assertEquals(0, moveInfoList[2].castles)
        assertEquals(0, moveInfoList[2].promotions)
        assertEquals(12, moveInfoList[2].checks)

        assertEquals(197281, moveInfoList[3].moveCount)
        assertEquals(1576, moveInfoList[3].captures)
        assertEquals(0, moveInfoList[3].passantCaptures)
        assertEquals(0, moveInfoList[3].castles)
        assertEquals(0, moveInfoList[3].promotions)
        assertEquals(469, moveInfoList[3].checks)

        assertEquals(4865609, moveInfoList[4].moveCount)
        assertEquals(82719, moveInfoList[4].captures)
        assertEquals(258, moveInfoList[4].passantCaptures)
        assertEquals(0, moveInfoList[4].castles)
        assertEquals(0, moveInfoList[4].promotions)
        assertEquals(27351, moveInfoList[4].checks)

        assertEquals(119060324, moveInfoList[5].moveCount)
        assertEquals(2812008, moveInfoList[5].captures)
        assertEquals(5248, moveInfoList[5].passantCaptures)
        assertEquals(0, moveInfoList[5].castles)
        assertEquals(0, moveInfoList[5].promotions)
        assertEquals(809099, moveInfoList[5].checks)
    }

    @Ignore
    @Test
    fun testKiwiPete() {
        val moveInfoList = Array(5) { MoveInfo() }
        val board = BoardFactory.getBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -")

        recursive(board, moveInfoList, 5)

        assertEquals(48, moveInfoList[0].moveCount)
        assertEquals(8, moveInfoList[0].captures)
        assertEquals(0, moveInfoList[0].passantCaptures)
        assertEquals(2, moveInfoList[0].castles)
        assertEquals(0, moveInfoList[0].promotions)
        assertEquals(0, moveInfoList[0].checks)

        assertEquals(2039, moveInfoList[1].moveCount)
        assertEquals(351, moveInfoList[1].captures)
        assertEquals(1, moveInfoList[1].passantCaptures)
        assertEquals(91, moveInfoList[1].castles)
        assertEquals(0, moveInfoList[1].promotions)
        assertEquals(3, moveInfoList[1].checks)

        assertEquals(97862, moveInfoList[2].moveCount)
        assertEquals(17102, moveInfoList[2].captures)
        assertEquals(45, moveInfoList[2].passantCaptures)
        assertEquals(3162, moveInfoList[2].castles)
        assertEquals(0, moveInfoList[2].promotions)
        assertEquals(993, moveInfoList[2].checks)

        assertEquals(4085603, moveInfoList[3].moveCount)
        assertEquals(757163, moveInfoList[3].captures)
        assertEquals(1929, moveInfoList[3].passantCaptures)
        assertEquals(128013, moveInfoList[3].castles)
        assertEquals(15172, moveInfoList[3].promotions)
        assertEquals(25523, moveInfoList[3].checks)

        assertEquals(193690690, moveInfoList[4].moveCount)
        assertEquals(35043416, moveInfoList[4].captures)
        assertEquals(73365, moveInfoList[4].passantCaptures)
        assertEquals(4993637, moveInfoList[4].castles)
        assertEquals(8392, moveInfoList[4].promotions)
        assertEquals(3309887, moveInfoList[4].checks)
    }

    // Not working
    @Ignore
    @Test
    fun testPosition3() {
        val moveInfoList = Array(7) { MoveInfo() }
        val board = BoardFactory.getBoard("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")

        divide(board, 2)
        recursive(board, moveInfoList, 2)

        assertEquals(14, moveInfoList[0].moveCount)
        assertEquals(1, moveInfoList[0].captures)
        assertEquals(0, moveInfoList[0].passantCaptures)
        assertEquals(0, moveInfoList[0].castles)
        assertEquals(0, moveInfoList[0].promotions)
        assertEquals(2, moveInfoList[0].checks)

        assertEquals(191, moveInfoList[1].moveCount)
        assertEquals(14, moveInfoList[1].captures)
        assertEquals(0, moveInfoList[1].passantCaptures)
        assertEquals(0, moveInfoList[1].castles)
        assertEquals(0, moveInfoList[1].promotions)
        assertEquals(10, moveInfoList[1].checks)

        assertEquals(2812, moveInfoList[2].moveCount)
        assertEquals(209, moveInfoList[2].captures)
        assertEquals(2, moveInfoList[2].passantCaptures)
        assertEquals(0, moveInfoList[2].castles)
        assertEquals(0, moveInfoList[2].promotions)
        assertEquals(267, moveInfoList[2].checks)

        assertEquals(43238, moveInfoList[3].moveCount)
        assertEquals(3348, moveInfoList[3].captures)
        assertEquals(123, moveInfoList[3].passantCaptures)
        assertEquals(0, moveInfoList[3].castles)
        assertEquals(0, moveInfoList[3].promotions)
        assertEquals(1680, moveInfoList[3].checks)

        assertEquals(674624, moveInfoList[4].moveCount)
        assertEquals(52051, moveInfoList[4].captures)
        assertEquals(1165, moveInfoList[4].passantCaptures)
        assertEquals(0, moveInfoList[4].castles)
        assertEquals(0, moveInfoList[4].promotions)
        assertEquals(52950, moveInfoList[4].checks)

        assertEquals(11030083, moveInfoList[5].moveCount)
        assertEquals(940350, moveInfoList[5].captures)
        assertEquals(33325, moveInfoList[5].passantCaptures)
        assertEquals(0, moveInfoList[5].castles)
        assertEquals(7552, moveInfoList[5].promotions)
        assertEquals(452473, moveInfoList[5].checks)

        assertEquals(178633661, moveInfoList[6].moveCount)
        assertEquals(14519036, moveInfoList[6].captures)
        assertEquals(294874, moveInfoList[6].passantCaptures)
        assertEquals(0, moveInfoList[6].castles)
        assertEquals(140024, moveInfoList[6].promotions)
        assertEquals(12797406, moveInfoList[6].checks)
    }

    @Ignore
    @Test
    fun testPosition4() {
        val moveInfoList = Array(6) { MoveInfo() }
        val board = BoardFactory
            .getBoard("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")

        recursive(board, moveInfoList, 6)

        assertEquals(6, moveInfoList[0].moveCount)
        assertEquals(0, moveInfoList[0].captures)
        assertEquals(0, moveInfoList[0].passantCaptures)
        assertEquals(0, moveInfoList[0].castles)
        assertEquals(0, moveInfoList[0].promotions)
        assertEquals(0, moveInfoList[0].checks)

        assertEquals(264, moveInfoList[1].moveCount)
        assertEquals(87, moveInfoList[1].captures)
        assertEquals(0, moveInfoList[1].passantCaptures)
        assertEquals(6, moveInfoList[1].castles)
        assertEquals(48, moveInfoList[1].promotions)
        assertEquals(10, moveInfoList[1].checks)

        assertEquals(9467, moveInfoList[2].moveCount)
        assertEquals(1021, moveInfoList[2].captures)
        assertEquals(4, moveInfoList[2].passantCaptures)
        assertEquals(0, moveInfoList[2].castles)
        assertEquals(120, moveInfoList[2].promotions)
        assertEquals(38, moveInfoList[2].checks)

        assertEquals(422333, moveInfoList[3].moveCount)
        assertEquals(131393, moveInfoList[3].captures)
        assertEquals(0, moveInfoList[3].passantCaptures)
        assertEquals(7795, moveInfoList[3].castles)
        assertEquals(60032, moveInfoList[3].promotions)
        assertEquals(15492, moveInfoList[3].checks)

        assertEquals(15833292, moveInfoList[4].moveCount)
        assertEquals(2046173, moveInfoList[4].captures)
        assertEquals(6512, moveInfoList[4].passantCaptures)
        assertEquals(0, moveInfoList[4].castles)
        assertEquals(329464, moveInfoList[4].promotions)
        assertEquals(200568, moveInfoList[4].checks)

        assertEquals(706045033, moveInfoList[5].moveCount)
        assertEquals(210369132, moveInfoList[5].captures)
        assertEquals(212, moveInfoList[5].passantCaptures)
        assertEquals(10882006, moveInfoList[5].castles)
        assertEquals(81102984, moveInfoList[5].promotions)
        assertEquals(26973664, moveInfoList[5].checks)
    }

    @Ignore
    @Test
    fun testPosition5() {
        val moveInfoList = Array(5) { MoveInfo() }
        val board = BoardFactory.getBoard("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ -")

        recursive(board, moveInfoList, 5)

        assertEquals(44, moveInfoList[0].moveCount)
        assertEquals(1486, moveInfoList[1].moveCount)
        assertEquals(62379, moveInfoList[2].moveCount)
        assertEquals(2103487, moveInfoList[3].moveCount)
        assertEquals(89941194, moveInfoList[4].moveCount)
    }

    @Ignore
    @Test
    fun testPosition6() {
        val moveInfoList = Array(5) { MoveInfo() }
        val board = BoardFactory.getBoard(
            "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - -")

        recursive(board, moveInfoList, 5)

        assertEquals(46, moveInfoList[0].moveCount)
        assertEquals(2079, moveInfoList[1].moveCount)
        assertEquals(89890, moveInfoList[2].moveCount)
        assertEquals(3894594, moveInfoList[3].moveCount)
        assertEquals(164075551, moveInfoList[4].moveCount)
    }

    fun divide(board: Board,
               wantedDepth: Int) {
        val moveList = MoveList()
        moveList.startPly()
        MoveGenerator.legalAttacks(board, attackInfo, moveList)
        MoveGenerator.legalMoves(board, attackInfo, moveList)
        while (moveList.hasNext()) {
            val moveInfoArray = Array(wantedDepth) { MoveInfo() }
            val move = moveList.next()
            if (!board.isLegalMove(move)) {
                continue
            }
            board.doMove(move)
            recursive(board, moveList, moveInfoArray, 1, wantedDepth - 1)
            println("Move " + Move.toString(move))
            if (wantedDepth > 1) {
                println(getString(moveInfoArray))
            }
            board.undoMove(move)
        }
        moveList.endPly()
    }

    private fun recursive(board: Board,
                          moveInfo: Array<MoveInfo>,
                          wantedDepth: Int): Int {
        return recursive(board, MoveList(), moveInfo, 0, wantedDepth - 1)
    }

    private fun recursive(board: Board,
                          moveList: MoveList,
                          moveInfoList: Array<MoveInfo>,
                          depth: Int,
                          wantedDepth: Int): Int {

        if (depth > wantedDepth) {
            return 1
        }

        moveList.startPly()
        MoveGenerator.legalAttacks(board, attackInfo, moveList)
        MoveGenerator.legalMoves(board, attackInfo, moveList)

        var totalMove = 0
        while (moveList.hasNext()) {
            val move = moveList.next()
            if (!board.isLegalMove(move)) {
                println("Skipping " + Move.toString(move))
                continue
            }
            moveInfoList[depth].moveCount++

            board.doMove(move)

            val moveType = Move.getMoveType(move)
            if (board.capturedPiece != Piece.NONE) {
                moveInfoList[depth].captures++
            }
            if (moveType == MoveType.TYPE_PASSANT) {
                moveInfoList[depth].passantCaptures++
            }
            if (MoveType.isCastling(moveType)) {
                moveInfoList[depth].castles++
            }
            if (MoveType.isPromotion(moveType)) {
                moveInfoList[depth].promotions++
            }
            if (board.basicEvalInfo.checkBitboard != Bitboard.EMPTY) {
                moveInfoList[depth].checks++
            }

            val moveCount = if (depth < wantedDepth) {
                recursive(board, moveList, moveInfoList, depth + 1, wantedDepth)
            } else {
                1
            }

            totalMove += moveCount
            board.undoMove(move)
        }

        moveList.endPly()
        return totalMove
    }

    private fun getString(moveInfoList: Array<MoveInfo>): String {
        val buffer = StringBuilder()
        for (index in moveInfoList.indices) {

            buffer.append("depth ").append(index).append(" ").append(moveInfoList[index].toString())
            buffer.append("\n")
        }
        return buffer.toString()
    }

    data class MoveInfo(var moveCount: Long = 0, var captures: Long = 0, var passantCaptures: Long = 0,
                        var castles: Long = 0, var promotions: Long = 0, var checks: Long = 0) {

        override fun toString(): String {
            return ("MoveInfo{"
                + "moveCount=" + moveCount
                + ", captures=" + captures
                + ", passantCaptures=" + passantCaptures
                + ", castles=" + castles
                + ", promotions=" + promotions
                + ", checks=" + checks
                + '}'.toString())
        }
    }
}