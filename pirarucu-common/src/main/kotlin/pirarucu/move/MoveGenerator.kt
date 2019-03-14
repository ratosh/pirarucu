package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.CastlingRights
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.eval.AttackInfo
import pirarucu.search.History
import pirarucu.tuning.TunableConstants

class MoveGenerator(private val history: History) {

    fun legalMoves(board: Board, attackInfo: AttackInfo, moveList: OrderedMoveList) {
        attackInfo.update(board, board.colorToMove)
        val ourColor = board.colorToMove
        val checkBitboard = board.basicEvalInfo.checkBitboard
        if (checkBitboard == Bitboard.EMPTY) {
            castlingMoves(board, moveList)
        }

        val mask = attackInfo.movementMask[ourColor] and board.emptyBitboard
        if (mask != Bitboard.EMPTY) {
            generateQuietMoves(board, attackInfo, moveList, Piece.QUEEN, mask)
            generateQuietMoves(board, attackInfo, moveList, Piece.ROOK, mask)
            generateQuietMoves(board, attackInfo, moveList, Piece.BISHOP, mask)
            generateQuietMoves(board, attackInfo, moveList, Piece.KNIGHT, mask)
            generateQuietPawnMoves(board, attackInfo, moveList, mask)
        }
        generateQuietMoves(board, attackInfo, moveList, Piece.KING, board.emptyBitboard)
    }

    private fun generateQuietPawnMoves(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        mask: Long
    ) {
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        var quietPieces = Bitboard.FROM_PROMOTION_BITBOARD[ourColor].inv() and
            board.pieceBitboard[ourColor][Piece.PAWN] and
            BitboardMove.pawnForward(theirColor, board.emptyBitboard)
        val kingSquare = board.kingSquare[ourColor]
        val emptyBitboardMask = board.emptyBitboard
        while (quietPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(quietPieces)
            var bitboard = (BitboardMove.PAWN_MOVES[ourColor][fromSquare] and emptyBitboardMask) or
                (BitboardMove.DOUBLE_PAWN_MOVES[ourColor][fromSquare] and emptyBitboardMask)

            val fromBitboard = Bitboard.getBitboard(fromSquare)

            if (fromBitboard and board.basicEvalInfo.pinnedBitboard != Bitboard.EMPTY) {
                bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
            }
            bitboard = bitboard and mask

            generateQuietMoves(moveList, ourColor, fromSquare, bitboard)

            quietPieces = quietPieces and quietPieces - 1
        }
    }

    private fun generateQuietMoves(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        movedPiece: Int,
        maskBitboard: Long
    ) {
        val ourColor = board.colorToMove
        val maskedMove = maskBitboard and attackInfo.attacksBitboard[ourColor][movedPiece]
        if (maskedMove == Bitboard.EMPTY) {
            return
        }
        var tmpPieces = board.pieceBitboard[ourColor][movedPiece]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)

            generateQuietMoves(
                moveList, ourColor, fromSquare,
                attackInfo.pieceMovement[ourColor][fromSquare] and board.emptyBitboard
            )

            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun castlingMoves(board: Board, moveList: OrderedMoveList) {
        val ourColor = board.colorToMove
        var castlingIndexes = CastlingRights.filterCastlingRight(
            ourColor,
            board.castlingRights
        )
        val kingSquare = board.kingSquare[ourColor]
        while (castlingIndexes != 0) {
            val castlingIndex = Square.getSquare(castlingIndexes)
            val kingTo = CastlingRights.KING_FINAL_SQUARE[castlingIndex]
            val kingPath = BitboardMove.BETWEEN_BITBOARD[kingSquare][kingTo] or
                Bitboard.getBitboard(kingTo)

            val rookFrom = board.initialRookSquare[castlingIndex]
            val rookTo = CastlingRights.ROOK_FINAL_SQUARE[castlingIndex]
            val rookPath = BitboardMove.BETWEEN_BITBOARD[rookFrom][rookTo]

            if ((kingPath or rookPath) and board.gameBitboard == Bitboard.EMPTY) {
                val move = Move.createCastlingMove(kingSquare, kingTo)
                moveList.addMove(move, history.getHistoryScore(ourColor, move))
            }
            castlingIndexes = castlingIndexes and castlingIndexes - 1
        }
    }

    fun legalAttacks(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList
    ) {
        val theirColor = board.nextColorToMove
        val theirBitboard = board.colorBitboard[theirColor]
        val ourColor = board.colorToMove

        attackInfo.update(board, ourColor)

        val mask = attackInfo.movementMask[ourColor] and theirBitboard
        if (mask != Bitboard.EMPTY) {
            generateCapturePromotions(board, attackInfo, moveList, mask)
            generateNoCapturePromotions(board, attackInfo, moveList)
            generateNoPromotionPawnCaptures(board, attackInfo, moveList, mask)
            generateAttacks(board, attackInfo, moveList, Piece.KNIGHT, mask)
            generateAttacks(board, attackInfo, moveList, Piece.BISHOP, mask)
            generateAttacks(board, attackInfo, moveList, Piece.ROOK, mask)
            generateAttacks(board, attackInfo, moveList, Piece.QUEEN, mask)
            legalPawnEPCapture(board, moveList, mask)
        }
        legalKingCaptures(board, attackInfo, moveList, theirBitboard)
    }

    private fun legalKingCaptures(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        mask: Long
    ) {
        val ourColor = board.colorToMove
        val fromSquare = board.kingSquare[ourColor]
        val bitboard = attackInfo.pieceMovement[ourColor][fromSquare] and mask
        generateCaptures(board, moveList, fromSquare, bitboard)
    }

    private fun generateCapturePromotions(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        mask: Long
    ) {
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        var promotionAttacks = attackInfo.attacksBitboard[ourColor][Piece.PAWN] and
            Bitboard.PROMOTION_BITBOARD and mask
        val kingSquare = board.kingSquare[ourColor]
        val pawnBitboard = board.pieceBitboard[ourColor][Piece.PAWN]

        while (promotionAttacks != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(promotionAttacks)
            val toBitboard = Bitboard.getBitboard(toSquare)
            var bitboard = BitboardMove.PAWN_ATTACKS[theirColor][toSquare] and pawnBitboard

            while (bitboard != Bitboard.EMPTY) {
                val fromSquare = Square.getSquare(bitboard)
                val fromBitboard = Bitboard.getBitboard(fromSquare)
                // Skip pinned pieces not attacking the pinner
                if (fromBitboard and board.basicEvalInfo.pinnedBitboard == Bitboard.EMPTY ||
                    BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare] and toBitboard != Bitboard.EMPTY
                ) {
                    createPromotions(board, moveList, fromSquare, toSquare)
                }
                bitboard = bitboard and bitboard - 1
            }

            promotionAttacks = promotionAttacks and promotionAttacks - 1
        }
    }

    private fun generateNoCapturePromotions(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList
    ) {
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        val pawnBitboard = board.pieceBitboard[ourColor][Piece.PAWN] and board.basicEvalInfo.pinnedBitboard.inv()
        var promotionLocation = BitboardMove.pawnForward(ourColor, pawnBitboard) and
            Bitboard.PROMOTION_BITBOARD and
            board.emptyBitboard and
            attackInfo.movementMask[ourColor]
        while (promotionLocation != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(promotionLocation)
            val fromSquare = toSquare + BitboardMove.PAWN_FORWARD[theirColor]

            createPromotions(board, moveList, fromSquare, toSquare)

            promotionLocation = promotionLocation and promotionLocation - 1
        }
    }

    private fun generateNoPromotionPawnCaptures(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        mask: Long
    ) {
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove

        var pawnAttacks = attackInfo.attacksBitboard[ourColor][Piece.PAWN] and
            mask and Bitboard.PROMOTION_BITBOARD.inv()
        val kingSquare = board.kingSquare[ourColor]
        val pawnBitboard = board.pieceBitboard[ourColor][Piece.PAWN]

        while (pawnAttacks != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(pawnAttacks)
            val toBitboard = Bitboard.getBitboard(toSquare)
            var bitboard = BitboardMove.PAWN_ATTACKS[theirColor][toSquare] and pawnBitboard

            while (bitboard != Bitboard.EMPTY) {
                val fromSquare = Square.getSquare(bitboard)
                val fromBitboard = Bitboard.getBitboard(fromSquare)
                // Skip pinned pieces not attacking the pinner
                if (fromBitboard and board.basicEvalInfo.pinnedBitboard == Bitboard.EMPTY ||
                    BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare] and toBitboard != Bitboard.EMPTY
                ) {
                    registerExchangeMove(
                        moveList, true, Move.createMove(fromSquare, toSquare),
                        getMVVLVAScore(Piece.PAWN, board.pieceTypeBoard[toSquare])
                    )
                }
                bitboard = bitboard and bitboard - 1
            }

            pawnAttacks = pawnAttacks and pawnAttacks - 1
        }
    }

    private fun generateAttacks(
        board: Board,
        attackInfo: AttackInfo,
        moveList: OrderedMoveList,
        movedPiece: Int,
        maskBitboard: Long
    ) {
        val ourColor = board.colorToMove
        val theirColor = board.nextColorToMove
        val targetMask = board.colorBitboard[theirColor] and attackInfo.attacksBitboard[ourColor][movedPiece] and
            maskBitboard
        if (targetMask == Bitboard.EMPTY) {
            return
        }
        var tmpPieces = board.pieceBitboard[ourColor][movedPiece]
        while (tmpPieces != Bitboard.EMPTY) {
            val fromSquare = Square.getSquare(tmpPieces)
            generateCaptures(board, moveList, fromSquare, attackInfo.pieceMovement[ourColor][fromSquare] and targetMask)
            tmpPieces = tmpPieces and tmpPieces - 1
        }
    }

    private fun generateCaptures(
        board: Board,
        moveList: OrderedMoveList,
        fromSquare: Int,
        targetBitboard: Long
    ) {
        var tmpTargetBitboard = targetBitboard
        while (tmpTargetBitboard != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(tmpTargetBitboard)
            registerExchangeMove(
                moveList, true, Move.createMove(fromSquare, toSquare),
                getMVVLVAScore(board.pieceTypeBoard[fromSquare], board.pieceTypeBoard[toSquare])
            )
            tmpTargetBitboard = tmpTargetBitboard and tmpTargetBitboard - 1
        }
    }

    private fun legalPawnEPCapture(
        board: Board,
        moveList: OrderedMoveList,
        maskBitboard: Long
    ) {
        val epSquare = board.epSquare
        if (epSquare != Square.NONE) {
            val theirColor = board.nextColorToMove
            val epPawnBitboard = BitboardMove.PAWN_MOVES[theirColor][epSquare] and
                maskBitboard
            if (epPawnBitboard != Bitboard.EMPTY) {
                val ourColor = board.colorToMove
                var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN] and
                    BitboardMove.PAWN_ATTACKS[theirColor][epSquare]
                while (tmpPieces != Bitboard.EMPTY) {
                    val fromSquare = Square.getSquare(tmpPieces)

                    moveList.addMove(
                        Move.createPassantMove(fromSquare, epSquare),
                        getMVVLVAScore(Piece.PAWN, Piece.PAWN)
                    )

                    tmpPieces = tmpPieces and tmpPieces - 1
                }
            }
        }
    }

    private fun generateQuietMoves(
        moveList: OrderedMoveList,
        color: Int,
        fromSquare: Int,
        toBitboard: Long
    ) {
        var tmpToBitboard = toBitboard
        while (tmpToBitboard != Bitboard.EMPTY) {
            val toSquare = Square.getSquare(tmpToBitboard)
            val move = Move.createMove(fromSquare, toSquare)

            moveList.addMove(move, history.getHistoryScore(color, move))
            tmpToBitboard = tmpToBitboard and tmpToBitboard - 1
        }
    }

    private fun createPromotions(
        board: Board,
        moveList: OrderedMoveList,
        fromSquare: Int,
        toSquare: Int
    ) {
        registerExchangeMove(
            moveList, true,
            Move.createPromotionMove(fromSquare, toSquare, MoveType.TYPE_PROMOTION_QUEEN),
            getMVVLVAScore(Piece.PAWN, board.pieceTypeBoard[toSquare]) + TunableConstants.SEE_VALUE[Piece.QUEEN]
        )

        registerExchangeMove(
            moveList, false,
            Move.createPromotionMove(fromSquare, toSquare, MoveType.TYPE_PROMOTION_ROOK),
            getMVVLVAScore(Piece.PAWN, board.pieceTypeBoard[toSquare]) + Piece.ROOK
        )

        registerExchangeMove(
            moveList, false,
            Move.createPromotionMove(fromSquare, toSquare, MoveType.TYPE_PROMOTION_BISHOP),
            getMVVLVAScore(Piece.PAWN, board.pieceTypeBoard[toSquare]) + Piece.BISHOP
        )

        registerExchangeMove(
            moveList, false,
            Move.createPromotionMove(fromSquare, toSquare, MoveType.TYPE_PROMOTION_KNIGHT),
            getMVVLVAScore(Piece.PAWN, board.pieceTypeBoard[toSquare]) + Piece.KNIGHT
        )
    }

    private fun registerExchangeMove(
        moveList: OrderedMoveList,
        goodExchange: Boolean,
        move: Int,
        score: Int
    ) {
        if (goodExchange) {
            moveList.addMove(move, score)
        } else {
            moveList.addMove(move, score - TunableConstants.SEE_VALUE[Piece.QUEEN])
        }
    }

    private fun getMVVLVAScore(attackerPiece: Int, victimPiece: Int): Int {
        return TunableConstants.SEE_VALUE[victimPiece] - attackerPiece
    }

    companion object {

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
                    val toBitboard = Bitboard.getBitboard(toSquare)
                    if (toBitboard and Bitboard.PROMOTION_BITBOARD != Bitboard.EMPTY) {
                        return false
                    }
                    val checkBitboard = board.basicEvalInfo.checkBitboard
                    val mask = when {
                        checkBitboard == Bitboard.EMPTY -> {
                            board.emptyBitboard
                        }
                        Bitboard.oneElement(checkBitboard) -> {
                            val square = Square.getSquare(checkBitboard)
                            BitboardMove.BETWEEN_BITBOARD[board.kingSquare[ourColor]][square]
                        }
                        else -> {
                            return false
                        }
                    }
                    var bitboard = BitboardMove.PAWN_MOVES[ourColor][fromSquare] and board.emptyBitboard

                    if (bitboard != Bitboard.EMPTY) {
                        bitboard = bitboard or (BitboardMove.DOUBLE_PAWN_MOVES[ourColor][fromSquare] and
                            board.emptyBitboard)
                    }
                    bitboard = bitboard and mask

                    if (fromBitboard and board.basicEvalInfo.pinnedBitboard != Bitboard.EMPTY) {
                        val kingSquare = board.kingSquare[ourColor]
                        bitboard = bitboard and BitboardMove.PINNED_MOVE_MASK[kingSquare][fromSquare]
                    }

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

        fun pathUnderAttack(
            path: Long, ourColor: Int, theirPieceBitboard: LongArray,
            gameBitboard: Long
        ): Boolean {
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

        fun squareAttackedBitboard(
            square: Int, ourColor: Int, theirPieceBitboard: LongArray,
            gameBitboard: Long
        ): Long {
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
    }
}
