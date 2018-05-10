package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.move.MoveGenerator
import pirarucu.util.Utils

class AttackInfo {

    var attacksBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }
        private set

    var pieceMovement = Array(Color.SIZE) { LongArray(Square.SIZE) }
        private set

    var zobristKey = LongArray(Color.SIZE)

    fun update(board: Board, color: Int) {
        if (board.zobristKey == zobristKey[color]) {
            return
        }
        zobristKey[color] = board.zobristKey
        Utils.specific.arrayFill(pieceMovement[color], 0)
        Utils.specific.arrayFill(attacksBitboard[color], 0)
        val checkBitboard = board.basicEvalInfo.checkBitboard[color]

        when {
            checkBitboard == Bitboard.EMPTY -> {
                val mask = Bitboard.ALL
                pawnAttacks(board, color, mask)
                knightMoves(board, color, mask)
                bishopMoves(board, color, mask)
                rookMoves(board, color, mask)
            }
            Bitboard.oneElement(checkBitboard) -> {
                val square = Square.getSquare(checkBitboard)
                val kingSquare = board.basicEvalInfo.kingSquare[color]
                val betweenMask = BitboardMove.BETWEEN_BITBOARD[kingSquare][square] or
                    checkBitboard
                pawnAttacks(board, color, betweenMask)
                knightMoves(board, color, betweenMask)
                bishopMoves(board, color, betweenMask)
                rookMoves(board, color, betweenMask)
            }
        }
        kingMoves(board, color)

        attacksBitboard[color][Piece.NONE] = attacksBitboard[color][Piece.PAWN] or
            attacksBitboard[color][Piece.KNIGHT] or
            attacksBitboard[color][Piece.BISHOP] or
            attacksBitboard[color][Piece.ROOK] or
            attacksBitboard[color][Piece.QUEEN] or
            attacksBitboard[color][Piece.KING]
    }

    private fun pawnAttacks(board: Board, color: Int, mask: Long) {
        val kingSquare = board.basicEvalInfo.kingSquare[color]
        var tmpPieces = board.pieceBitboard[color][Piece.PAWN]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)

            var bitboard = BitboardMove.PAWN_ATTACKS[color][fromSquare] and mask
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[color] != Bitboard.EMPTY) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            pieceMovement[color][fromSquare] = bitboard
            attacksBitboard[color][Piece.PAWN] = attacksBitboard[color][Piece.PAWN] or
                bitboard

            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun knightMoves(board: Board, color: Int, mask: Long) {
        var tmpPieces = board.pieceBitboard[color][Piece.KNIGHT] and
            board.basicEvalInfo.pinnedBitboard[color].inv()
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val bitboard = BitboardMove.KNIGHT_MOVES[fromSquare] and mask

            pieceMovement[color][fromSquare] = bitboard
            attacksBitboard[color][Piece.KNIGHT] = attacksBitboard[color][Piece.KNIGHT] or
                bitboard

            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun bishopMoves(board: Board, color: Int, mask: Long) {
        val kingSquare = board.basicEvalInfo.kingSquare[color]
        val pieces = board.pieceBitboard[color][Piece.BISHOP] or
            board.pieceBitboard[color][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.bishopMoves(fromSquare, board.gameBitboard) and
                mask
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[color] != Bitboard.EMPTY) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }
            val pieceType = board.pieceTypeBoard[fromSquare]

            pieceMovement[color][fromSquare] = pieceMovement[color][fromSquare] or bitboard
            attacksBitboard[color][pieceType] = attacksBitboard[color][pieceType] or
                bitboard

            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun rookMoves(board: Board, color: Int, mask: Long) {
        val kingSquare = board.basicEvalInfo.kingSquare[color]
        val pieces = board.pieceBitboard[color][Piece.ROOK] or
            board.pieceBitboard[color][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.rookMoves(fromSquare, board.gameBitboard) and
                mask
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[color] != Bitboard.EMPTY) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }
            val pieceType = board.pieceTypeBoard[fromSquare]

            pieceMovement[color][fromSquare] = pieceMovement[color][fromSquare] or bitboard
            attacksBitboard[color][pieceType] = attacksBitboard[color][pieceType] or
                bitboard

            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun kingMoves(board: Board, color: Int) {
        val fromSquare = board.basicEvalInfo.kingSquare[color]
        val fromBitboard = Bitboard.getBitboard(fromSquare)
        var moves = BitboardMove.KING_MOVES[fromSquare]
        val ourColor = color
        val theirColor = Color.invertColor(color)
        val gameBitboard = board.gameBitboard xor fromBitboard
        var bitboard = Bitboard.EMPTY

        while (moves != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(moves)
            if (MoveGenerator
                    .squareAttackedBitboard(toSquare, ourColor, board.pieceBitboard[theirColor], gameBitboard) ==
                Bitboard.EMPTY) {
                bitboard = bitboard or Bitboard.getBitboard(toSquare)
            }
            moves = moves and moves - 1
        }
        pieceMovement[color][fromSquare] = bitboard
        attacksBitboard[color][Piece.KING] = bitboard
    }
}
