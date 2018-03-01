package pirarucu.board

import pirarucu.eval.BasicEvalInfo
import pirarucu.hash.Zobrist
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.move.MoveType
import pirarucu.util.Utils

class Board {

    // This change on 960 chess, but we do not support that at the moment
    private val initialRookSquare = arrayOf(Square.H1, Square.A1, Square.H8, Square.A8)

    private var castlingRightsSquare = IntArray(Square.SIZE)

    var gameBitboard: Long
    var emptyBitboard: Long

    val pieceTypeBoard = IntArray(Square.SIZE)
    val pieceBitboard = Array(Color.SIZE) { LongArray(Piece.SIZE) }
    val colorBitboard = LongArray(Color.SIZE)

    val pieceCount = IntArray(Piece.SIZE)

    var colorToMove = Color.WHITE

    var moveNumber: Int

    var basicEvalInfo: BasicEvalInfo

    var currentState: BoardState

    fun colorAt(square: Int): Int {
        val bitboard = Bitboard.getBitboard(square)
        return when {
            colorBitboard[Color.WHITE] and bitboard != 0L -> Color.WHITE
            colorBitboard[Color.BLACK] and bitboard != 0L -> Color.BLACK
            else -> Color.INVALID
        }
    }

    init {
        currentState = BoardState(0, 0, Square.NONE, CastlingRights.ANY_CASTLING, 0, 0, null)
        basicEvalInfo = BasicEvalInfo()
        gameBitboard = 0
        emptyBitboard = 0
        moveNumber = 0
        Utils.specific.arrayFill(pieceTypeBoard, Piece.NONE)
    }

    private fun pushToHistory(move: Int) {
        currentState = currentState.copy(lastMove = move, previousState = currentState)
        moveNumber++

        currentState.rule50++
    }

    private fun popFromHistory() {
        currentState = currentState.previousState!!
        moveNumber--
    }

    fun doMove(move: Int) {
        pushToHistory(move)

        val fromSquare = Move.getFromSquare(move)
        val toSquare = Move.getToSquare(move)
        val movedPieceType = Move.getMovedPieceType(move)
        val attackedPieceType = Move.getAttackedPieceType(move)
        val moveType = Move.getMoveType(move)

        val ourColor = colorToMove
        val theirColor = Color.invertColor(ourColor)

        currentState.zobristKey = currentState.zobristKey xor Zobrist.SIDE xor
            Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][fromSquare] xor
            Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][toSquare]

        currentState.lastMove = move

        // Castling needs to move two pieces
        if (MoveType.isCastling(moveType)) {
            doCastle(ourColor, fromSquare, toSquare)
        } else {
            movePiece(ourColor, movedPieceType, fromSquare, toSquare)
        }

        if (attackedPieceType != Piece.NONE) {
            var capturedSquare = toSquare
            if (attackedPieceType == Piece.PAWN) {
                if (MoveType.TYPE_PASSANT == moveType) {
                    capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                }
                currentState.pawnZobristKey = currentState.pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[theirColor][Piece.PAWN][capturedSquare]
            }
            removePiece(theirColor, attackedPieceType, capturedSquare)

            currentState.zobristKey = currentState.zobristKey xor Zobrist.PIECE_SQUARE_TABLE[theirColor][attackedPieceType][capturedSquare]
            currentState.rule50 = 0
        }

        clearEpSquare()

        when (movedPieceType) {
            Piece.PAWN -> {
                currentState.pawnZobristKey = currentState.pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][fromSquare]
                val promotedPiece = MoveType.getPromotedPiece(moveType)
                if (promotedPiece != Piece.NONE) {
                    removePiece(ourColor, Piece.PAWN, toSquare)
                    currentState.zobristKey = currentState.zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][movedPieceType][toSquare]
                    putPiece(ourColor, promotedPiece, toSquare)
                    currentState.zobristKey = currentState.zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][promotedPiece][toSquare]
                } else {
                    val betweenBitboard = BitboardMove.BETWEEN_BITBOARD[fromSquare][toSquare]
                    if (betweenBitboard != 0L) {
                        currentState.epSquare = Square.getSquare(betweenBitboard)
                        currentState.zobristKey = currentState.zobristKey xor Zobrist.PASSANT_FILE[File.getFile(currentState.epSquare)]
                    }
                    currentState.pawnZobristKey = currentState.pawnZobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.PAWN][toSquare]
                }
            }
        }

        updateCastlingRights(fromSquare, toSquare)

        colorToMove = Color.invertColor(colorToMove)

        gameBitboard = colorBitboard[Color.WHITE] or colorBitboard[Color.BLACK]
        emptyBitboard = gameBitboard.inv()
        basicEvalInfo.update(this)
    }

    fun undoMove() {
        if (currentState.lastMove != Move.NONE) {

            colorToMove = Color.invertColor(colorToMove)

            val ourColor = colorToMove
            val theirColor = Color.invertColor(ourColor)

            val move = currentState.lastMove

            val fromSquare = Move.getFromSquare(move)
            val toSquare = Move.getToSquare(move)
            val movedPieceType = Move.getMovedPieceType(move)
            val attackedPieceType = Move.getAttackedPieceType(move)
            val moveType = Move.getMoveType(move)

            if (MoveType.isCastling(moveType)) {
                undoCastle(ourColor, fromSquare, toSquare)
            } else {
                movePiece(ourColor, movedPieceType, toSquare, fromSquare)
            }

            if (attackedPieceType != Piece.NONE) {
                var capturedSquare = toSquare
                if (attackedPieceType == Piece.PAWN) {
                    if (MoveType.TYPE_PASSANT == moveType) {
                        capturedSquare -= BitboardMove.PAWN_FORWARD[ourColor]
                    }
                }
                putPiece(theirColor, attackedPieceType, capturedSquare)
            }

            when (movedPieceType) {
                Piece.PAWN -> {
                    val promotedPiece = MoveType.getPromotedPiece(moveType)
                    if (promotedPiece != Piece.NONE) {
                        putPiece(ourColor, promotedPiece, toSquare)
                    }
                }
            }

            gameBitboard = colorBitboard[Color.WHITE] or colorBitboard[Color.BLACK]
            emptyBitboard = gameBitboard.inv()
            basicEvalInfo.update(this)
            popFromHistory()
        }
    }

    private fun updateCastlingRights(fromSquare: Int, toSquare: Int) {
        val castlingChange: Int = castlingRightsSquare[fromSquare] or castlingRightsSquare[toSquare]
        if (castlingChange and currentState.castlingRights != 0) {
            currentState.zobristKey = currentState.zobristKey xor Zobrist.CASTLING_RIGHT[currentState.castlingRights]
            currentState.castlingRights = currentState.castlingRights and -castlingChange
            currentState.zobristKey = currentState.zobristKey xor Zobrist.CASTLING_RIGHT[currentState.castlingRights]
        }
    }

    private fun clearEpSquare() {
        if (currentState.epSquare != Square.NONE) {
            currentState.zobristKey = currentState.pawnZobristKey xor Zobrist.PASSANT_FILE[File.getFile(currentState.epSquare)]
            currentState.epSquare = Square.NONE
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

        currentState.zobristKey = currentState.zobristKey xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookFrom] xor Zobrist.PIECE_SQUARE_TABLE[ourColor][Piece.ROOK][rookTo]
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