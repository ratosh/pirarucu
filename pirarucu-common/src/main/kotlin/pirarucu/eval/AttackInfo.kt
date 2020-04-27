package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import pirarucu.util.PlatformSpecific

class AttackInfo {

    var attacksBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }
        private set

    var pieceMovement = Array(Color.SIZE) { LongArray(Square.SIZE) }
        private set

    var movementMask = LongArray(Color.SIZE)

    var zobristKey = LongArray(Color.SIZE)

    fun update(board: Board, color: Int) {
        if (board.zobristKey == zobristKey[color]) {
            return
        }
        zobristKey[color] = board.zobristKey
        PlatformSpecific.arrayFill(pieceMovement[color], 0)
        PlatformSpecific.arrayFill(attacksBitboard[color], 0)
        val checkBitboard = board.basicEvalInfo.checkBitboard

        val mask = when {
            checkBitboard == Bitboard.EMPTY -> {
                Bitboard.ALL
            }
            Bitboard.oneElement(checkBitboard) -> {
                val square = Square.getSquare(checkBitboard)
                val kingSquare = board.kingSquare[color]
                BitboardMove.BETWEEN_BITBOARD[kingSquare][square] or checkBitboard
            }
            else -> {
                Bitboard.EMPTY
            }
        }
        if (mask != Bitboard.EMPTY) {
            pawnAttacks(board, color, mask)
            knightMoves(board, color, mask)
            bishopMoves(board, color, mask)
            rookMoves(board, color, mask)
        }
        kingMoves(board, color)

        movementMask[color] = mask

        attacksBitboard[color][Piece.NONE] = attacksBitboard[color][Piece.PAWN] or
            attacksBitboard[color][Piece.KNIGHT] or
            attacksBitboard[color][Piece.BISHOP] or
            attacksBitboard[color][Piece.ROOK] or
            attacksBitboard[color][Piece.QUEEN] or
            attacksBitboard[color][Piece.KING]
    }

    private fun pawnAttacks(board: Board, color: Int, mask: Long) {
        val unpinnedPawns = board.pieceBitboard[color][Piece.PAWN] and board.basicEvalInfo.pinnedBitboard.inv()
        attacksBitboard[color][Piece.PAWN] = BitboardMove.pawnAttacks(color, unpinnedPawns) and mask

        var tmpPieces = board.pieceBitboard[color][Piece.PAWN] and board.basicEvalInfo.pinnedBitboard
        val kingSquare = board.kingSquare[color]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val bitboard = BitboardMove.pawnAttacks(color, fromSquare) and mask and
                BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]

            attacksBitboard[color][Piece.PAWN] = attacksBitboard[color][Piece.PAWN] or
                bitboard
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun knightMoves(board: Board, color: Int, mask: Long) {
        var tmpPieces = board.pieceBitboard[color][Piece.KNIGHT] and
            board.basicEvalInfo.pinnedBitboard.inv()
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
        val kingSquare = board.kingSquare[color]
        val pieces = board.pieceBitboard[color][Piece.BISHOP] or
            board.pieceBitboard[color][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.bishopMoves(fromSquare, board.gameBitboard) and
                mask
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard != Bitboard.EMPTY) {
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
        val kingSquare = board.kingSquare[color]
        val pieces = board.pieceBitboard[color][Piece.ROOK] or
            board.pieceBitboard[color][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.rookMoves(fromSquare, board.gameBitboard) and
                mask
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard != Bitboard.EMPTY) {
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
        val fromSquare = board.kingSquare[color]
        val theirKing = board.kingSquare[Color.invertColor(color)]
        val moves = BitboardMove.KING_MOVES[fromSquare] and BitboardMove.KING_MOVES[theirKing].inv()

        pieceMovement[color][fromSquare] = moves
        attacksBitboard[color][Piece.KING] = moves
    }
}
