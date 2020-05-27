package pirarucu.board

import pirarucu.eval.AttackInfo
import pirarucu.eval.BasicEvalInfo
import pirarucu.eval.EvalInfo
import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.move.MoveGenerator
import pirarucu.move.MoveType
import pirarucu.tuning.TunableConstants
import pirarucu.util.PlatformSpecific

class Board {

    // This change on 960 chess, but we do not support that at the moment
    val initialRookSquare = arrayOf(Square.H1, Square.A1, Square.H8, Square.A8)
    private val initialKingSquare = arrayOf(Square.E1, Square.E8)

    private var castlingRightsSquare = IntArray(Square.SIZE)

    var gameBitboard = 0L
    var emptyBitboard = 0L

    var moveNumber = 0
    private var basicInfoIndex = 0

    var colorToMove = Color.WHITE
    var nextColorToMove = Color.BLACK

    val pieceTypeBoard = IntArray(Square.SIZE)
    val pieceBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }

    var rule50 = 0
    var castlingRights = CastlingRights.ANY_CASTLING
    var epSquare = Square.NONE
    var zobristKey = 0L
    var pawnZobristKey = 0L

    val kingSquare = IntArray(Color.SIZE)
    val psqScore = IntArray(Color.SIZE)
    val materialScore = IntArray(Color.SIZE)
    var phase = 0

    var capturedPiece = Piece.NONE

    // History
    val historyZobristKey = LongArray(GameConstants.GAME_MAX_LENGTH)
    private val historyRule50 = IntArray(GameConstants.GAME_MAX_LENGTH)
    private val historyCastlingRights = IntArray(GameConstants.GAME_MAX_LENGTH)
    private val historyEpSquare = IntArray(GameConstants.GAME_MAX_LENGTH)
    private val historyPawnZobristKey = LongArray(GameConstants.GAME_MAX_LENGTH)
    private val historyCapturedPiece = IntArray(GameConstants.GAME_MAX_LENGTH)
    private val historyBasicEvalInfo = Array(GameConstants.GAME_MAX_LENGTH) { BasicEvalInfo() }

    var basicEvalInfo = historyBasicEvalInfo[basicInfoIndex]
    val evalInfo = EvalInfo()

    /**
     * Setup some initial information.
     */
    init {
        PlatformSpecific.arrayFill(pieceTypeBoard, Piece.NONE)

        castlingRightsSquare[initialKingSquare[Color.WHITE]] = CastlingRights.WHITE_CASTLING_RIGHTS
        castlingRightsSquare[initialKingSquare[Color.BLACK]] = CastlingRights.BLACK_CASTLING_RIGHTS

        castlingRightsSquare[initialRookSquare[CastlingRights.WHITE_KING_CASTLING_INDEX]] =
            CastlingRights.WHITE_OO
        castlingRightsSquare[initialRookSquare[CastlingRights.WHITE_QUEEN_CASTLING_INDEX]] =
            CastlingRights.WHITE_OOO
        castlingRightsSquare[initialRookSquare[CastlingRights.BLACK_KING_CASTLING_INDEX]] =
            CastlingRights.BLACK_OO
        castlingRightsSquare[initialRookSquare[CastlingRights.BLACK_QUEEN_CASTLING_INDEX]] =
            CastlingRights.BLACK_OOO
    }

    /**
     * Reset the board information.
     */
    fun reset() {
        rule50 = 0
        castlingRights = CastlingRights.ANY_CASTLING
        epSquare = Square.NONE
        zobristKey = 0L
        pawnZobristKey = 0L

        PlatformSpecific.arrayFill(psqScore, 0)
        PlatformSpecific.arrayFill(materialScore, 0)
        phase = 0

        gameBitboard = 0L
        emptyBitboard = 0L

        moveNumber = 0

        colorToMove = Color.WHITE
        nextColorToMove = Color.BLACK
        capturedPiece = Piece.NONE

        PlatformSpecific.arrayFill(pieceTypeBoard, Piece.NONE)

        PlatformSpecific.arrayFill(pieceBitboard[Color.WHITE], Bitboard.EMPTY)
        PlatformSpecific.arrayFill(pieceBitboard[Color.BLACK], Bitboard.EMPTY)
    }

    private fun pushToHistory() {
        historyRule50[moveNumber] = rule50
        historyCastlingRights[moveNumber] = castlingRights
        historyEpSquare[moveNumber] = epSquare
        historyZobristKey[moveNumber] = zobristKey
        historyPawnZobristKey[moveNumber] = pawnZobristKey
        historyCapturedPiece[moveNumber] = capturedPiece
        moveNumber++
    }

    private fun popFromHistory() {
        moveNumber--
        rule50 = historyRule50[moveNumber]
        castlingRights = historyCastlingRights[moveNumber]
        epSquare = historyEpSquare[moveNumber]
        zobristKey = historyZobristKey[moveNumber]
        pawnZobristKey = historyPawnZobristKey[moveNumber]
        capturedPiece = historyCapturedPiece[moveNumber]
    }

    private fun setInitialKingSquare() {
        kingSquare[Color.WHITE] = Square.getSquare(pieceBitboard[Color.WHITE][Piece.KING])
        kingSquare[Color.BLACK] = Square.getSquare(pieceBitboard[Color.BLACK][Piece.KING])
    }

    private fun updateBitboardInfo() {
        gameBitboard = pieceBitboard[Color.WHITE][Piece.NONE] or pieceBitboard[Color.BLACK][Piece.NONE]
        emptyBitboard = gameBitboard.inv()
    }

    /**
     * Update board extra information.
     */
    fun updateBoardExtra() {
        setInitialKingSquare()
        updateBitboardInfo()
        basicInfoIndex = 0
        basicEvalInfo = historyBasicEvalInfo[basicInfoIndex]
        basicEvalInfo.update(this)
    }

    /**
     * Gets all bitboards as string, used to debug.
     */
    fun debugString(): String {
        val buffer = StringBuilder()
        for (color in Color.WHITE until Color.SIZE) {
            buffer.append("Color " + Color.toString(color))
            buffer.append("\n")
            buffer.append(Bitboard.toString(pieceBitboard[color][Piece.NONE]))
            buffer.append("\n")
            for (piece in Piece.PAWN until Piece.SIZE) {
                buffer.append("Piece " + Piece.toString(piece))
                buffer.append("\n")
                buffer.append(Bitboard.toString(pieceBitboard[color][piece]))
                buffer.append("\n")
            }
        }
        return buffer.toString()
    }

    /**
     * Update evaluation information
     */
    inline fun updateEval(attackInfo: AttackInfo) {
        evalInfo.update(this, attackInfo)
    }

    /**
     * Executes null movement.
     */
    fun doNullMove() {
        pushToHistory()

        zobristKey = zobristKey xor Zobrist.SIDE
        if (epSquare != Square.NONE) {
            zobristKey = zobristKey xor Zobrist.PASSANT_SQUARE[epSquare]
            epSquare = Square.NONE
        }
        capturedPiece = 0

        nextColorToMove = colorToMove
        colorToMove = Color.invertColor(colorToMove)
    }

    /**
     * Revert the null movement
     */
    fun undoNullMove() {
        popFromHistory()

        nextColorToMove = colorToMove
        colorToMove = Color.invertColor(colorToMove)
    }

    /**
     * Check if pseudo move is legal.
     */
    fun isLegalMove(move: Int): Boolean {
        when (Move.getMoveType(move)) {
            MoveType.TYPE_CASTLING -> {
                // check if path is under attack
                val toSquare = Move.getToSquare(move)
                val toBitboard = Bitboard.getBitboard(toSquare)
                val path = BitboardMove.BETWEEN_BITBOARD[Move.getFromSquare(move)][toSquare] or toBitboard
                return !MoveGenerator.pathUnderAttack(path, colorToMove, pieceBitboard[nextColorToMove], gameBitboard)
            }
            MoveType.TYPE_PASSANT -> {
                val fromBitboard = Bitboard.getBitboard(Move.getFromSquare(move))

                val tmpBitboard = gameBitboard xor fromBitboard xor
                    Bitboard.getBitboard(epSquare) xor BitboardMove.PAWN_MOVES[nextColorToMove][epSquare]
                return MoveGenerator.squareAttackedBitboard(
                    kingSquare[colorToMove], colorToMove, pieceBitboard[nextColorToMove], tmpBitboard
                ) == Bitboard.EMPTY
            }
            else -> {
                val fromSquare = Move.getFromSquare(move)
                val fromBitboard = Bitboard.getBitboard(fromSquare)
                val toSquare = Move.getToSquare(move)
                if (pieceTypeBoard[fromSquare] == Piece.KING) {
                    val tmpBitboard = gameBitboard xor fromBitboard

                    return MoveGenerator.squareAttackedBitboard(
                        toSquare, colorToMove, pieceBitboard[nextColorToMove], tmpBitboard
                    ) == Bitboard.EMPTY
                }
                val toBitboard = Bitboard.getBitboard(toSquare)
                return fromBitboard and basicEvalInfo.pinnedBitboard == Bitboard.EMPTY ||
                        BitboardMove.PINNED_MOVE_MASK[kingSquare[colorToMove]][fromSquare] and toBitboard != Bitboard.EMPTY
            }
        }
    }

    /**
     * Execute a movement, need to check if move is legal before making it.
     */
    fun doMove(move: Int) {
        pushToHistory()
        rule50++

        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        val movedPieceType = pieceTypeBoard[fromSquare]
        capturedPiece = pieceTypeBoard[toSquare]
        val moveType = Move.getMoveType(move)

        val ourColor = colorToMove
        val theirColor = nextColorToMove

        zobristKey = zobristKey xor Zobrist.SIDE xor
            Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][fromSquare] xor
            Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][toSquare]

        // Castling needs to move two pieces
        if (MoveType.isCastling(moveType)) {
            doCastle(ourColor, fromSquare, toSquare)
        } else {
            var capturedSquare = toSquare
            if (MoveType.TYPE_PASSANT == moveType) {
                capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                capturedPiece = Piece.PAWN
            }
            if (capturedPiece != Piece.NONE) {
                if (capturedPiece == Piece.PAWN) {
                    pawnZobristKey = pawnZobristKey xor
                        Zobrist.PIECE_SQUARE_TABLE[theirColor][Piece.PAWN][capturedSquare]
                }
                removePiece(theirColor, capturedPiece, capturedSquare)
                pieceTypeBoard[capturedSquare] = Piece.NONE

                zobristKey = zobristKey xor
                    Zobrist.PIECE_SQUARE_TABLE[theirColor][capturedPiece][capturedSquare]
                rule50 = 0
            }

            movePiece(ourColor, movedPieceType, fromSquare, toSquare)
        }

        clearEpSquare()

        when (movedPieceType) {
            Piece.PAWN -> {
                pawnZobristKey = pawnZobristKey xor
                    Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][fromSquare]
                val promotedPiece = MoveType.getPromotedPiece(moveType)
                if (promotedPiece != Piece.NONE) {
                    removePiece(ourColor, Piece.PAWN, toSquare)
                    zobristKey = zobristKey xor
                        Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][toSquare]
                    putPiece(ourColor, promotedPiece, toSquare)
                    zobristKey = zobristKey xor
                        Zobrist.PIECE_SQUARE_TABLE[ourColor][promotedPiece][toSquare]
                } else {
                    if (fromSquare xor toSquare == 16 &&
                        BitboardMove.NEIGHBOURS[toSquare] and pieceBitboard[theirColor][Piece.PAWN] != 0L
                    ) {
                        epSquare = toSquare xor 8
                        zobristKey = zobristKey xor Zobrist.PASSANT_SQUARE[epSquare]
                    }
                    pawnZobristKey = pawnZobristKey xor
                        Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][toSquare]
                }
                rule50 = 0
            }
            Piece.KING -> {
                kingSquare[ourColor] = toSquare
            }
        }

        updateCastlingRights(fromSquare, toSquare)

        nextColorToMove = ourColor
        colorToMove = theirColor

        updateBitboardInfo()
        basicEvalInfo = nextBasicEvalInfo()
        basicEvalInfo.update(this)
    }

    /**
     * Undo previously made move.
     */
    fun undoMove(move: Int) {
        nextColorToMove = colorToMove
        colorToMove = Color.invertColor(colorToMove)

        val ourColor = colorToMove
        val theirColor = nextColorToMove

        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        var movedPieceType = pieceTypeBoard[toSquare]
        val moveType = Move.getMoveType(move)

        if (MoveType.isPromotion(moveType)) {
            removePiece(ourColor, movedPieceType, toSquare)
            putPiece(ourColor, Piece.PAWN, toSquare)

            movedPieceType = Piece.PAWN
        }

        if (MoveType.isCastling(moveType)) {
            undoCastle(ourColor, fromSquare, toSquare)
        } else {
            movePiece(ourColor, movedPieceType, toSquare, fromSquare)

            if (capturedPiece != Piece.NONE) {
                var capturedSquare = toSquare
                if (capturedPiece == Piece.PAWN) {
                    if (MoveType.TYPE_PASSANT == moveType) {
                        capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                    }
                }
                putPiece(theirColor, capturedPiece, capturedSquare)
            }
        }
        if (movedPieceType == Piece.KING) {
            kingSquare[ourColor] = fromSquare
        }

        updateBitboardInfo()
        popFromHistory()
        basicEvalInfo = previousBasicEvalInfo()
    }

    private fun updateCastlingRights(fromSquare: Int, toSquare: Int) {
        val castlingChange: Int = castlingRightsSquare[fromSquare] or castlingRightsSquare[toSquare]
        if (castlingChange and castlingRights != 0) {
            zobristKey = zobristKey xor Zobrist.CASTLING_RIGHT[castlingRights]
            castlingRights = castlingRights and castlingChange.inv()
            zobristKey = zobristKey xor Zobrist.CASTLING_RIGHT[castlingRights]
        }
    }

    private fun clearEpSquare() {
        if (epSquare != Square.NONE) {
            zobristKey = zobristKey xor Zobrist.PASSANT_SQUARE[epSquare]
            epSquare = Square.NONE
        }
    }

    private fun doCastle(ourColor: Int, fromSquare: Int, toSquare: Int) {
        val kingSide = if (toSquare > fromSquare) CastlingRights.KING_SIDE else
            CastlingRights.QUEEN_SIDE
        val castlingRightIndex = CastlingRights.getCastlingRightIndex(ourColor, kingSide)
        val rookFrom = initialRookSquare[castlingRightIndex]
        val rookTo = CastlingRights.ROOK_FINAL_SQUARE[castlingRightIndex]

        // Remove both pieces first since squares could overlap in Chess960
        removePiece(ourColor, Piece.KING, fromSquare)
        removePiece(ourColor, Piece.ROOK, rookFrom)

        pieceTypeBoard[rookFrom] = Piece.NONE
        pieceTypeBoard[fromSquare] = Piece.NONE

        putPiece(ourColor, Piece.KING, toSquare)
        putPiece(ourColor, Piece.ROOK, rookTo)

        zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookFrom] xor
            Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookTo]
    }

    /**
     * There is no need to update zobrist key when undoing the castle (it is cached in history data)
     */
    private fun undoCastle(ourColor: Int, fromSquare: Int, toSquare: Int) {
        val kingSide = if (toSquare > fromSquare) CastlingRights.KING_SIDE else
            CastlingRights.QUEEN_SIDE
        val castlingRightIndex = CastlingRights.getCastlingRightIndex(ourColor, kingSide)
        val rookFrom = initialRookSquare[castlingRightIndex]
        val rookTo = CastlingRights.ROOK_FINAL_SQUARE[castlingRightIndex]

        removePiece(ourColor, Piece.KING, toSquare)
        removePiece(ourColor, Piece.ROOK, rookTo)

        pieceTypeBoard[rookTo] = Piece.NONE
        pieceTypeBoard[toSquare] = Piece.NONE

        putPiece(ourColor, Piece.KING, fromSquare)
        putPiece(ourColor, Piece.ROOK, rookFrom)
    }

    private fun removePiece(color: Int, piece: Int, square: Int) {
        val bitboard = Bitboard.getBitboard(square)
        pieceBitboard[color][piece] = pieceBitboard[color][piece] xor bitboard
        pieceBitboard[color][Piece.NONE] = pieceBitboard[color][Piece.NONE] xor bitboard

        val relativeSquare = Square.getRelativeSquare(color, square)
        psqScore[color] -= TunableConstants.PSQT[piece][relativeSquare]
        materialScore[color] -= TunableConstants.MATERIAL_SCORE[piece]
        phase -= TunableConstants.PHASE_PIECE_VALUE[piece]
    }

    /**
     * Insert a piece on the board.
     */
    fun putPiece(color: Int, piece: Int, square: Int) {
        val bitboard = Bitboard.getBitboard(square)
        pieceTypeBoard[square] = piece
        pieceBitboard[color][piece] = pieceBitboard[color][piece] or bitboard
        pieceBitboard[color][Piece.NONE] = pieceBitboard[color][Piece.NONE] or bitboard

        val relativeSquare = Square.getRelativeSquare(color, square)
        psqScore[color] += TunableConstants.PSQT[piece][relativeSquare]
        materialScore[color] += TunableConstants.MATERIAL_SCORE[piece]
        phase += TunableConstants.PHASE_PIECE_VALUE[piece]
    }

    private fun movePiece(color: Int, piece: Int, fromSquare: Int, toSquare: Int) {
        val moveBitboard = Bitboard.getBitboard(fromSquare) xor Bitboard.getBitboard(toSquare)
        pieceBitboard[color][piece] = pieceBitboard[color][piece] xor moveBitboard
        pieceBitboard[color][Piece.NONE] = pieceBitboard[color][Piece.NONE] xor moveBitboard

        pieceTypeBoard[fromSquare] = Piece.NONE
        pieceTypeBoard[toSquare] = piece

        val relativeFromSquare = Square.getRelativeSquare(color, fromSquare)
        val relativeToSquare = Square.getRelativeSquare(color, toSquare)
        psqScore[color] -= TunableConstants.PSQT[piece][relativeFromSquare]
        psqScore[color] += TunableConstants.PSQT[piece][relativeToSquare]
    }

    /**
     * Check if a side has pieces that are not pawns or king.
     */
    fun hasNonPawnMaterial(color: Int): Boolean {
        return pieceBitboard[color][Piece.KING] or pieceBitboard[color][Piece.PAWN] != pieceBitboard[color][Piece.NONE]
    }

    private fun nextBasicEvalInfo(): BasicEvalInfo {
        basicInfoIndex++
        return historyBasicEvalInfo[basicInfoIndex]
    }

    private fun previousBasicEvalInfo(): BasicEvalInfo {
        basicInfoIndex--
        return historyBasicEvalInfo[basicInfoIndex]
    }

    /**
     * Copy the object state from another board.
     */
    fun copy(board: Board) {
        gameBitboard = board.gameBitboard
        emptyBitboard = board.emptyBitboard

        moveNumber = board.moveNumber
        basicInfoIndex = board.basicInfoIndex

        colorToMove = board.colorToMove
        nextColorToMove = board.nextColorToMove

        PlatformSpecific.arrayCopy(board.pieceTypeBoard, 0, pieceTypeBoard, 0, pieceTypeBoard.size)
        PlatformSpecific.arrayCopy(board.pieceBitboard, pieceBitboard)

        rule50 = board.rule50
        castlingRights = board.castlingRights
        epSquare = board.epSquare
        zobristKey = board.zobristKey
        pawnZobristKey = board.pawnZobristKey

        PlatformSpecific.arrayCopy(board.kingSquare, 0, kingSquare, 0, kingSquare.size)
        PlatformSpecific.arrayCopy(board.psqScore, 0, psqScore, 0, psqScore.size)
        PlatformSpecific.arrayCopy(board.materialScore, 0, materialScore, 0, materialScore.size)

        phase = board.phase

        capturedPiece = board.capturedPiece

        // History
        PlatformSpecific.arrayCopy(board.historyZobristKey, 0, historyZobristKey, 0, historyZobristKey.size)
        PlatformSpecific.arrayCopy(board.historyRule50, 0, historyRule50, 0, historyZobristKey.size)
        PlatformSpecific.arrayCopy(board.historyCastlingRights, 0, historyCastlingRights, 0, historyZobristKey.size)
        PlatformSpecific.arrayCopy(board.historyEpSquare, 0, historyEpSquare, 0, historyZobristKey.size)
        PlatformSpecific.arrayCopy(board.historyPawnZobristKey, 0, historyPawnZobristKey, 0, historyZobristKey.size)
        PlatformSpecific.arrayCopy(board.historyCapturedPiece, 0, historyCapturedPiece, 0, historyZobristKey.size)
        for (index in historyBasicEvalInfo.indices) {
            historyBasicEvalInfo[index].copy(board.historyBasicEvalInfo[index])
        }

        basicEvalInfo = historyBasicEvalInfo[basicInfoIndex]
    }

    /**
     * Get the current piece color at a square.
     */
    fun pieceColorAt(square: Int): Int {
        val bitboard = Bitboard.getBitboard(square)
        return when {
            pieceBitboard[Color.WHITE][Piece.NONE] and bitboard != 0L -> Color.WHITE
            pieceBitboard[Color.BLACK][Piece.NONE] and bitboard != 0L -> Color.BLACK
            else -> Color.INVALID
        }
    }

}
