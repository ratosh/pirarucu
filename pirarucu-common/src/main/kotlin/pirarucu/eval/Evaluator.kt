package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.cache.PawnEvaluationCache
import pirarucu.tuning.TunableConstants
import pirarucu.util.PlatformSpecific
import pirarucu.util.SplitValue
import kotlin.math.min

object Evaluator {

    fun evaluate(board: Board, attackInfo: AttackInfo, pawnEvaluationCache: PawnEvaluationCache): Int {
        board.updateEval(attackInfo)

        val materialScore = board.materialScore[Color.WHITE] - board.materialScore[Color.BLACK]
        if (materialScore > EvalConstants.SCORE_DRAWISH_MATERIAL) {
            if (!DrawEvaluator.hasSufficientMaterial(board, Color.WHITE)) {
                return EvalConstants.SCORE_DRAW
            }
        } else if (materialScore < -EvalConstants.SCORE_DRAWISH_MATERIAL) {
            if (!DrawEvaluator.hasSufficientMaterial(board, Color.BLACK)) {
                return EvalConstants.SCORE_DRAW
            }
        }

        var score = TunableConstants.TEMPO[board.colorToMove] +
            board.psqScore[Color.WHITE] - board.psqScore[Color.BLACK] +
            materialScore

        score += PawnEvaluator.evaluate(board, attackInfo, pawnEvaluationCache)

        score += evalKnight(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalKnight(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalBishop(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalBishop(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalRook(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalRook(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalQueen(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalQueen(board, attackInfo, Color.BLACK, Color.WHITE)

        score += evalKing(board, attackInfo, Color.WHITE, Color.BLACK) -
            evalKing(board, attackInfo, Color.BLACK, Color.WHITE)

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

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val pawnSupportBitboard = attackInfo.attacksBitboard[ourColor][Piece.PAWN]
        val pawnThreatBitboard = attackInfo.attacksBitboard[theirColor][Piece.PAWN]

        var result = 0

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.KNIGHT] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)
            val bitboard = Bitboard.getBitboard(square)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            // Is supported by a pawn
            if (bitboard and pawnSupportBitboard != Bitboard.EMPTY) {
                result += TunableConstants.PAWN_SUPPORT[Piece.KNIGHT]
            }
            // Is attacked by a pawn
            if (bitboard and pawnThreatBitboard != Bitboard.EMPTY) {
                result -= TunableConstants.PAWN_THREAT[Piece.KNIGHT]
            }

            // Attacking undefended pawns
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.PAWN] and
                attackInfo.attacksBitboard[theirColor][Piece.NONE].inv() != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_KNIGHT[Piece.PAWN]
            }

            // Attacking bishops
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.BISHOP] != Bitboard.EMPTY) {
                result += TunableConstants.THREATEN_BY_KNIGHT[Piece.BISHOP]
            }

            // Attacking rook
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.ROOK] != Bitboard.EMPTY) {
                result += TunableConstants.THREATEN_BY_KNIGHT[Piece.ROOK]
            }

            // Attacking queen
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.QUEEN] != Bitboard.EMPTY) {
                result += TunableConstants.THREATEN_BY_KNIGHT[Piece.QUEEN]
            }

            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.KNIGHT][PlatformSpecific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += PlatformSpecific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.KNIGHT]
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

        val mobilityBitboard = board.evalInfo.mobilityArea[ourColor]
        val pawnSupportBitboard = attackInfo.attacksBitboard[ourColor][Piece.PAWN]
        val pawnThreatBitboard = attackInfo.attacksBitboard[theirColor][Piece.PAWN]

        val possibleSafeCheck = board.basicEvalInfo.dangerBitboard[theirColor][Piece.BISHOP] and
            board.colorBitboard[ourColor].inv() and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()

        var result = 0

        if (board.pieceCountColorType[ourColor][Piece.BISHOP] > 1) {
            result += TunableConstants.OTHER_BONUS[TunableConstants.OTHER_BONUS_BISHOP_PAIR]
        }

        while (tmpPieces != Bitboard.EMPTY) {
            val square = Square.getSquare(tmpPieces)
            val bitboard = Bitboard.getBitboard(square)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            // Is supported by a pawn
            if (bitboard and pawnSupportBitboard != Bitboard.EMPTY) {
                result += TunableConstants.PAWN_SUPPORT[Piece.BISHOP]
            }
            // Is attacked by a pawn
            if (bitboard and pawnThreatBitboard != Bitboard.EMPTY) {
                result -= TunableConstants.PAWN_THREAT[Piece.BISHOP]
            }
            // Attacking undefended pawns
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.PAWN] and
                attackInfo.attacksBitboard[theirColor][Piece.NONE].inv() != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_BISHOP[Piece.PAWN]
            }

            // Attacking knights
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.KNIGHT] != Bitboard.EMPTY) {
                result += TunableConstants.THREATEN_BY_BISHOP[Piece.KNIGHT]
            }

            // Attacking rook
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.ROOK] != Bitboard.EMPTY) {
                result += TunableConstants.THREATEN_BY_BISHOP[Piece.ROOK]
            }

            // Protected bishop attacking queen
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.QUEEN] != Bitboard.EMPTY &&
                bitboard and attackInfo.attacksBitboard[ourColor][Piece.NONE] != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_BISHOP[Piece.QUEEN]
            }

            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.BISHOP][PlatformSpecific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += PlatformSpecific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.BISHOP]
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
            val bitboard = Bitboard.getBitboard(square)

            val attackBitboard = attackInfo.pieceMovement[ourColor][square]
            // Attacking undefended pawns
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.PAWN] and
                attackInfo.attacksBitboard[theirColor][Piece.NONE].inv() != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_ROOK[Piece.PAWN]
            }

            // Attacking undefended knights
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.KNIGHT] and
                attackInfo.attacksBitboard[theirColor][Piece.NONE].inv() != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_ROOK[Piece.KNIGHT]
            }

            // Attacking undefended bishops
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.BISHOP] and
                attackInfo.attacksBitboard[theirColor][Piece.NONE].inv() != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_ROOK[Piece.BISHOP]
            }

            // Protected rook attacking queen
            if (attackBitboard and board.pieceBitboard[theirColor][Piece.QUEEN] != Bitboard.EMPTY &&
                bitboard and attackInfo.attacksBitboard[ourColor][Piece.NONE] != Bitboard.EMPTY
            ) {
                result += TunableConstants.THREATEN_BY_ROOK[Piece.QUEEN]
            }

            val pieceMobilityBitboard = attackBitboard and mobilityBitboard
            result += TunableConstants.MOBILITY[Piece.ROOK][PlatformSpecific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += PlatformSpecific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.ROOK]
            }

            val safeChecks = possibleSafeCheck and attackBitboard

            if (safeChecks != Bitboard.EMPTY) {
                result += TunableConstants.SAFE_CHECK_THREAT[Piece.ROOK]
            }
            if (Rank.getRelativeRank(ourColor, Rank.getRank(square)) == Rank.RANK_7 &&
                Rank.getRelativeRank(ourColor, Rank.getRank(board.kingSquare[theirColor])) >=
                Rank.RANK_7
            ) {
                result += TunableConstants.OTHER_BONUS[TunableConstants.OTHER_BONUS_ROOK_ON_SEVENTH]
            }

            val fileBitboard = Bitboard.FILES[File.getFile(square)]
            if (fileBitboard and board.pieceBitboard[ourColor][Piece.PAWN] == Bitboard.EMPTY) {
                result += if (fileBitboard and board.pieceBitboard[theirColor][Piece.PAWN] == Bitboard.EMPTY) {
                    TunableConstants.OTHER_BONUS[TunableConstants.OTHER_BONUS_ROOK_OPEN_FILE]
                } else {
                    TunableConstants.OTHER_BONUS[TunableConstants.OTHER_BONUS_ROOK_HALF_OPEN_FILE]
                }
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
            result += TunableConstants.MOBILITY[Piece.QUEEN][PlatformSpecific.bitCount(pieceMobilityBitboard)]

            val kingAreaAttacks = attackBitboard and board.evalInfo.kingArea[theirColor]
            if (kingAreaAttacks != Bitboard.EMPTY) {
                result += PlatformSpecific.bitCount(kingAreaAttacks) * TunableConstants.KING_THREAT[Piece.QUEEN]
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
    private fun evalKing(board: Board, attackInfo: AttackInfo, ourColor: Int, theirColor: Int): Int {
        val kingSquare = board.kingSquare[ourColor]

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
        val attackBitboard = attackInfo.pieceMovement[ourColor][kingSquare]

        val kingMobility = attackBitboard and attackInfo.attacksBitboard[theirColor][Piece.NONE].inv()
        result += TunableConstants.MOBILITY[Piece.KING][PlatformSpecific.bitCount(kingMobility)]

        return result
    }
}
