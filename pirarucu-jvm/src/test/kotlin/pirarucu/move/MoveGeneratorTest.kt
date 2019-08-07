package pirarucu.move

import pirarucu.board.Square
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

    @Test
    fun testPawnMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3P4/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 6)
    }

    @Test
    fun testPawnDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/3P4/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 7)
    }

    @Test
    fun testCantPromote() {
        val board = BoardFactory.getBoard("2r2k2/2P5/8/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 0)
    }

    @Test
    fun testPromotion() {
        val board = BoardFactory.getBoard("5k2/2P5/8/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 4)
    }

    @Test
    fun testPromotionDeniedByCheck() {
        val board = BoardFactory.getBoard("5k2/3P4/1q6/8/8/8/5K2/8 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 0)
    }

    @Test
    fun testPinnedPromotion() {
        val board = BoardFactory.getBoard("1b3k2/2P5/8/8/5K2/8/8/8 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 4)
    }

    @Test
    fun testInCheckPromotion() {
        val board = BoardFactory.getBoard("1q3K2/2P5/8/8/5k2/8/8/8 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 8)
    }

    @Test
    fun testPawnBlockedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3p4/3P4/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 5)
    }

    @Test
    fun testPawnBlockedDoubleMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/3p4/3P4/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 4)
    }

    @Test
    fun testPawnBlockedDoubleMove2() {
        val board = BoardFactory.getBoard("5k2/8/8/8/3p4/8/3P4/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 6)
    }

    @Test
    fun testKnightMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/2N5/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 13)
    }

    @Test
    fun testKnightPinned() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6N1/7K w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 2)
    }

    @Test
    fun testBishopMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3B4/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 18)
    }

    @Test
    fun testBishopPinnedMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3b4/8/8/6B1/7K w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 5)
    }

    @Test
    fun testRookMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3R4/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 19)
    }

    @Test
    fun testRookPinnedMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5R2/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 9)
    }

    @Test
    fun testQueenMove() {
        val board = BoardFactory.getBoard("5k2/8/8/3Q4/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 32)
    }

    @Test
    fun testQueenPinnedMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/5Q2/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 9)
    }

    @Test
    fun testKingMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 5)
    }

    @Test
    fun testKingSafeMove() {
        val board = BoardFactory.getBoard("5k2/5r2/8/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 4)
    }

    @Test
    fun testCastlingMove() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/R3K3 w Q -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 16)
    }

    @Test
    fun testCastlingMoveInCheck() {
        val board = BoardFactory.getBoard("3r1k2/8/8/8/8/8/8/R3K3 w Q -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 13)
    }

    @Test
    fun testEpCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/3Pp3/8/8/8/4K3 w - e6")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 7)
    }

    @Test
    fun testEpCapturePinned() {
        val board = BoardFactory.getBoard("5kb1/8/8/3Pp3/2K5/8/8/8 w - e6")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 7)
    }

    @Test
    fun testNoEpCapturePinned() {
        val board = BoardFactory.getBoard("2r2k2/8/8/2P1p3/2K5/8/8/8 w - e6")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 7)
    }

    @Test
    fun testCapturePromotion() {
        val board = BoardFactory.getBoard("3r1k2/2P5/8/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 13)
    }

    @Test
    fun testKnightCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/2N5/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 13)
    }

    @Test
    fun testBishopCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/1B6/8/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 14)
    }

    @Test
    fun testRookCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/8/3R4/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 19)
    }

    @Test
    fun testQueenCapture() {
        val board = BoardFactory.getBoard("3r1k2/8/8/3Q4/8/8/8/5K2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 32)
    }

    @Test
    fun testKingCapture() {
        val board = BoardFactory.getBoard("5k2/8/8/8/8/8/8/4qK2 w - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(legalMoves, 2)
    }

    @Test
    fun testInCheckGeneration() {
        val board = BoardFactory.getBoard("7K/8/3P4/3r4/8/2k3r1/7p/8 w - -")
        val moveList = OrderedMoveList()
        board.doMove(Move.createMove(Square.H8, Square.H7))
        board.doMove(Move.createMove(Square.D5, Square.H5))
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(0, legalMoves)
    }

    @Test
    fun testLongPromotion() {
        val board = BoardFactory.getBoard("1Qq2knr/pp1n1p1p/6p1/8/2Pb4/2N4P/PP2PPP1/R1B1KB1R b KQ -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)
        moveGenerator.generateNoisy(board, attackInfo, moveList)
        var legalMoves = 0
        while (moveList.hasNext()) {
            if (board.isLegalMove(moveList.next())) {
                legalMoves++
            }
        }
        assertEquals(31, legalMoves)
    }

    @Test
    fun testLegalQuietMove() {
        val board = BoardFactory.getBoard("1qr1b1k1/6r1/p2PQ2p/BpPn2p1/n2P1pP1/3N3P/R1B2P2/5RK1 b - -")
        val moveList = OrderedMoveList()
        moveGenerator.generateQuiet(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            assertTrue(MoveGenerator.isLegalQuietMove(board, attackInfo, move))
        }
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            assertFalse(MoveGenerator.isLegalQuietMove(board, attackInfo, move))
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

            assertTrue(MoveGenerator.isLegalQuietMove(board, attackInfo, move))
        }
        moveGenerator.generateNoisy(board, attackInfo, moveList)

        while (moveList.hasNext()) {
            val move = moveList.next()

            assertFalse(MoveGenerator.isLegalQuietMove(board, attackInfo, move))
        }
        println(moveList.toString())
    }
}