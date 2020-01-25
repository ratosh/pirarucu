package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.move.MoveGenerator
import pirarucu.util.PlatformSpecific

class BasicEvalInfo {

    var checkBitboard = 0L
        private set

    var pinnedBitboard = 0L
        private set

    fun update(board: Board) {
        pinnedBitboard = 0L
        for (ourColor in Color.WHITE until Color.SIZE) {
            val theirColor = Color.invertColor(ourColor)
            val theirPieceBitboard = board.pieceBitboard[theirColor]
            val kingSquarePosition = board.kingSquare[ourColor]
            setPinned(
                kingSquarePosition,
                ourColor,
                theirColor,
                theirPieceBitboard,
                board.colorBitboard
            )
        }

        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        checkBitboard = MoveGenerator.squareAttackedBitboard(
                board.kingSquare[ourColor], ourColor, board.pieceBitboard[theirColor], board.gameBitboard)
    }

    /**
     * Updates pinnedBitboard
     */
    private fun setPinned(
        kingSquare: Int,
        ourColor: Int,
        theirColor: Int,
        theirPieceBitboard: LongArray,
        colorBitboard: LongArray
    ) {
        var pinned = 0L

        val ourColorBitboard = colorBitboard[ourColor]
        val gameBitboard = ourColorBitboard or colorBitboard[theirColor]

        var tmpPiece = ((theirPieceBitboard[Piece.BISHOP] or theirPieceBitboard[Piece.QUEEN]) and
            BitboardMove.BISHOP_PSEUDO_MOVES[kingSquare]) or
            ((theirPieceBitboard[Piece.ROOK] or theirPieceBitboard[Piece.QUEEN]) and
                BitboardMove.ROOK_PSEUDO_MOVES[kingSquare])

        while (tmpPiece != 0L) {
            val square = Square.getSquare(tmpPiece)
            val betweenPiece = BitboardMove.BETWEEN_BITBOARD[kingSquare][square] and gameBitboard
            if (betweenPiece != 0L && Bitboard.oneElement(betweenPiece)) {
                pinned = pinned or (betweenPiece and ourColorBitboard)
            }

            tmpPiece = tmpPiece and tmpPiece - 1
        }
        pinnedBitboard = pinnedBitboard or pinned
    }

    fun copy(basicEvalInfo: BasicEvalInfo) {
        checkBitboard = basicEvalInfo.checkBitboard
        pinnedBitboard = basicEvalInfo.pinnedBitboard
    }
}
