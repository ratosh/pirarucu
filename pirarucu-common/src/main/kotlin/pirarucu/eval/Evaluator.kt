package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue
import pirarucu.util.Utils

object Evaluator {

    fun evaluate(board: Board, attackInfo: AttackInfo): Int {
        board.updateEval(attackInfo)
        var score = TunableConstants.TEMPO[board.colorToMove] +
            board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        score += PawnEvaluator.evaluate(board, attackInfo)

        score += evalKnight(board, attackInfo, Color.WHITE) -
            evalKnight(board, attackInfo, Color.BLACK)

        score += evalBishop(board, attackInfo, Color.WHITE) -
            evalBishop(board, attackInfo, Color.BLACK)

        score += evalRook(board, attackInfo, Color.WHITE) -
            evalRook(board, attackInfo, Color.BLACK)

        score += evalQueen(board, attackInfo, Color.WHITE) -
            evalQueen(board, attackInfo, Color.BLACK)

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        return (mgScore * phase + egScore * (TunableConstants.PHASE_MAX - phase)) /
            TunableConstants.PHASE_MAX
    }

    /**
     * Evaluate knights
     */
    private fun evalKnight(board: Board, attackInfo: AttackInfo, ourColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.KNIGHT]

        // Outpost not attacked squares
        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val outpostProtectedBitboard = board.evalInfo.protectedOutpost[ourColor]
        val outpostUnprotectedBitboard = board.evalInfo.unprotectedOutpost[ourColor]

        var result = 0

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)
            val bitboard = Bitboard.getBitboard(square)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            // A knight is in an outpost square
            if (bitboard and outpostProtectedBitboard != Bitboard.EMPTY) {
                result += TunableConstants.OUTPOST[0][Piece.KNIGHT]
            }
            if (bitboard and outpostUnprotectedBitboard != Bitboard.EMPTY) {
                result += TunableConstants.OUTPOST[1][Piece.KNIGHT]
            }
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.KNIGHT][Utils.specific.bitCount(pieceMobilityBitboard)]

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate bishops
     */
    private fun evalBishop(board: Board, attackInfo: AttackInfo, ourColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.BISHOP]

        // Outpost not attacked squares
        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val outpostProtectedBitboard = board.evalInfo.protectedOutpost[ourColor]
        val outpostUnprotectedBitboard = board.evalInfo.unprotectedOutpost[ourColor]

        var result = 0

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)
            val bitboard = Bitboard.getBitboard(square)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            // A bishop is in an outpost square
            if (bitboard and outpostProtectedBitboard != Bitboard.EMPTY) {
                result += TunableConstants.OUTPOST[0][Piece.BISHOP]
            }
            if (bitboard and outpostUnprotectedBitboard != Bitboard.EMPTY) {
                result += TunableConstants.OUTPOST[1][Piece.BISHOP]
            }
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.BISHOP][Utils.specific.bitCount(pieceMobilityBitboard)]

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate rooks
     */
    private fun evalRook(board: Board, attackInfo: AttackInfo, ourColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.ROOK]

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]

        var result = 0

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.ROOK][Utils.specific.bitCount(pieceMobilityBitboard)]

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate queens
     */
    private fun evalQueen(board: Board, attackInfo: AttackInfo, ourColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.QUEEN]

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]

        var result = 0

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.QUEEN][Utils.specific.bitCount(pieceMobilityBitboard)]

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }
}
