package pirarucu.board

import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist

object BoardUtil {
    fun updateZobristKeys(board: Board) {
        var zobristKey = 0L
        var pawnZobristKey = 0L
        if (board.colorToMove == Color.BLACK) {
            zobristKey = Zobrist.SIDE
        }
        if (board.epSquare != Square.NONE) {
            zobristKey = zobristKey xor Zobrist.PASSANT_SQUARE[board.epSquare]
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
        for (square in Square.H1 until Square.SIZE) {
            if (board.pieceTypeBoard[square] != Piece.NONE &&
                Bitboard.getBitboard(square) and board.gameBitboard == Bitboard.EMPTY) {
                return false
            }
        }
        return board.colorBitboard[Color.WHITE] and board.colorBitboard[Color.BLACK] ==
            Bitboard.EMPTY
    }

    fun calculatePsqtScore(board: Board): Int {
        var result = 0
        for (square in Square.A1 until Square.SIZE) {
            val piece = board.pieceTypeBoard[square]
            if (piece != Piece.NONE) {
                val color = board.colorAt(square)
                val relativeSquare = Square.getRelativeSquare(color, square)
                result += EvalConstants.PSQT[piece][relativeSquare] * GameConstants.COLOR_FACTOR[color]
            }
        }
        return result
    }

    fun calculateMaterialScore(board: Board): Int {
        return (EvalConstants.MATERIAL_SCORE[Piece.PAWN] *
            board.pieceCountColorType[Color.WHITE][Piece.PAWN] +
            EvalConstants.MATERIAL_SCORE[Piece.KNIGHT] *
            board.pieceCountColorType[Color.WHITE][Piece.KNIGHT] +
            EvalConstants.MATERIAL_SCORE[Piece.BISHOP] *
            board.pieceCountColorType[Color.WHITE][Piece.BISHOP] +
            EvalConstants.MATERIAL_SCORE[Piece.ROOK] *
            board.pieceCountColorType[Color.WHITE][Piece.ROOK] +
            EvalConstants.MATERIAL_SCORE[Piece.QUEEN] *
            board.pieceCountColorType[Color.WHITE][Piece.QUEEN] -
            (EvalConstants.MATERIAL_SCORE[Piece.PAWN] *
                board.pieceCountColorType[Color.BLACK][Piece.PAWN] +
                EvalConstants.MATERIAL_SCORE[Piece.KNIGHT] *
                board.pieceCountColorType[Color.BLACK][Piece.KNIGHT] +
                EvalConstants.MATERIAL_SCORE[Piece.BISHOP] *
                board.pieceCountColorType[Color.BLACK][Piece.BISHOP] +
                EvalConstants.MATERIAL_SCORE[Piece.ROOK] *
                board.pieceCountColorType[Color.BLACK][Piece.ROOK] +
                EvalConstants.MATERIAL_SCORE[Piece.QUEEN] *
                board.pieceCountColorType[Color.BLACK][Piece.QUEEN]))
    }

    fun calculatePhase(board: Board): Int {
        return EvalConstants.PHASE_PIECE_VALUE[Piece.PAWN] * board.pieceCountType[Piece.PAWN] +
            EvalConstants.PHASE_PIECE_VALUE[Piece.KNIGHT] * board.pieceCountType[Piece.KNIGHT] +
            EvalConstants.PHASE_PIECE_VALUE[Piece.BISHOP] * board.pieceCountType[Piece.BISHOP] +
            EvalConstants.PHASE_PIECE_VALUE[Piece.ROOK] * board.pieceCountType[Piece.ROOK] +
            EvalConstants.PHASE_PIECE_VALUE[Piece.QUEEN] * board.pieceCountType[Piece.QUEEN]
    }
}
