package pirarucu.board.factory

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import kotlin.math.max

/**
 * Fen string factory.
 */
object FenFactory {

    private const val EMPTY_SPACE = ' '
    private const val RANK_SEPARATOR = '/'

    /**
     * Get the board string representation in Forsyth–Edwards Notation
     */
    fun getFen(board: Board): String {
        val stringBuilder = StringBuilder()
        var emptyCount = 0

        // Board piece representation
        for (rank in Rank.RANK_8 downTo Rank.RANK_1) {
            for (file in File.FILE_A until File.SIZE) {
                val square = Square.getSquare(file, rank)
                val bitboard = Bitboard.getBitboard(square)
                val piece = board.pieceTypeBoard[square]
                if (piece == Piece.NONE) {
                    emptyCount++
                    continue
                }
                if (emptyCount > 0) {
                    stringBuilder.append(emptyCount)
                    emptyCount = 0
                }

                if (file <= File.FILE_H) {
                    val color = if (board.pieceBitboard[Color.WHITE][Piece.NONE] and bitboard != Bitboard.EMPTY) 0 else 1
                    stringBuilder.append(Piece.toString(color, piece))
                }
            }
            if (emptyCount > 0) {
                stringBuilder.append(emptyCount)
                emptyCount = 0
            }

            if (rank > Rank.RANK_1) {
                stringBuilder.append(RANK_SEPARATOR)
            }
        }

        // Extra information
        val castlingRights = CastlingRights.toString(board.castlingRights)
        stringBuilder.append(EMPTY_SPACE)
            .append(Color.toString(board.colorToMove))
            .append(EMPTY_SPACE)
            .append(castlingRights)
            .append(EMPTY_SPACE)
            .append(Square.toString(board.epSquare))
            .append(EMPTY_SPACE)
            .append(board.rule50)
            .append(EMPTY_SPACE)
            .append(max(1, (board.moveNumber - board.colorToMove) / 2))

        return stringBuilder.toString()
    }
}