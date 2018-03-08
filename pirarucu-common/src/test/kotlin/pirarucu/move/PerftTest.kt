package pirarucu.move

import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.factory.BoardFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class PerftTest {

    @Test
    fun testInitialPosition() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory.getBoard()

        recursive(board, moveInfoList, 5)

        assertEquals(moveInfoList[0].moveCount, 20)
        assertEquals(moveInfoList[0].captures, 0)
        assertEquals(moveInfoList[0].passantCaptures, 0)
        assertEquals(moveInfoList[0].castles, 0)
        assertEquals(moveInfoList[0].promotions, 0)
        assertEquals(moveInfoList[0].checks, 0)

        assertEquals(moveInfoList[1].moveCount, 400)
        assertEquals(moveInfoList[1].captures, 0)
        assertEquals(moveInfoList[1].passantCaptures, 0)
        assertEquals(moveInfoList[1].castles, 0)
        assertEquals(moveInfoList[1].promotions, 0)
        assertEquals(moveInfoList[1].checks, 0)

        assertEquals(moveInfoList[2].moveCount, 8902)
        assertEquals(moveInfoList[2].captures, 34)
        assertEquals(moveInfoList[2].passantCaptures, 0)
        assertEquals(moveInfoList[2].castles, 0)
        assertEquals(moveInfoList[2].promotions, 0)
        assertEquals(moveInfoList[2].checks, 12)

        assertEquals(moveInfoList[3].moveCount, 197281)
        assertEquals(moveInfoList[3].captures, 1576)
        assertEquals(moveInfoList[3].passantCaptures, 0)
        assertEquals(moveInfoList[3].castles, 0)
        assertEquals(moveInfoList[3].promotions, 0)
        assertEquals(moveInfoList[3].checks, 469)

        assertEquals(moveInfoList[4].moveCount, 4865609)
        assertEquals(moveInfoList[4].captures, 82719)
        assertEquals(moveInfoList[4].passantCaptures, 258)
        assertEquals(moveInfoList[4].castles, 0)
        assertEquals(moveInfoList[4].promotions, 0)
        assertEquals(moveInfoList[4].checks, 27351)
    }

    @Test
    fun testKiwiPete() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory.getBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -")

        recursive(board, moveInfoList, 4)

        assertEquals(moveInfoList[0].moveCount, 48)
        assertEquals(moveInfoList[0].captures, 8)
        assertEquals(moveInfoList[0].passantCaptures, 0)
        assertEquals(moveInfoList[0].castles, 2)
        assertEquals(moveInfoList[0].promotions, 0)
        assertEquals(moveInfoList[0].checks, 0)

        assertEquals(moveInfoList[1].moveCount, 2039)
        assertEquals(moveInfoList[1].captures, 351)
        assertEquals(moveInfoList[1].passantCaptures, 1)
        assertEquals(moveInfoList[1].castles, 91)
        assertEquals(moveInfoList[1].promotions, 0)
        assertEquals(moveInfoList[1].checks, 3)

        assertEquals(moveInfoList[2].moveCount, 97862)
        assertEquals(moveInfoList[2].captures, 17102)
        assertEquals(moveInfoList[2].passantCaptures, 45)
        assertEquals(moveInfoList[2].castles, 3162)
        assertEquals(moveInfoList[2].promotions, 0)
        assertEquals(moveInfoList[2].checks, 993)

        assertEquals(moveInfoList[3].moveCount, 4085603)
        assertEquals(moveInfoList[3].captures, 757163)
        assertEquals(moveInfoList[3].passantCaptures, 1929)
        assertEquals(moveInfoList[3].castles, 128013)
        assertEquals(moveInfoList[3].promotions, 15172)
        assertEquals(moveInfoList[3].checks, 25523)
    }

    @Test
    fun testPosition3() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory.getBoard("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -")

        recursive(board, moveInfoList, 6)

        assertEquals(moveInfoList[0].moveCount, 14)
        assertEquals(moveInfoList[0].captures, 1)
        assertEquals(moveInfoList[0].passantCaptures, 0)
        assertEquals(moveInfoList[0].castles, 0)
        assertEquals(moveInfoList[0].promotions, 0)
        assertEquals(moveInfoList[0].checks, 2)

        assertEquals(moveInfoList[1].moveCount, 191)
        assertEquals(moveInfoList[1].captures, 14)
        assertEquals(moveInfoList[1].passantCaptures, 0)
        assertEquals(moveInfoList[1].castles, 0)
        assertEquals(moveInfoList[1].promotions, 0)
        assertEquals(moveInfoList[1].checks, 10)

        assertEquals(moveInfoList[2].moveCount, 2812)
        assertEquals(moveInfoList[2].captures, 209)
        assertEquals(moveInfoList[2].passantCaptures, 2)
        assertEquals(moveInfoList[2].castles, 0)
        assertEquals(moveInfoList[2].promotions, 0)
        assertEquals(moveInfoList[2].checks, 267)

        assertEquals(moveInfoList[3].moveCount, 43238)
        assertEquals(moveInfoList[3].captures, 3348)
        assertEquals(moveInfoList[3].passantCaptures, 123)
        assertEquals(moveInfoList[3].castles, 0)
        assertEquals(moveInfoList[3].promotions, 0)
        assertEquals(moveInfoList[3].checks, 1680)

        assertEquals(moveInfoList[4].moveCount, 674624)
        assertEquals(moveInfoList[4].captures, 52051)
        assertEquals(moveInfoList[4].passantCaptures, 1165)
        assertEquals(moveInfoList[4].castles, 0)
        assertEquals(moveInfoList[4].promotions, 0)
        assertEquals(moveInfoList[4].checks, 52950)

        assertEquals(moveInfoList[5].moveCount, 11030083)
        assertEquals(moveInfoList[5].captures, 940350)
        assertEquals(moveInfoList[5].passantCaptures, 33325)
        assertEquals(moveInfoList[5].castles, 0)
        assertEquals(moveInfoList[5].promotions, 7552)
        assertEquals(moveInfoList[5].checks, 452473)
    }

    @Test
    fun testPosition4() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory
            .getBoard("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq -")

        recursive(board, moveInfoList, 5)

        assertEquals(moveInfoList[0].moveCount, 6)
        assertEquals(moveInfoList[0].captures, 0)
        assertEquals(moveInfoList[0].passantCaptures, 0)
        assertEquals(moveInfoList[0].castles, 0)
        assertEquals(moveInfoList[0].promotions, 0)
        assertEquals(moveInfoList[0].checks, 0)

        assertEquals(moveInfoList[1].moveCount, 264)
        assertEquals(moveInfoList[1].captures, 87)
        assertEquals(moveInfoList[1].passantCaptures, 0)
        assertEquals(moveInfoList[1].castles, 6)
        assertEquals(moveInfoList[1].promotions, 48)
        assertEquals(moveInfoList[1].checks, 10)

        assertEquals(moveInfoList[2].moveCount, 9467)
        assertEquals(moveInfoList[2].captures, 1021)
        assertEquals(moveInfoList[2].passantCaptures, 4)
        assertEquals(moveInfoList[2].castles, 0)
        assertEquals(moveInfoList[2].promotions, 120)
        assertEquals(moveInfoList[2].checks, 38)

        assertEquals(moveInfoList[3].moveCount, 422333)
        assertEquals(moveInfoList[3].captures, 131393)
        assertEquals(moveInfoList[3].passantCaptures, 0)
        assertEquals(moveInfoList[3].castles, 7795)
        assertEquals(moveInfoList[3].promotions, 60032)
        assertEquals(moveInfoList[3].checks, 15492)

        assertEquals(moveInfoList[4].moveCount, 15833292)
        assertEquals(moveInfoList[4].captures, 2046173)
        assertEquals(moveInfoList[4].passantCaptures, 6512)
        assertEquals(moveInfoList[4].castles, 0)
        assertEquals(moveInfoList[4].promotions, 329464)
        assertEquals(moveInfoList[4].checks, 200568)
    }

    @Test
    fun testPosition5() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory.getBoard("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ -")

        recursive(board, moveInfoList, 4)

        assertEquals(moveInfoList[0].moveCount, 44)
        assertEquals(moveInfoList[1].moveCount, 1486)
        assertEquals(moveInfoList[2].moveCount, 62379)
        assertEquals(moveInfoList[3].moveCount, 2103487)
        //assertEquals(moveInfoList[4].moveCount, 89941194)
    }

    @Test
    fun testPosition6() {
        val moveInfoList = arrayOf(MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo(), MoveInfo())
        val board = BoardFactory.getBoard(
            "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - -")

        recursive(board, moveInfoList, 4)

        assertEquals(moveInfoList[0].moveCount, 46)
        assertEquals(moveInfoList[1].moveCount, 2079)
        assertEquals(moveInfoList[2].moveCount, 89890)
        assertEquals(moveInfoList[3].moveCount, 3894594)
        //assertEquals(moveInfoList[4].moveCount, 164075551)
    }

    fun getString(rootNode: MoveNode, wantedDepth: Int, depth: Int = 0): String {
        val result = StringBuilder()
        for (child in rootNode.children) {
            result.append("move " + Move.toString(child.move) + " | " + child.children.size + " | " + child.moveCount + "\n")
            if (wantedDepth > depth) {
                result.append(getString(child, wantedDepth, depth + 1) + "\n")
            }
        }
        return result.toString()
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

        moveList.startPly()
        MoveGenerator.legalAttacks(board, moveList)
        MoveGenerator.legalMoves(board, moveList)

        var totalMove = 0
        while (moveList.hasNext()) {
            val move = moveList.next()
            moveInfoList[depth].moveCount++

            if (!board.possibleMove(move)) {
                continue
            }

            board.doMove(move)

            val moveType = Move.getMoveType(move)
            if (Move.isCapture(move)) {
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
            if (board.basicEvalInfo.checkBitboard[Color.WHITE] or
                board.basicEvalInfo.checkBitboard[Color.BLACK] != 0L) {
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

    data class MoveNode(val move: Int) {
        val children = mutableListOf<MoveNode>()
        var moveCount = 0
    }

    private class MoveInfo {

        var moveCount: Int = 0
        var captures: Int = 0
        var passantCaptures: Int = 0
        var castles: Int = 0
        var promotions: Int = 0
        var checks: Int = 0

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