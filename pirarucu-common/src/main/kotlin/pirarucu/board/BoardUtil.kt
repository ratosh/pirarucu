package pirarucu.board

import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist
import pirarucu.tuning.TunableConstants

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
                println("piece type not set")
                return false
            }
            bitboard = bitboard and bitboard - 1
        }
        for (square in Square.H1 until Square.SIZE) {
            if (board.pieceTypeBoard[square] != Piece.NONE &&
                Bitboard.getBitboard(square) and board.gameBitboard == Bitboard.EMPTY) {
                println("game bitboard not set")
                return false
            }
        }

        val zobKey = board.zobristKey
        val pawnZobKey = board.pawnZobristKey

        updateZobristKeys(board)

        if (zobKey != board.zobristKey) {
            println("ZOBRIST KEY PROBLEM")
            return false
        }

        if (pawnZobKey != board.pawnZobristKey) {
            println("PAWN ZOBRIST KEY PROBLEM")
            return false
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
                result += TunableConstants.PSQT[piece][relativeSquare] * GameConstants.COLOR_FACTOR[color]
            }
        }
        return result
    }

    fun calculateMaterialScore(board: Board): Int {
        return (TunableConstants.MATERIAL_SCORE[Piece.PAWN] *
            board.pieceCountColorType[Color.WHITE][Piece.PAWN] +
            TunableConstants.MATERIAL_SCORE[Piece.KNIGHT] *
            board.pieceCountColorType[Color.WHITE][Piece.KNIGHT] +
            TunableConstants.MATERIAL_SCORE[Piece.BISHOP] *
            board.pieceCountColorType[Color.WHITE][Piece.BISHOP] +
            TunableConstants.MATERIAL_SCORE[Piece.ROOK] *
            board.pieceCountColorType[Color.WHITE][Piece.ROOK] +
            TunableConstants.MATERIAL_SCORE[Piece.QUEEN] *
            board.pieceCountColorType[Color.WHITE][Piece.QUEEN] -
            (TunableConstants.MATERIAL_SCORE[Piece.PAWN] *
                board.pieceCountColorType[Color.BLACK][Piece.PAWN] +
                TunableConstants.MATERIAL_SCORE[Piece.KNIGHT] *
                board.pieceCountColorType[Color.BLACK][Piece.KNIGHT] +
                TunableConstants.MATERIAL_SCORE[Piece.BISHOP] *
                board.pieceCountColorType[Color.BLACK][Piece.BISHOP] +
                TunableConstants.MATERIAL_SCORE[Piece.ROOK] *
                board.pieceCountColorType[Color.BLACK][Piece.ROOK] +
                TunableConstants.MATERIAL_SCORE[Piece.QUEEN] *
                board.pieceCountColorType[Color.BLACK][Piece.QUEEN]))
    }

    fun calculatePhase(board: Board): Int {
        return TunableConstants.PHASE_PIECE_VALUE[Piece.PAWN] * board.pieceCountType[Piece.PAWN] +
            TunableConstants.PHASE_PIECE_VALUE[Piece.KNIGHT] * board.pieceCountType[Piece.KNIGHT] +
            TunableConstants.PHASE_PIECE_VALUE[Piece.BISHOP] * board.pieceCountType[Piece.BISHOP] +
            TunableConstants.PHASE_PIECE_VALUE[Piece.ROOK] * board.pieceCountType[Piece.ROOK] +
            TunableConstants.PHASE_PIECE_VALUE[Piece.QUEEN] * board.pieceCountType[Piece.QUEEN]
    }
}
