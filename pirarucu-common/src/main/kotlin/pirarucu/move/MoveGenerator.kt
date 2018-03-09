package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square

object MoveGenerator {

    fun legalMoves(board: Board, moveList: MoveList) {
        var mask = board.emptyBitboard
        legalKingMoves(board, moveList, mask)
        val ourColor = board.colorToMove
        val checkBitboard = board.basicEvalInfo.checkBitboard[ourColor]
        when {
            checkBitboard == 0L -> {
                castlingMoves(board, moveList)
                legalRookMoves(board, moveList, mask)
                legalBishopMoves(board, moveList, mask)
                legalKnightMoves(board, moveList, mask)
                legalPawnMoves(board, moveList, mask)
            }
            Bitboard.oneElement(checkBitboard) -> {
                val square = Square.getSquare(checkBitboard)
                val betweenBitboard =
                    BitboardMove.BETWEEN_BITBOARD[board.basicEvalInfo.kingSquare[ourColor]][square]
                mask = mask and betweenBitboard
                legalRookMoves(board, moveList, mask)
                legalBishopMoves(board, moveList, mask)
                legalKnightMoves(board, moveList, mask)
                legalPawnMoves(board, moveList, mask)
            }
        }
    }

    private fun legalPawnMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        val emptyBitboard = board.emptyBitboard
        var pieces = board.pieceBitboard[ourColor][Piece.PAWN]
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.PAWN_MOVES[ourColor][fromSquare] and emptyBitboard

            if (bitboard != 0L) {
                bitboard = bitboard or BitboardMove.DOUBLE_PAWN_MOVES[ourColor][fromSquare] and
                    emptyBitboard
            }

            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }
            bitboard = bitboard and maskBitboard

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                val toBitboard = Bitboard.getBitboard(toSquare)
                when {
                    Bitboard.PROMOTION_BITBOARD and toBitboard != 0L -> {
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_KNIGHT))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_BISHOP))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_ROOK))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_QUEEN))
                    }
                    else -> moveList.addMove(Move.createMove(fromSquare, toSquare, Piece.PAWN))
                }
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalKnightMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        var pieces = board.pieceBitboard[ourColor][Piece.KNIGHT] and
            board.basicEvalInfo.pinnedBitboard[ourColor].inv()
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            var bitboard = BitboardMove.KNIGHT_MOVES[fromSquare] and maskBitboard

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createMove(fromSquare, toSquare, Piece.KNIGHT))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalBishopMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        var pieces = board.pieceBitboard[ourColor][Piece.BISHOP] or
            board.pieceBitboard[ourColor][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.bishopMoves(fromSquare, board.gameBitboard) and
                maskBitboard
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createMove(fromSquare, toSquare,
                    board.pieceTypeBoard[fromSquare]))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalRookMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        var pieces = board.pieceBitboard[ourColor][Piece.ROOK] or
            board.pieceBitboard[ourColor][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.rookMoves(fromSquare, board.gameBitboard) and
                maskBitboard
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createMove(fromSquare, toSquare,
                    board.pieceTypeBoard[fromSquare]))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalKingMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val fromSquare = board.basicEvalInfo.kingSquare[board.colorToMove]
        val fromBitboard = Bitboard.getBitboard(fromSquare)
        var moves = BitboardMove.KING_MOVES[fromSquare] and maskBitboard
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        val gameBitboard = board.gameBitboard xor fromBitboard
        while (moves != 0L) {
            val toSquare = Square.getSquare(moves)
            if (!squareUnderAttack(toSquare, ourColor, board.pieceBitboard[theirColor],
                    gameBitboard)) {
                moveList.addMove(Move.createMove(fromSquare, toSquare, Piece.KING))
            }
            moves = moves and moves - 1
        }
    }

    private fun castlingMoves(board: Board, moveList: MoveList) {
        val ourColor = board.colorToMove
        var castlingIndexes = CastlingRights.filterCastlingRight(ourColor,
            board.castlingRights)
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        while (castlingIndexes != 0) {
            val castlingIndex = Square.getSquare(castlingIndexes)
            val kingTo = CastlingRights.KING_FINAL_SQUARE[castlingIndex]
            val kingPath = BitboardMove.BETWEEN_BITBOARD[kingSquare][kingTo] or
                Bitboard.getBitboard(kingTo)

            val rookFrom = board.initialRookSquare[castlingIndex]
            val rookTo = CastlingRights.ROOK_FINAL_SQUARE[castlingIndex]
            val rookPath = BitboardMove.BETWEEN_BITBOARD[rookFrom][rookTo]

            if ((kingPath or rookPath) and board.gameBitboard == 0L) {
                val theirColor = Color.invertColor(board.colorToMove)

                if (!pathUnderAttack(kingPath, ourColor, board.pieceBitboard[theirColor],
                        board.gameBitboard)) {
                    moveList.addMove(Move.createCastlingMove(kingSquare, kingTo))
                }
            }
            castlingIndexes = castlingIndexes and castlingIndexes - 1
        }
    }

    fun legalAttacks(board: Board, moveList: MoveList) {
        val theirColor = board.nextColorToMove
        val theirBitboard = board.colorBitboard[theirColor]
        val ourColor = board.colorToMove
        val checkBitboard = board.basicEvalInfo.checkBitboard[ourColor]

        legalKingAttacks(board, moveList, theirBitboard)
        when {
            checkBitboard == 0L -> {
                legalPawnEPCapture(board, moveList, theirBitboard)
                legalPawnAttacks(board, moveList, theirBitboard)
                legalKnightAttacks(board, moveList, theirBitboard)
                legalBishopAttacks(board, moveList, theirBitboard)
                legalRookAttacks(board, moveList, theirBitboard)
            }
            Bitboard.oneElement(checkBitboard) -> {
                legalPawnEPCapture(board, moveList, checkBitboard)
                legalPawnAttacks(board, moveList, checkBitboard)
                legalKnightAttacks(board, moveList, checkBitboard)
                legalBishopAttacks(board, moveList, checkBitboard)
                legalRookAttacks(board, moveList, checkBitboard)
            }
        }
    }

    private fun legalPawnEPCapture(board: Board, moveList: MoveList, maskBitboard: Long) {
        val theirColor = board.nextColorToMove
        val epSquare = board.epSquare
        if (epSquare != Square.NONE) {
            var epPawnBitboard = BitboardMove.PAWN_MOVES[theirColor][epSquare] and
                maskBitboard
            if (epPawnBitboard != 0L) {
                val ourColor = board.colorToMove
                val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
                val gameBitboard = board.gameBitboard xor epPawnBitboard
                val theirPieceBitboard = board.pieceBitboard[theirColor]
                var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN] and
                    BitboardMove.PAWN_ATTACKS[theirColor][epSquare]
                while (tmpPieces != 0L) {
                    val fromSquare = Square.getSquare(tmpPieces)
                    val fromBitboard = Bitboard.getBitboard(fromSquare)
                    val tmpBitboard = gameBitboard xor fromBitboard xor
                        Bitboard.getBitboard(epSquare)

                    if (!squareUnderAttack(kingSquare, ourColor, theirPieceBitboard, tmpBitboard)) {
                        moveList.addMove(Move.createPassantMove(fromSquare, epSquare))
                    }
                    tmpPieces = tmpPieces and tmpPieces - 1
                }
            }
        }
    }

    private fun legalPawnAttacks(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN]
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.PAWN_ATTACKS[ourColor][fromSquare] and maskBitboard
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                val toBitboard = Bitboard.getBitboard(toSquare)
                when {
                    Bitboard.PROMOTION_BITBOARD and toBitboard != 0L -> {
                        moveList.addMove(Move.createPromotionAttack(fromSquare, toSquare,
                            board.pieceTypeBoard[toSquare], MoveType.TYPE_PROMOTION_KNIGHT))
                        moveList.addMove(Move.createPromotionAttack(fromSquare, toSquare,
                            board.pieceTypeBoard[toSquare], MoveType.TYPE_PROMOTION_BISHOP))
                        moveList.addMove(Move.createPromotionAttack(fromSquare, toSquare,
                            board.pieceTypeBoard[toSquare], MoveType.TYPE_PROMOTION_ROOK))
                        moveList.addMove(Move.createPromotionAttack(fromSquare, toSquare,
                            board.pieceTypeBoard[toSquare], MoveType.TYPE_PROMOTION_QUEEN))
                    }
                    else -> moveList.addMove(Move.createAttackMove(fromSquare, toSquare, Piece.PAWN,
                        board.pieceTypeBoard[toSquare]))
                }
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalKnightAttacks(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        var pieces = board.pieceBitboard[ourColor][Piece.KNIGHT] and
            board.basicEvalInfo.pinnedBitboard[ourColor].inv()
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            var bitboard = BitboardMove.KNIGHT_MOVES[fromSquare] and maskBitboard

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createAttackMove(fromSquare, toSquare, Piece.KNIGHT,
                    board.pieceTypeBoard[toSquare]))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalBishopAttacks(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        var pieces = board.pieceBitboard[ourColor][Piece.BISHOP] or
            board.pieceBitboard[ourColor][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.bishopMoves(fromSquare, board.gameBitboard) and
                maskBitboard
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createAttackMove(fromSquare, toSquare,
                    board.pieceTypeBoard[fromSquare], board.pieceTypeBoard[toSquare]))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalRookAttacks(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        var pieces = board.pieceBitboard[ourColor][Piece.ROOK] or
            board.pieceBitboard[ourColor][Piece.QUEEN]
        var tmpPieces = pieces
        while (tmpPieces != 0L) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.rookMoves(fromSquare, board.gameBitboard) and
                maskBitboard
            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != 0L) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }

            while (bitboard != 0L) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createAttackMove(fromSquare, toSquare,
                    board.pieceTypeBoard[fromSquare], board.pieceTypeBoard[toSquare]))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalKingAttacks(board: Board, moveList: MoveList, maskBitboard: Long) {
        val fromSquare = board.basicEvalInfo.kingSquare[board.colorToMove]
        val fromBitboard = Bitboard.getBitboard(fromSquare)
        var moves = BitboardMove.KING_MOVES[fromSquare] and maskBitboard
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        val gameBitboard = board.gameBitboard xor fromBitboard
        while (moves != 0L) {
            val toSquare = Square.getSquare(moves)
            if (!squareUnderAttack(toSquare, ourColor, board.pieceBitboard[theirColor],
                    gameBitboard)) {
                moveList.addMove(Move.createAttackMove(fromSquare, toSquare, Piece.KING,
                    board.pieceTypeBoard[toSquare]))
            }
            moves = moves and moves - 1
        }
    }

    private fun pathUnderAttack(path: Long, ourColor: Int, theirPieceBitboard: LongArray,
        gameBitboard: Long): Boolean {
        var tmpPath = path
        while (tmpPath != 0L) {
            val square = Square.getSquare(tmpPath)
            if (squareUnderAttack(square, ourColor, theirPieceBitboard, gameBitboard)) {
                return true
            }
            tmpPath = tmpPath and tmpPath - 1
        }
        return false
    }

    private fun squareUnderAttack(square: Int, ourColor: Int, theirPieceBitboard: LongArray,
        gameBitboard: Long): Boolean {
        val pawns = theirPieceBitboard[Piece.PAWN] and gameBitboard
        val knights = theirPieceBitboard[Piece.KNIGHT] and gameBitboard
        val bishops = (theirPieceBitboard[Piece.BISHOP] or
            theirPieceBitboard[Piece.QUEEN]) and gameBitboard
        val rooks = (theirPieceBitboard[Piece.ROOK] or theirPieceBitboard[Piece.QUEEN]) and
            gameBitboard
        return (pawns != 0L && BitboardMove.PAWN_ATTACKS[ourColor][square] and pawns != 0L)
            || (knights != 0L && BitboardMove.KNIGHT_MOVES[square] and knights != 0L)
            || (bishops != 0L && BitboardMove.bishopMoves(square, gameBitboard) and bishops != 0L)
            || (rooks != 0L && BitboardMove.rookMoves(square, gameBitboard) and rooks != 0L)
    }
}
