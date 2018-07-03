package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove

class BasicEvalInfo {

    var checkBitboard = 0L
        private set

    val pinnerBitboard = LongArray(Color.SIZE)

    val discoveryBitboard = LongArray(Color.SIZE)

    val pinnedBitboard = LongArray(Color.SIZE)

    val kingSquare = IntArray(Color.SIZE)

    val dangerBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }

    fun update(board: Board) {
        for (ourColor in Color.WHITE until Color.SIZE) {
            kingSquare[ourColor] = Square.getSquare(board.pieceBitboard[ourColor][Piece.KING])
            val theirColor = Color.invertColor(ourColor)
            val theirPieceBitboard = board.pieceBitboard[theirColor]
            val kingSquarePosition = kingSquare[ourColor]

            kingSquare[ourColor] = kingSquarePosition

            setPinnedDiscovery(kingSquarePosition,
                ourColor,
                theirColor,
                theirPieceBitboard,
                board.colorBitboard)

            dangerBitboard[ourColor][Piece.PAWN] = BitboardMove.PAWN_ATTACKS[ourColor][kingSquarePosition]
            dangerBitboard[ourColor][Piece.KNIGHT] = BitboardMove.KNIGHT_MOVES[kingSquarePosition]
            dangerBitboard[ourColor][Piece.BISHOP] = BitboardMove.bishopMoves(kingSquarePosition, board.gameBitboard)
            dangerBitboard[ourColor][Piece.ROOK] = BitboardMove.rookMoves(kingSquarePosition, board.gameBitboard)
            dangerBitboard[ourColor][Piece.QUEEN] = dangerBitboard[ourColor][Piece.BISHOP] or
                dangerBitboard[ourColor][Piece.ROOK]
        }

        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        checkBitboard = (dangerBitboard[ourColor][Piece.PAWN] and board.pieceBitboard[theirColor][Piece.PAWN]) or
            (dangerBitboard[ourColor][Piece.KNIGHT] and board.pieceBitboard[theirColor][Piece.KNIGHT]) or
            (dangerBitboard[ourColor][Piece.BISHOP] and board.pieceBitboard[theirColor][Piece.BISHOP]) or
            (dangerBitboard[ourColor][Piece.ROOK] and board.pieceBitboard[theirColor][Piece.ROOK]) or
            (dangerBitboard[ourColor][Piece.QUEEN] and board.pieceBitboard[theirColor][Piece.QUEEN])
    }

    /**
     * Updates pinnedBitboard, pinnerBitboard and discoveryBitboard
     */
    private fun setPinnedDiscovery(kingSquare: Int,
                                   ourColor: Int,
                                   theirColor: Int,
                                   theirPieceBitboard: LongArray,
                                   colorBitboard: LongArray) {
        var pinned = 0L
        var pinner = 0L
        var discovery = 0L

        val ourColorBitboard = colorBitboard[ourColor]
        val theirColorBitboard = colorBitboard[theirColor]
        val gameBitboard = ourColorBitboard or theirColorBitboard

        var piece = ((theirPieceBitboard[Piece.BISHOP] or theirPieceBitboard[Piece.QUEEN]) and
            BitboardMove.BISHOP_PSEUDO_MOVES[kingSquare]) or
            ((theirPieceBitboard[Piece.ROOK] or theirPieceBitboard[Piece.QUEEN]) and
                BitboardMove.ROOK_PSEUDO_MOVES[kingSquare])

        while (piece != 0L) {
            val square = Square.getSquare(piece)
            val betweenPiece = BitboardMove.BETWEEN_BITBOARD[kingSquare][square] and gameBitboard
            if (betweenPiece != 0L && Bitboard.oneElement(betweenPiece)) {
                val bitboard = Bitboard.getBitboard(square)
                pinner = pinner or bitboard
                discovery = discovery or (betweenPiece and theirColorBitboard)
                pinned = pinned or (betweenPiece and ourColorBitboard)
            }

            piece = piece and piece - 1
        }
        pinnedBitboard[ourColor] = pinned
        pinnerBitboard[theirColor] = pinner
        discoveryBitboard[theirColor] = discovery
    }
}
