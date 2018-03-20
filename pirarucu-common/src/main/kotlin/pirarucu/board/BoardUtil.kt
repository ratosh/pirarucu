package pirarucu.board

import pirarucu.eval.EvalConstants
import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist
import pirarucu.util.Utils

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

    fun calculatePieceScore(board: Board): Int {
        return (EvalConstants.PIECE_SCORE[Piece.PAWN] *
            Utils.specific.bitCount(board.pieceBitboard[Color.WHITE][Piece.PAWN]) +
            EvalConstants.PIECE_SCORE[Piece.KNIGHT] *
            Utils.specific.bitCount(board.pieceBitboard[Color.WHITE][Piece.KNIGHT]) +
            EvalConstants.PIECE_SCORE[Piece.BISHOP] *
            Utils.specific.bitCount(board.pieceBitboard[Color.WHITE][Piece.BISHOP]) +
            EvalConstants.PIECE_SCORE[Piece.ROOK] *
            Utils.Companion.specific.bitCount(board.pieceBitboard[Color.WHITE][Piece.ROOK]) +
            EvalConstants.PIECE_SCORE[Piece.QUEEN] *
            Utils.Companion.specific.bitCount(board.pieceBitboard[Color.WHITE][Piece.QUEEN])) -
            (EvalConstants.PIECE_SCORE[Piece.PAWN] *
                Utils.specific.bitCount(board.pieceBitboard[Color.BLACK][Piece.PAWN]) +
                EvalConstants.PIECE_SCORE[Piece.KNIGHT] *
                Utils.specific.bitCount(board.pieceBitboard[Color.BLACK][Piece.KNIGHT]) +
                EvalConstants.PIECE_SCORE[Piece.BISHOP] *
                Utils.specific.bitCount(board.pieceBitboard[Color.BLACK][Piece.BISHOP]) +
                EvalConstants.PIECE_SCORE[Piece.ROOK] *
                Utils.Companion.specific.bitCount(board.pieceBitboard[Color.BLACK][Piece.ROOK]) +
                EvalConstants.PIECE_SCORE[Piece.QUEEN] *
                Utils.Companion.specific.bitCount(board.pieceBitboard[Color.BLACK][Piece.QUEEN]))
    }

    fun calculatePhase(board: Board): Int {
        return EvalConstants.PHASE_PIECE_SCORE[Piece.PAWN] * board.pieceCount[Piece.PAWN] +
            EvalConstants.PHASE_PIECE_SCORE[Piece.KNIGHT] * board.pieceCount[Piece.KNIGHT] +
            EvalConstants.PHASE_PIECE_SCORE[Piece.BISHOP] * board.pieceCount[Piece.BISHOP] +
            EvalConstants.PHASE_PIECE_SCORE[Piece.ROOK] * board.pieceCount[Piece.ROOK] +
            EvalConstants.PHASE_PIECE_SCORE[Piece.QUEEN] * board.pieceCount[Piece.QUEEN]
    }
}
