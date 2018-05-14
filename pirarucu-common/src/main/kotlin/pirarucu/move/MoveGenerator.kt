package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.eval.AttackInfo

object MoveGenerator {

    fun legalMoves(board: Board, attackInfo: AttackInfo, moveList: MoveList) {
        attackInfo.update(board, board.colorToMove)
        val ourColor = board.colorToMove
        val checkBitboard = board.basicEvalInfo.checkBitboard
        val mask = when {
            checkBitboard == Bitboard.EMPTY -> {
                castlingMoves(board, moveList)
                board.emptyBitboard
            }
            Bitboard.oneElement(checkBitboard) -> {
                BitboardMove.BETWEEN_BITBOARD[board.basicEvalInfo.kingSquare[ourColor]][Square.getSquare(checkBitboard)]
            }
            else -> {
                Bitboard.EMPTY
            }
        }
        if (mask != Bitboard.EMPTY) {
            generateMoves(board, attackInfo, moveList, Piece.QUEEN, mask)
            generateMoves(board, attackInfo, moveList, Piece.ROOK, mask)
            generateMoves(board, attackInfo, moveList, Piece.BISHOP, mask)
            generateMoves(board, attackInfo, moveList, Piece.KNIGHT, mask)
            legalPawnMoves(board, moveList, mask)
        }
        generateMoves(board, attackInfo, moveList, Piece.KING, board.emptyBitboard)
    }

    private fun legalPawnMoves(board: Board, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
        val emptyBitboard = board.emptyBitboard
        var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            val fromBitboard = Bitboard.getBitboard(fromSquare)
            var bitboard = BitboardMove.PAWN_MOVES[ourColor][fromSquare] and emptyBitboard

            if (bitboard != Bitboard.EMPTY) {
                bitboard = bitboard or (BitboardMove.DOUBLE_PAWN_MOVES[ourColor][fromSquare] and
                    emptyBitboard)
            }

            if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != Bitboard.EMPTY) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }
            bitboard = bitboard and maskBitboard

            while (bitboard != Bitboard.EMPTY) {
                val toSquare = Square.getSquare(bitboard)
                val toBitboard = Bitboard.getBitboard(toSquare)
                when {
                    Bitboard.PROMOTION_BITBOARD and toBitboard != Bitboard.EMPTY -> {
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_KNIGHT))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_BISHOP))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_ROOK))
                        moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                            MoveType.TYPE_PROMOTION_QUEEN))
                    }
                    else -> moveList.addMove(Move.createMove(fromSquare, toSquare))
                }
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun generateMoves(board: Board,
                              attackInfo: AttackInfo,
                              moveList: MoveList,
                              movedPiece: Int,
                              maskBitboard: Long) {
        if (maskBitboard == Bitboard.EMPTY) {
            return
        }
        val ourColor = board.colorToMove
        var tmpPieces = board.pieceBitboard[ourColor][movedPiece]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            var bitboard = attackInfo.pieceMovement[ourColor][fromSquare] and maskBitboard

            while (bitboard != Bitboard.EMPTY) {
                val toSquare = Square.getSquare(bitboard)
                moveList.addMove(Move.createMove(fromSquare, toSquare))
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
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

            if ((kingPath or rookPath) and board.gameBitboard == Bitboard.EMPTY) {
                val theirColor = Color.invertColor(board.colorToMove)

                if (!pathUnderAttack(kingPath, ourColor, board.pieceBitboard[theirColor],
                        board.gameBitboard)) {
                    moveList.addMove(Move.createCastlingMove(kingSquare, kingTo))
                }
            }
            castlingIndexes = castlingIndexes and castlingIndexes - 1
        }
    }

    fun legalAttacks(board: Board, attackInfo: AttackInfo, moveList: MoveList) {
        val theirColor = board.nextColorToMove
        val theirBitboard = board.colorBitboard[theirColor]
        val ourColor = board.colorToMove
        val checkBitboard = board.basicEvalInfo.checkBitboard

        attackInfo.update(board, ourColor)

        val mask = when {
            checkBitboard == Bitboard.EMPTY -> {
                theirBitboard
            }
            Bitboard.oneElement(checkBitboard) -> {
                checkBitboard
            }
            else -> {
                Bitboard.EMPTY
            }
        }
        if (mask != Bitboard.EMPTY) {
            for (capturedPiece in Piece.QUEEN downTo Piece.PAWN) {
                for (movedPiece in Piece.PAWN until Piece.KING) {
                    generateAttacks(board, attackInfo, moveList, movedPiece, capturedPiece,
                        board.pieceBitboard[theirColor], mask)
                }
            }
            legalPawnEPCapture(board, moveList, mask)

        }
        legalKingMoves(board, attackInfo, moveList, theirBitboard)
    }

    private fun legalKingMoves(board: Board, attackInfo: AttackInfo, moveList: MoveList, maskBitboard: Long) {
        val ourColor = board.colorToMove
        val fromSquare = board.basicEvalInfo.kingSquare[ourColor]
        var bitboard = attackInfo.pieceMovement[ourColor][fromSquare] and maskBitboard
        while (bitboard != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(bitboard)
            moveList.addMove(Move.createMove(fromSquare, toSquare))
            bitboard = bitboard and bitboard - 1
        }
    }

    private fun generateAttacks(board: Board,
                                attackInfo: AttackInfo,
                                moveList: MoveList,
                                movedPiece: Int,
                                capturedPiece: Int,
                                theirPieceBitboard: LongArray,
                                maskBitboard: Long) {
        val pieceMask = maskBitboard and theirPieceBitboard[capturedPiece]
        if (pieceMask == Bitboard.EMPTY) {
            return
        }
        val ourColor = board.colorToMove
        var tmpPieces = board.pieceBitboard[ourColor][movedPiece]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            var bitboard = attackInfo.pieceMovement[ourColor][fromSquare] and pieceMask

            while (bitboard != Bitboard.EMPTY) {
                val toSquare = Square.getSquare(bitboard)
                if (movedPiece == Piece.PAWN) {
                    val toBitboard = Bitboard.getBitboard(toSquare)
                    when {
                        Bitboard.PROMOTION_BITBOARD and toBitboard != Bitboard.EMPTY -> {
                            moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                                MoveType.TYPE_PROMOTION_KNIGHT))
                            moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                                MoveType.TYPE_PROMOTION_BISHOP))
                            moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                                MoveType.TYPE_PROMOTION_ROOK))
                            moveList.addMove(Move.createPromotionMove(fromSquare, toSquare,
                                MoveType.TYPE_PROMOTION_QUEEN))
                        }
                        else -> moveList.addMove(Move.createMove(fromSquare, toSquare))
                    }
                } else {
                    moveList.addMove(Move.createMove(fromSquare, toSquare))
                }
                bitboard = bitboard and bitboard - 1
            }
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun legalPawnEPCapture(board: Board, moveList: MoveList, maskBitboard: Long) {
        val epSquare = board.epSquare
        if (epSquare != Square.NONE) {
            val theirColor = board.nextColorToMove
            val epPawnBitboard = BitboardMove.PAWN_MOVES[theirColor][epSquare] and
                maskBitboard
            if (epPawnBitboard != Bitboard.EMPTY) {
                val ourColor = board.colorToMove
                val ourKingSquare = board.basicEvalInfo.kingSquare[ourColor]
                val gameBitboard = board.gameBitboard xor epPawnBitboard
                val theirPieceBitboard = board.pieceBitboard[theirColor]
                var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN] and
                    BitboardMove.PAWN_ATTACKS[theirColor][epSquare]
                while (tmpPieces != Bitboard.EMPTY) {
                    val fromSquare = Square.getSquare(tmpPieces)
                    val fromBitboard = Bitboard.getBitboard(fromSquare)
                    val tmpBitboard = gameBitboard xor fromBitboard xor
                        Bitboard.getBitboard(epSquare)

                    if (squareAttackedBitboard(ourKingSquare, ourColor, theirPieceBitboard, tmpBitboard) ==
                        Bitboard.EMPTY) {
                        moveList.addMove(Move.createPassantMove(fromSquare, epSquare))
                    }
                    tmpPieces = tmpPieces and tmpPieces - 1
                }
            }
        }
    }

    fun pathUnderAttack(path: Long, ourColor: Int, theirPieceBitboard: LongArray,
                        gameBitboard: Long): Boolean {
        var tmpPath = path
        while (tmpPath != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPath)
            if (squareAttackedBitboard(square, ourColor, theirPieceBitboard, gameBitboard) != Bitboard.EMPTY) {
                return true
            }
            tmpPath = tmpPath and tmpPath - 1
        }
        return false
    }

    fun squareAttackedBitboard(square: Int, ourColor: Int, theirPieceBitboard: LongArray,
                               gameBitboard: Long): Long {
        val pawns = theirPieceBitboard[Piece.PAWN] and gameBitboard
        val knights = theirPieceBitboard[Piece.KNIGHT] and gameBitboard
        val bishops = (theirPieceBitboard[Piece.BISHOP] or
            theirPieceBitboard[Piece.QUEEN]) and gameBitboard
        val rooks = (theirPieceBitboard[Piece.ROOK] or theirPieceBitboard[Piece.QUEEN]) and
            gameBitboard
        var result = (pawns and BitboardMove.PAWN_ATTACKS[ourColor][square]) or
            (knights and BitboardMove.KNIGHT_MOVES[square]) or
            (BitboardMove.KING_MOVES[square] and theirPieceBitboard[Piece.KING])

        if (bishops != Bitboard.EMPTY) {
            result = result or (bishops and BitboardMove.bishopMoves(square, gameBitboard))
        }
        if (rooks != Bitboard.EMPTY) {
            result = result or (rooks and BitboardMove.rookMoves(square, gameBitboard))
        }
        return result
    }

    fun isLegalQuietMove(board: Board, attackInfo: AttackInfo, move: Int): Boolean {
        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        if (board.pieceTypeBoard[toSquare] != Piece.NONE) {
            return false
        }
        val ourColor = board.colorToMove
        val fromBitboard = Bitboard.getBitboard(fromSquare)
        if (board.colorBitboard[ourColor] and fromBitboard == Bitboard.EMPTY) {
            return false
        }
        when (board.pieceTypeBoard[fromSquare]) {
            Piece.NONE -> {
                return false
            }
            Piece.PAWN -> {
                val checkBitboard = board.basicEvalInfo.checkBitboard
                val mask = when {
                    checkBitboard == Bitboard.EMPTY -> {
                        board.emptyBitboard
                    }
                    Bitboard.oneElement(checkBitboard) -> {
                        val square = Square.getSquare(checkBitboard)
                        BitboardMove.BETWEEN_BITBOARD[board.basicEvalInfo.kingSquare[ourColor]][square]
                    }
                    else -> {
                        return false
                    }
                }
                var bitboard = BitboardMove.PAWN_MOVES[ourColor][fromSquare] and mask

                if (bitboard != Bitboard.EMPTY) {
                    bitboard = bitboard or (BitboardMove.DOUBLE_PAWN_MOVES[ourColor][fromSquare] and
                        mask)
                }

                if (fromBitboard and board.basicEvalInfo.pinnedBitboard[ourColor] != Bitboard.EMPTY) {
                    val kingSquare = board.basicEvalInfo.kingSquare[ourColor]
                    bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
                }

                val toBitboard = Bitboard.getBitboard(toSquare)
                return (toBitboard and bitboard != Bitboard.EMPTY)
            }
            else -> {
                attackInfo.update(board, ourColor)

                val moveBitboard = attackInfo.pieceMovement[ourColor][fromSquare]
                if (moveBitboard != Bitboard.EMPTY) {
                    val toBitboard = Bitboard.getBitboard(toSquare)
                    return moveBitboard and toBitboard != Bitboard.EMPTY
                }
            }
        }
        return false
    }
}
