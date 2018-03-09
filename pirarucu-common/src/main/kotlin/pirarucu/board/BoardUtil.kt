package pirarucu.board

import pirarucu.hash.Zobrist

object BoardUtil {
    fun updateZobristKeys(board: Board) {
        var zobristKey = 0L
        var pawnZobristKey = 0L
        if (board.colorToMove == Color.BLACK) {
            zobristKey = Zobrist.SIDE
        }
        if (board.epSquare != Square.NONE) {
            zobristKey = zobristKey xor Zobrist.PASSANT_FILE[File.getFile(board.epSquare)]
        }
        for (square in Square.A1 until Square.SIZE) {
            val piece = board.pieceTypeBoard[square]
            val colorAt = board.colorAt(square)
            when (piece) {
                Piece.NONE -> {
                    // DO NOTHING
                }
                Piece.PAWN -> {
                    zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[colorAt][piece][square]
                    pawnZobristKey = pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[colorAt][piece][square]
                }
                else -> {
                    zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[colorAt][piece][square]
                }
            }
        }
        zobristKey = zobristKey xor Zobrist.CASTLING_RIGHT[board.castlingRights]
        board.zobristKey = zobristKey
        board.pawnZobristKey = pawnZobristKey
    }

    fun validBoard(board: Board): Boolean {
        var bitboard = board.gameBitboard
        while (bitboard != 0L) {
            val square = Square.getSquare(bitboard)
            if (board.pieceTypeBoard[square] == Piece.NONE) {
                return false
            }
            bitboard = bitboard and bitboard - 1
        }
        return board.colorBitboard[Color.WHITE] and board.colorBitboard[Color.BLACK] == 0L
    }
}
