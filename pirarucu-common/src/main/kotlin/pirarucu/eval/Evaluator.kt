package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.board.factory.FenFactory
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue
import pirarucu.util.Utils
import kotlin.math.min

object Evaluator {

    fun evaluate(board: Board, attackInfo: AttackInfo): Int {
        board.updateEval(attackInfo)
        var score = TunableConstants.TEMPO[board.colorToMove] +
            board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]

        score += PawnEvaluator.evaluate(board, attackInfo)

        score += evalKnight(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalKnight(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalBishop(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalBishop(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalRook(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalRook(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalQueen(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalQueen(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalKing(board, attackInfo, Color.WHITE) -
            evalKing(board, attackInfo, Color.BLACK)

        val mgScore = SplitValue.getFirstPart(score)
        val egScore = SplitValue.getSecondPart(score)

        val phase = board.phase

        return (mgScore * phase + egScore * (TunableConstants.PHASE_MAX - phase)) /
            TunableConstants.PHASE_MAX
    }

    /**
     * Evaluate knights
     */
    private fun evalKnight(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.KNIGHT]
        if (tmpPieces == Bitboard.EMPTY) {
            return 0
        }

        // Outpost not attacked squares
        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val outpostProtectedBitboard = board.evalInfo.protectedOutpost[ourColor]
        val outpostUnprotectedBitboard = board.evalInfo.unprotectedOutpost[ourColor]

        var result = 0

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.KNIGHT] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

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

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += Utils.specific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.KNIGHT]
            }

            val safeChecks = possibleSafeCheck and attackBitboard

            if (safeChecks != Bitboard.EMPTY) {
                result += TunableConstants.SAFE_CHECK_THREAT[Piece.KNIGHT]
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate bishops
     */
    private fun evalBishop(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.BISHOP]
        if (tmpPieces == Bitboard.EMPTY) {
            return 0
        }

        // Outpost not attacked squares
        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val outpostProtectedBitboard = board.evalInfo.protectedOutpost[ourColor]
        val outpostUnprotectedBitboard = board.evalInfo.unprotectedOutpost[ourColor]

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.BISHOP] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

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

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += Utils.specific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.BISHOP]
            }

            val safeChecks = possibleSafeCheck and attackBitboard

            if (safeChecks != Bitboard.EMPTY) {
                result += TunableConstants.SAFE_CHECK_THREAT[Piece.BISHOP]
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate rooks
     */
    private fun evalRook(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.ROOK]
        if (tmpPieces == Bitboard.EMPTY) {
            return 0
        }

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]

        var result = 0

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.ROOK] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.ROOK][Utils.specific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += Utils.specific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.ROOK]
            }

            val safeChecks = possibleSafeCheck and attackBitboard

            if (safeChecks != Bitboard.EMPTY) {
                result += TunableConstants.SAFE_CHECK_THREAT[Piece.ROOK]
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate queens
     */
    private fun evalQueen(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        var tmpPieces = board.pieceBitboard[ourColor][Piece.QUEEN]
        if (tmpPieces == Bitboard.EMPTY) {
            return 0
        }

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]

        var result = 0

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.QUEEN] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.QUEEN][Utils.specific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += Utils.specific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.QUEEN]
            }

            val safeChecks = possibleSafeCheck and attackBitboard

            if (safeChecks != Bitboard.EMPTY) {
                result += TunableConstants.SAFE_CHECK_THREAT[Piece.QUEEN]
            }

            tmpPieces = tmpPieces and tmpPieces - 1
        }
        return result
    }

    /**
     * Evaluate king
     */
    private fun evalKing(board: Board, attackInfo: AttackInfo, ourColor: Int): Int {
        val kingSquare = board.basicEvalInfo.kingSquare[ourColor]

        var result = 0

        var tmpPieces = board.pieceBitboard[ourColor][Piece.PAWN] and
            EvalConstants.PAWN_SHIELD_MASK[kingSquare]

        // Calculate bonus for pawns near the king file
        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)
            val file = File.getFile(square)
            val fileBorderDistance = min(file, File.flipFile(file))
            val fileDistance = Square.FILE_DISTANCE[kingSquare][square]
            val rankDistance = Square.RANK_DISTANCE[kingSquare][square]

            result += TunableConstants.PAWN_SHIELD[fileDistance][fileBorderDistance][rankDistance]

            tmpPieces = tmpPieces and tmpPieces - 1
        }

        return result
    }
}
