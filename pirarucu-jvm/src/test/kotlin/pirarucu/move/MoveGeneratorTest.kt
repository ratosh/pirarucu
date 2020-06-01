package pirarucu.move

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.search.History
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveGeneratorTest {

    val attackInfo = AttackInfo()
    val moveGenerator = MoveGenerator(History())

    private fun countAll(board: Board): Int {
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            val move = moveList.next()
            if (board.isLegalMove(move)) {
                println("move ${Move.toString(move)}")
                legalMoves++
            } else {
                println("illegal ${Move.toString(move)}")
            }
        }
        return legalMoves;
    }

    @Test
    fun pawnMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3P4/8/8/5K2 w - -")
        assertEquals(6, countAll(board))
    }

    @Test
    fun pawnDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/3P4/5K2 w - -")
        assertEquals(7, countAll(board))
    }

    @Test
    fun pawnBlockedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3p4/3P4/8/8/5K2 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun pawnBlockedDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/3p4/3P4/5K2 w - -")
        assertEquals(4, countAll(board))
    }

    @Test
    fun pawnDoubleBlockedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3p4/8/3P4/5K2 w - -")
        assertEquals(6, countAll(board))
    }

    @Test
    fun pawnPinned() {
        val board = BoardFactory.getBoard("5k2/8/8/1b6/8/3P4/8/5K2 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun pawnBlockingCheck() {
        val board = BoardFactory.getBoard("5k2/8/8/8/2b5/8/3P4/5K2 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun pawnDoubleMoveBlockingCheck() {
        val board = BoardFactory.getBoard("5k2/8/8/2b5/8/8/3P4/6K1 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun castling() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/R3K3 w Q -")
        assertEquals(16, countAll(board))
    }

    @Test
    fun castlingBlockedByCheck() {
        val board = BoardFactory.getBoard("3r1k2/8/8/8/8/8/8/R3K3 w Q -")
        assertEquals(13, countAll(board))
    }

    @Test
    fun promotion() {
        val board = BoardFactory.getBoard("5k2/2P5/8/8/8/8/8/5K2 w - -")
        assertEquals(9, countAll(board))
    }

    @Test
    fun promotionBlocked() {
        val board = BoardFactory.getBoard("2r2k2/2P5/8/8/8/8/8/5K2 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun promotionBlockedByCheck() {
        val board = BoardFactory.getBoard("5k2/3P4/1q6/8/8/8/5K2/8 w - -")
        assertEquals(6, countAll(board))
    }

    @Test
    fun promotionCaptureChecker() {
        val board = BoardFactory.getBoard("1q3K2/2P5/8/8/5k2/8/8/8 w - -")
        assertEquals(11, countAll(board))
    }

    @Test
    fun promotionCapturePinner() {
        val board = BoardFactory.getBoard("1b3k2/2P5/8/8/5K2/8/8/8 w - -")
        assertEquals(12, countAll(board))
    }

    @Test
    fun knightMoves() {
        val board = BoardFactory.getBoard("5k2/8/8/8/2N5/8/8/5K2 w - -")
        assertEquals(13, countAll(board))
    }

    @Test
    fun knightCaptures() {
        val board = BoardFactory.getBoard("3r1k2/8/2N5/8/8/8/8/5K2 w - -")
        assertEquals(13, countAll(board))
    }

    @Test
    fun knightPinned() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6N1/7K w - -")
        assertEquals(2, countAll(board))
    }

    @Test
    fun bishopMoves() {
        val board = BoardFactory.getBoard("5k2/8/8/3B4/8/8/8/5K2 w - -")
        assertEquals(18, countAll(board))
    }

    @Test
    fun bishopCaptures() {
        val board = BoardFactory.getBoard("3r1k2/8/1B6/8/8/8/8/5K2 w - -")
        assertEquals(14, countAll(board))
    }

    @Test
    fun bishopPinned() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6B1/7K w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun rookMoves() {
        val board = BoardFactory.getBoard("5k2/8/8/3R4/8/8/8/5K2 w - -")
        assertEquals(19, countAll(board))
    }

    @Test
    fun rookCaptures() {
        val board = BoardFactory.getBoard("3r1k2/8/8/3R4/8/8/8/5K2 w - -")
        assertEquals(19, countAll(board))
    }

    @Test
    fun rookPinned() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5R2/5K2 w - -")
        assertEquals(9, countAll(board))
    }

    @Test
    fun queenMoves() {
        val board = BoardFactory.getBoard("5k2/8/8/3Q4/8/8/8/5K2 w - -")
        assertEquals(32, countAll(board))
    }

    @Test
    fun queenPinned() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5Q2/5K2 w - -")
        assertEquals(9, countAll(board))
    }

    @Test
    fun kingMoves() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/5K2 w - -")
        assertEquals(5, countAll(board))
    }

    @Test
    fun kingCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/4qK2 w - -")
        assertEquals(2, countAll(board))
    }

    @Test
    fun kingSafeMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/8/5K2 w - -")
        assertEquals(4, countAll(board))
    }

    @Test
    fun epCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/3Pp3/8/8/8/4K3 w - e6")
        assertEquals(7, countAll(board))
    }

    @Test
    fun epPinCapture() {
        val board = BoardFactory.getBoard("5kb1/8/8/3Pp3/2K5/8/8/8 w - e6")
        assertEquals(7, countAll(board))
    }

    @Test
    fun epPinnedCapture() {
        val board = BoardFactory.getBoard("5k2/6b1/8/3Pp3/8/2K5/8/8 w - e6")
        assertEquals(8, countAll(board))
    }

    @Test
    fun epCheckCapture() {
        val board = BoardFactory.getBoard("5k2/6b1/8/3Pp3/5K2/8/8/8 w - e6")
        assertEquals(8, countAll(board))
    }

    @Test
    fun p4_1() {
        val board = BoardFactory.getBoard("r1bqkbnr/pppppppp/2n5/8/Q7/2P5/PP1PPPPP/RNB1KBNR b KQkq -")
        assertEquals(22, countAll(board))
    }

    @Test
    fun p4_2() {
        val board = BoardFactory.getBoard("rnbqkbnr/1ppppppp/8/p7/1P6/7P/P1PPPPP1/RNBQKBNR b KQkq -")
        assertEquals(22, countAll(board))
    }

    @Test
    fun p4_3() {
        val board = BoardFactory.getBoard("rnbqkbnr/ppp1pppp/3p4/8/Q7/2P5/PP1PPPPP/RNB1KBNR b KQkq -")
        assertEquals(6, countAll(board))
    }

    @Test
    fun p5_1() {
        val board = BoardFactory.getBoard("rnbqkbnr/1ppppp1p/6p1/p7/8/1P6/PBPPPPPP/RN1QKBNR w KQkq -")
        assertEquals(28, countAll(board))
    }

    @Test
    fun p438() {
        val board = BoardFactory.getBoard("1rb3rk/1p6/n7/Bqbpp1p1/P1p3Qp/5NPK/2P3nP/RN3R2 w - -")
        assertEquals(33, countAll(board))
    }

    @Test
    fun p2762() {
        val board = BoardFactory.getBoard("8/p4Q2/P6k/2P5/8/1P6/1r4RK/r7 w - -")
        assertEquals(30, countAll(board))
    }

    @Test
    fun p916() {
        val board = BoardFactory.getBoard("r3qbn1/4k1p1/B4n2/1p3p1r/p1ppPP1P/P1P1Q3/1P6/RNBbK2R w KQ -")
        assertEquals(29, countAll(board))
    }

    @Test
    fun p2535() {
        val board = BoardFactory.getBoard("rnbqk1n1/pp1pppb1/2p5/6p1/8/NP1P4/P1PQPPP1/R3KBNr w Qq -")
        assertEquals(28, countAll(board))
    }

    @Test
    fun p683() {
        val board = BoardFactory.getBoard("5b2/8/rp3qN1/p1k2p1r/PpbpP3/7P/2QP1PB1/RN2K2R w KQ -")
        assertEquals(30, countAll(board))
    }

    @Test
    fun p4136() {
        val board = BoardFactory.getBoard("2b1k3/r2qbpr1/n1p1p2n/1p1p4/Pp5P/2PP1P1B/4P3/RNBQK2R w KQ -")
        assertEquals(34, countAll(board))
    }

    @Test
    fun p5() {
        val board = BoardFactory.getBoard("rn2kbnr/p1q1ppp1/1ppp3p/8/4B1P1/2P5/PPQPPP2/RNB1K1NR b KQkq -")
        assertEquals(22, countAll(board))
    }

    @Test
    fun p94() {
        val board = BoardFactory.getBoard("2b1kbnB/rp1qp3/3p3p/2pP1pp1/pnP3P1/PP2P2P/4QP2/RN2KBNR w KQ c6")
        assertEquals(29, countAll(board))
    }

    @Test
    fun p4() {
        val board = BoardFactory.getBoard("rnb1kbnr/p1q1pppp/1ppp4/8/4B1P1/2P5/PPQPPP1P/RNB1K1NR b KQkq -")
        assertEquals(28, countAll(board))
    }

    @Test
    fun testLegalQuietMove() {
        val board = BoardFactory.getBoard("1qr1b1k1/6r1/p2PQ2p/BpPn2p1/n2P1pP1/3N3P/R1B2P2/5RK1 b - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            println(Move.toString(move))
            assertTrue(MoveGenerator.isPseudoLegalMove(board, attackInfo, move))
        }
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            println(Move.toString(move))
            assertFalse(MoveGenerator.isPseudoLegalMove(board, attackInfo, move))
        }
        println(moveList.toString())
    }

    @Test
    fun testLegalQuietMove2() {
        // g7g5 was not being considered a valid move
        val board = BoardFactory.getBoard("8/3r2p1/2b1Rp1k/7P/PR4P1/4B3/7K/3r4 b - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()
            if (board.isLegalMove(move)) {
                println(Move.toString(move))
                assertTrue(MoveGenerator.isPseudoLegalMove(board, attackInfo, move))
            }
        }
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            if (board.isLegalMove(move)) {
                println(Move.toString(move))
                assertFalse(MoveGenerator.isPseudoLegalMove(board, attackInfo, move))
            }
        }
        println(moveList.toString())
    }
}