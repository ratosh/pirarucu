package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.util.PlatformSpecific

class BasicEvalInfo {

    var checkBitboard = 0L
        private set

    var pinnedBitboard = 0L
        private set

    val dangerBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }

    fun update(board: Board) {
        updateDangerBitboard(board, Color.WHITE)
        updateDangerBitboard(board, Color.BLACK)

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
        checkBitboard = (dangerBitboard[ourColor][Piece.PAWN] and board.pieceBitboard[theirColor][Piece.PAWN]) or
            (dangerBitboard[ourColor][Piece.KNIGHT] and board.pieceBitboard[theirColor][Piece.KNIGHT]) or
            (dangerBitboard[ourColor][Piece.BISHOP] and board.pieceBitboard[theirColor][Piece.BISHOP]) or
            (dangerBitboard[ourColor][Piece.ROOK] and board.pieceBitboard[theirColor][Piece.ROOK]) or
            (dangerBitboard[ourColor][Piece.QUEEN] and board.pieceBitboard[theirColor][Piece.QUEEN])
    }

    private fun updateDangerBitboard(board: Board, ourColor: Int) {
        val kingSquarePosition = board.kingSquare[ourColor]

        dangerBitboard[ourColor][Piece.PAWN] = BitboardMove.PAWN_ATTACKS[ourColor][kingSquarePosition]
        dangerBitboard[ourColor][Piece.KNIGHT] = BitboardMove.KNIGHT_MOVES[kingSquarePosition]
        dangerBitboard[ourColor][Piece.BISHOP] = BitboardMove.bishopMoves(kingSquarePosition, board.gameBitboard)
        dangerBitboard[ourColor][Piece.ROOK] = BitboardMove.rookMoves(kingSquarePosition, board.gameBitboard)
        dangerBitboard[ourColor][Piece.QUEEN] = dangerBitboard[ourColor][Piece.BISHOP] or
            dangerBitboard[ourColor][Piece.ROOK]

        dangerBitboard[ourColor][Piece.NONE] =
            dangerBitboard[ourColor][Piece.PAWN] or
            dangerBitboard[ourColor][Piece.KNIGHT] or
            dangerBitboard[ourColor][Piece.BISHOP] or
            dangerBitboard[ourColor][Piece.ROOK]
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

        val theirColorBitboard = colorBitboard[theirColor]
        if (theirPieceBitboard[Piece.BISHOP] or
            theirPieceBitboard[Piece.ROOK] or
            theirPieceBitboard[Piece.QUEEN] != Bitboard.EMPTY
        ) {

            var pinned = 0L

            val ourColorBitboard = colorBitboard[ourColor]
            val gameBitboard = ourColorBitboard or theirColorBitboard

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
    }

    fun copy(basicEvalInfo: BasicEvalInfo) {
        checkBitboard = basicEvalInfo.checkBitboard
        pinnedBitboard = basicEvalInfo.pinnedBitboard
        PlatformSpecific.arrayCopy(basicEvalInfo.dangerBitboard, dangerBitboard)
    }
}
