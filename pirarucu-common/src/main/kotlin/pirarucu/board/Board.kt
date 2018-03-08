package pirarucu.board

import pirarucu.eval.BasicEvalInfo
import pirarucu.game.GameConstants
import pirarucu.hash.Zobrist
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.move.MoveType
import pirarucu.util.Utils

class Board {

    // This change on 960 chess, but we do not support that at the moment
    val initialRookSquare = arrayOf(Square.H1, Square.A1, Square.H8, Square.A8)
    private val initialKingSquare = arrayOf(Square.E1, Square.E8)

    private var castlingRightsSquare = IntArray(Square.SIZE)

    var gameBitboard: Long
    var emptyBitboard: Long

    val pieceTypeBoard = IntArray(Square.SIZE)
    val pieceBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }
    val colorBitboard = LongArray(Color.SIZE)

    val pieceCount = IntArray(Piece.SIZE)

    var colorToMove = Color.WHITE
    var nextColorToMove = Color.BLACK

    var moveNumber: Int

    var basicEvalInfo: BasicEvalInfo

    var rule50: Int
    var castlingRights: Int
    var epSquare: Int
    var pawnZobristKey: Long
    var zobristKey: Long

    // History
    var historyZobristKey = LongArray(GameConstants.GAME_MAX_LENGTH)
    var historyPawnZobristKey = LongArray(GameConstants.GAME_MAX_LENGTH)
    var historyEpSquare = IntArray(GameConstants.GAME_MAX_LENGTH)
    var historyCastlingRights = IntArray(GameConstants.GAME_MAX_LENGTH)
    var historyRule50 = IntArray(GameConstants.GAME_MAX_LENGTH)

    fun colorAt(square: Int): Int {
        val bitboard = Bitboard.getBitboard(square)
        return when {
            colorBitboard[Color.WHITE] and bitboard != 0L -> Color.WHITE
            colorBitboard[Color.BLACK] and bitboard != 0L -> Color.BLACK
            else -> Color.INVALID
        }
    }

    init {
        rule50 = 0
        castlingRights = CastlingRights.ANY_CASTLING
        epSquare = Square.NONE
        pawnZobristKey = 0L
        zobristKey = 0L
        basicEvalInfo = BasicEvalInfo()
        gameBitboard = 0
        emptyBitboard = 0
        moveNumber = 0
        Utils.specific.arrayFill(pieceTypeBoard, Piece.NONE)
        castlingRightsSquare[initialKingSquare[0]] = CastlingRights.WHITE_CASTLING_RIGHTS
        castlingRightsSquare[initialKingSquare[1]] = CastlingRights.BLACK_CASTLING_RIGHTS
        castlingRightsSquare[initialRookSquare[0]] = CastlingRights.WHITE_OO
        castlingRightsSquare[initialRookSquare[1]] = CastlingRights.WHITE_OOO
        castlingRightsSquare[initialRookSquare[2]] = CastlingRights.BLACK_OO
        castlingRightsSquare[initialRookSquare[3]] = CastlingRights.BLACK_OOO
    }

    private fun pushToHistory() {
        historyRule50[moveNumber] = rule50
        historyCastlingRights[moveNumber] = castlingRights
        historyEpSquare[moveNumber] = epSquare
        historyPawnZobristKey[moveNumber] = pawnZobristKey
        historyZobristKey[moveNumber] = zobristKey
        moveNumber++
    }

    private fun popFromHistory() {
        moveNumber--
        rule50 = historyRule50[moveNumber]
        castlingRights = historyCastlingRights[moveNumber]
        epSquare = historyEpSquare[moveNumber]
        pawnZobristKey = historyPawnZobristKey[moveNumber]
        zobristKey = historyZobristKey[moveNumber]
    }

    fun possibleMove(move: Int): Boolean {
        return Move.getAttackedPieceType(move) != Piece.KING
    }

    fun doMove(move: Int) {
        pushToHistory()

        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        val movedPieceType = Move.getMovedPieceType(move)
        val attackedPieceType = Move.getAttackedPieceType(move)
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
            if (attackedPieceType != Piece.NONE) {
                var capturedSquare = toSquare
                if (attackedPieceType == Piece.PAWN) {
                    if (MoveType.TYPE_PASSANT == moveType) {
                        capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                    }
                    pawnZobristKey = pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[theirColor][Piece.PAWN][capturedSquare]
                }
                removePiece(theirColor, attackedPieceType, capturedSquare)

                zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[theirColor][attackedPieceType][capturedSquare]
                rule50 = 0
            }

            movePiece(ourColor, movedPieceType, fromSquare, toSquare)
        }

        clearEpSquare()

        when (movedPieceType) {
            Piece.PAWN -> {
                pawnZobristKey = pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][fromSquare]
                val promotedPiece = MoveType.getPromotedPiece(moveType)
                if (promotedPiece != Piece.NONE) {
                    removePiece(ourColor, Piece.PAWN, toSquare)
                    zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][toSquare]
                    putPiece(ourColor, promotedPiece, toSquare)
                    zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][promotedPiece][toSquare]
                } else {
                    val betweenBitboard = BitboardMove.BETWEEN_BITBOARD[fromSquare][toSquare]
                    if (betweenBitboard != 0L) {
                        epSquare = Square.getSquare(betweenBitboard)
                        zobristKey = zobristKey xor Zobrist.PASSANT_FILE[File.getFile(epSquare)]
                    }
                    pawnZobristKey = pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][toSquare]
                }
            }
        }

        updateCastlingRights(fromSquare, toSquare)

        nextColorToMove = colorToMove
        colorToMove = Color.invertColor(colorToMove)

        gameBitboard = colorBitboard[Color.WHITE] or colorBitboard[Color.BLACK]
        emptyBitboard = gameBitboard.inv()
        updateBasicInfo()
    }

    fun debugString(): String {
        val buffer = StringBuilder()
        for (color in Color.WHITE until Color.SIZE) {
            buffer.append(Color.toString(color))
            buffer.append("\n")
            buffer.append(Bitboard.toString(colorBitboard[color]))
            buffer.append("\n")
            for (piece in Piece.PAWN until Piece.SIZE) {
                buffer.append(Piece.toString(piece))
                buffer.append("\n")
                buffer.append(Bitboard.toString(pieceBitboard[color][piece]))
                buffer.append("\n")
            }
        }
        return buffer.toString()
    }

    fun updateBasicInfo() {
        gameBitboard = colorBitboard[Color.WHITE] or colorBitboard[Color.BLACK]
        emptyBitboard = gameBitboard.inv()
        basicEvalInfo.update(this)
    }

    fun undoMove(move: Int) {
        nextColorToMove = colorToMove
        colorToMove = Color.invertColor(colorToMove)

        val ourColor = colorToMove
        val theirColor = nextColorToMove

        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        val movedPieceType = Move.getMovedPieceType(move)
        val attackedPieceType = Move.getAttackedPieceType(move)
        val moveType = Move.getMoveType(move)

        when (movedPieceType) {
            Piece.PAWN -> {
                val promotedPiece = MoveType.getPromotedPiece(moveType)
                if (promotedPiece != Piece.NONE) {
                    removePiece(ourColor, promotedPiece, toSquare)
                    putPiece(ourColor, Piece.PAWN, toSquare)
                }
            }
        }

        if (MoveType.isCastling(moveType)) {
            undoCastle(ourColor, fromSquare, toSquare)
        } else {
            movePiece(ourColor, movedPieceType, toSquare, fromSquare)

            if (attackedPieceType != Piece.NONE) {
                var capturedSquare = toSquare
                if (attackedPieceType == Piece.PAWN) {
                    if (MoveType.TYPE_PASSANT == moveType) {
                        capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                    }
                }
                putPiece(theirColor, attackedPieceType, capturedSquare)
            }
        }

        gameBitboard = colorBitboard[Color.WHITE] or colorBitboard[Color.BLACK]
        emptyBitboard = gameBitboard.inv()
        basicEvalInfo.update(this)
        popFromHistory()
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
            zobristKey = pawnZobristKey xor Zobrist.PASSANT_FILE[File.getFile(epSquare)]
            epSquare = Square.NONE
        }
    }

    private fun doCastle(ourColor: Int, fromSquare: Int, toSquare: Int) {
        val kingSide = if (toSquare > fromSquare) CastlingRights.KING_SIDE else CastlingRights.QUEEN_SIDE
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

        zobristKey = zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookFrom] xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookTo]
    }

    /**
     * There is no need to update zobrist key when undoing the castle (it is cached in history data)
     */
    private fun undoCastle(ourColor: Int, fromSquare: Int, toSquare: Int) {
        val kingSide = if (toSquare > fromSquare) CastlingRights.KING_SIDE else CastlingRights.QUEEN_SIDE
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
        colorBitboard[color] = colorBitboard[color] xor bitboard
        pieceCount[piece]--
    }

    fun putPiece(color: Int, piece: Int, square: Int) {
        val bitboard = Bitboard.getBitboard(square)
        pieceTypeBoard[square] = piece
        pieceBitboard[color][piece] = pieceBitboard[color][piece] or bitboard
        colorBitboard[color] = colorBitboard[color] or bitboard
        pieceCount[piece]++
    }

    private fun movePiece(color: Int, piece: Int, fromSquare: Int, toSquare: Int) {
        val moveBitboard = Bitboard.getBitboard(fromSquare) xor Bitboard.getBitboard(toSquare)
        pieceBitboard[color][piece] = pieceBitboard[color][piece] xor moveBitboard
        colorBitboard[color] = colorBitboard[color] xor moveBitboard
        pieceTypeBoard[fromSquare] = Piece.NONE
        pieceTypeBoard[toSquare] = piece
    }
}