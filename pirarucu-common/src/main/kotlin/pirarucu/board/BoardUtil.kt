package pirarucu.board

import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist
import pirarucu.tuning.TunableConstants
import pirarucu.util.PlatformSpecific

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
            val colorAt = board.pieceColorAt(square)
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
                println("piece type not set " + Square.toString(square))
                return false
            }
            bitboard = bitboard and bitboard - 1
        }
        for (square in Square.H1 until Square.SIZE) {
            if (board.pieceTypeBoard[square] != Piece.NONE &&
                    Bitboard.getBitboard(square) and board.gameBitboard == Bitboard.EMPTY) {
                println("game bitboard not set " + Square.toString(square))
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

        if (board.pieceBitboard[Color.WHITE][Piece.NONE] and
                board.pieceBitboard[Color.BLACK][Piece.NONE] != Bitboard.EMPTY) {
            println("OVERLAPPING COLOR BITBOARD")
            return false
        }
        return true
    }

    fun calculatePsqtScore(board: Board): Int {
        var result = 0
        for (square in Square.A1 until Square.SIZE) {
            val piece = board.pieceTypeBoard[square]
            if (piece != Piece.NONE) {
                val color = board.pieceColorAt(square)
                val relativeSquare = Square.getRelativeSquare(color, square)
                result += TunableConstants.PSQT[piece][relativeSquare] * GameConstants.COLOR_FACTOR[color]
            }
        }
        return result
    }

    fun calculateMaterialScore(board: Board): Int {
        return (TunableConstants.MATERIAL_SCORE[Piece.PAWN] *
                PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.PAWN]) +
                TunableConstants.MATERIAL_SCORE[Piece.KNIGHT] *
                PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.KNIGHT]) +
                TunableConstants.MATERIAL_SCORE[Piece.BISHOP] *
                PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.BISHOP]) +
                TunableConstants.MATERIAL_SCORE[Piece.ROOK] *
                PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.ROOK]) +
                TunableConstants.MATERIAL_SCORE[Piece.QUEEN] *
                PlatformSpecific.bitCount(board.pieceBitboard[Color.WHITE][Piece.QUEEN]) -
                (TunableConstants.MATERIAL_SCORE[Piece.PAWN] *
                        PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.PAWN]) +
                        TunableConstants.MATERIAL_SCORE[Piece.KNIGHT] *
                        PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.KNIGHT]) +
                        TunableConstants.MATERIAL_SCORE[Piece.BISHOP] *
                        PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.BISHOP]) +
                        TunableConstants.MATERIAL_SCORE[Piece.ROOK] *
                        PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.ROOK]) +
                        TunableConstants.MATERIAL_SCORE[Piece.QUEEN] *
                        PlatformSpecific.bitCount(board.pieceBitboard[Color.BLACK][Piece.QUEEN])))
    }

    fun calculatePhase(board: Board): Int {
        return TunableConstants.PHASE_PIECE_VALUE[Piece.PAWN] * PlatformSpecific
                .bitCount(board.pieceBitboard[Color.WHITE][Piece.PAWN] or board.pieceBitboard[Color.BLACK][Piece.PAWN]) +
                TunableConstants.PHASE_PIECE_VALUE[Piece.KNIGHT] * PlatformSpecific
                .bitCount(board.pieceBitboard[Color.WHITE][Piece.KNIGHT] or board.pieceBitboard[Color.BLACK][Piece.KNIGHT]) +
                TunableConstants.PHASE_PIECE_VALUE[Piece.BISHOP] * PlatformSpecific
                .bitCount(board.pieceBitboard[Color.WHITE][Piece.BISHOP] or board.pieceBitboard[Color.BLACK][Piece.BISHOP]) +
                TunableConstants.PHASE_PIECE_VALUE[Piece.ROOK] * PlatformSpecific
                .bitCount(board.pieceBitboard[Color.WHITE][Piece.ROOK] or board.pieceBitboard[Color.BLACK][Piece.ROOK]) +
                TunableConstants.PHASE_PIECE_VALUE[Piece.QUEEN] * PlatformSpecific
                .bitCount(board.pieceBitboard[Color.WHITE][Piece.QUEEN] or board.pieceBitboard[Color.BLACK][Piece.QUEEN])
    }
}
