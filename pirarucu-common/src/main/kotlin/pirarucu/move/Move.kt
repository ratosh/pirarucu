package pirarucu.move

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square


/**
 * 16 Bits move representation
 */
object Move {
    // Predefined moves
    const val NONE = 0
    const val NULL = 65535

    const val NONE_STRING = "none"
    const val NULL_STRING = "null"

    private const val TO_SHIFT = 6
    private const val MOVE_TYPE_SHIFT = 12

    private const val FROM_TO_MASK = 0xFFF

    const val SIZE = 16

    fun createPromotionMove(fromSquare: Int,
                            toSquare: Int,
                            moveType: Int): Int {
        return createMove(fromSquare, toSquare, moveType)
    }

    fun createPassantMove(fromSquare: Int,
                          toSquare: Int): Int {
        return createMove(fromSquare, toSquare, MoveType.TYPE_PASSANT)
    }

    fun createCastlingMove(fromSquare: Int,
                           toSquare: Int): Int {
        return createMove(fromSquare, toSquare, MoveType.TYPE_CASTLING)
    }

    fun createMove(fromSquare: Int,
                   toSquare: Int,
                   moveType: Int = MoveType.TYPE_NORMAL): Int {
        return (fromSquare or (toSquare shl TO_SHIFT) or (moveType shl MOVE_TYPE_SHIFT))
    }

    fun isValid(move: Int): Boolean {
        return move != NONE && move != NULL
    }

    fun getFromSquare(move: Int): Int {
        return move and Square.H8
    }

    fun getToSquare(move: Int): Int {
        return (move ushr TO_SHIFT) and Square.H8
    }

    fun getMoveType(move: Int): Int {
        return move.ushr(MOVE_TYPE_SHIFT) and 0xf
    }

    fun toString(move: Int): String {
        return when (move) {
            NONE -> NONE_STRING
            NULL -> NULL_STRING
            else -> {
                val sb = StringBuilder()
                val moveType = getMoveType(move)
                sb.append(Square.toString(getFromSquare(move)))
                sb.append(Square.toString(getToSquare(move)))
                if (MoveType.isPromotion(moveType)) {
                    sb.append(Piece.toString(Color.BLACK, MoveType.getPromotedPiece(moveType)))
                }
                sb.toString()
            }
        }
    }

    fun getMove(board: Board, token: String): Int {
        var moveType = MoveType.TYPE_NORMAL
        val fromSquare = Square.getSquare(token.substring(0, 2))
        val toSquare = Square.getSquare(token.substring(2, 4))

        val movedPiece = board.pieceTypeBoard[fromSquare]

        when (movedPiece) {
            Piece.PAWN -> {
                val toBitboard = Bitboard.getBitboard(toSquare)
                if (toBitboard and Bitboard.PROMOTION_BITBOARD != Bitboard.EMPTY) {
                    moveType = if (token.length == 4) {
                        MoveType.getPromotionMoveType(Piece.QUEEN)
                    } else {
                        MoveType.getPromotionMoveType(Piece.getPiece(token[4]))
                    }
                } else if (toSquare == board.epSquare) {
                    moveType = MoveType.TYPE_PASSANT
                }
            }
            Piece.KING -> {
                if (Square.SQUARE_DISTANCE[fromSquare][toSquare] > 1) {
                    moveType = MoveType.TYPE_CASTLING
                }
            }
        }
        return Move.createMove(fromSquare, toSquare, moveType)
    }

    fun areMovesCompatibles(board: Board, ourMove: Int, algebraicMove: String): Boolean {
        // Remove unwanted characters
        var move = algebraicMove.replace("+", "").replace("x", "")
            .replace("-", "").replace("=", "")
            .replace("#", "").replace("?", "")
            .replace("!", "").replace(" ", "")
            .replace("0", "o").replace("O", "o")

        // Fixing castle string
        when (move) {
            "oo" -> {
                val castlingIndex = CastlingRights.getCastlingRightIndex(board.colorToMove, CastlingRights.KING_SIDE)
                move = Square.toString(board.kingSquare[board.colorToMove]) +
                    Square.toString(CastlingRights.KING_FINAL_SQUARE[castlingIndex])
            }
            "ooo" -> {
                val castlingIndex = CastlingRights.getCastlingRightIndex(board.colorToMove, CastlingRights.QUEEN_SIDE)
                move = Square.toString(board.kingSquare[board.colorToMove]) +
                    Square.toString(CastlingRights.KING_FINAL_SQUARE[castlingIndex])
            }
            else -> when (move.length) {
                2 -> {
                    move = Square.toString(getFromSquare(ourMove)) + move
                }
                3 -> {
                    if (board.pieceTypeBoard[getFromSquare(ourMove)] == Piece.PAWN) {
                        if (File.toString(File.getFile(getFromSquare(ourMove))) != move[0]) {
                            return false
                        }
                    } else {
                        val piece = Piece.getPiece(move[0])
                        if (board.pieceTypeBoard[getFromSquare(ourMove)] != piece) {
                            return false
                        }
                    }
                    move = Square.toString(getFromSquare(ourMove)) + move.substring(1, move.length)
                }
                4 -> {
                    val piece = Piece.getPiece(move[0])
                    if (board.pieceTypeBoard[getFromSquare(ourMove)] != piece) {
                        return false
                    }
                    val file = File.getFile(move[1])
                    if (file != File.INVALID) {
                        if (File.getFile(getFromSquare(ourMove)) != file) {
                            return false
                        }
                    } else {
                        val rank = Rank.getRank(move[1])
                        if (rank != Rank.INVALID && Rank.getRank(getFromSquare(ourMove)) != rank) {
                            return false
                        }
                    }
                    move = Square.toString(getFromSquare(ourMove)) + move.substring(2, move.length)
                }
                5 -> {
                    if (Piece.getPiece(move[4]) == Piece.NONE) {
                        move = move.substring(1, move.length)
                    }
                }
            }
        }
        return getMove(board, move) == ourMove
    }

    fun getFromTo(move: Int): Int {
        return move and FROM_TO_MASK
    }

}
