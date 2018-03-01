package pirarucu.board

import pirarucu.hash.Zobrist

object BoardUtil {
    fun updateZobristKeys(board: Board) {
        var zobristKey = 0L
        var pawnZobristKey = 0L
        if (board.colorToMove == Color.BLACK) {
            zobristKey = Zobrist.SIDE
        }
        if (board.currentState.epSquare != Square.NONE) {
            zobristKey = zobristKey xor Zobrist.PASSANT_FILE[File.getFile(board.currentState.epSquare)]
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
        zobristKey = zobristKey xor Zobrist.CASTLING_RIGHT[board.currentState.castlingRights]
        board.currentState.zobristKey = zobristKey
        board.currentState.pawnZobristKey = pawnZobristKey
    }
}