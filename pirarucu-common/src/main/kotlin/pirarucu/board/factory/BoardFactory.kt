package pirarucu.board.factory

import pirarucu.board.Board
import pirarucu.board.BoardUtil
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import kotlin.math.max

object BoardFactory {
    const val STARTER_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    private const val SPLITTER = '/'

    fun getBoard(): Board {
        return getBoard(STARTER_FEN)
    }

    fun getBoard(fen: String): Board {
        val result = Board()

        var square = Square.A8

        val tokenList = fen.split(' ')

        for (token in tokenList[0]) {
            if (token == SPLITTER) {
                square -= 16
                continue
            }

            val rank = Rank.getRank(token)

            if (Rank.isValid(rank)) {
                square += rank + 1
            } else {
                val piece = Piece.getPiece(token)
                val pieceColor = Piece.getPieceColor(token)
                result.putPiece(pieceColor, piece, square)
                square += 1
            }
        }

        val colorToMove = Color.getColor(tokenList[1].single())

        val castlingRigts = CastlingRights.getCastlingRight(tokenList[2])

        val epSquare = Square.getSquare(tokenList[3])

        var rule50 = 0
        var moveNumber = 0
        if (tokenList.size > 4) {
            rule50 = tokenList[4].toInt()
            if (tokenList.size > 5) {
                moveNumber = tokenList[5].toInt()
            }
        }

        moveNumber = max(2 * (moveNumber - 1), 0) + colorToMove

        result.colorToMove = colorToMove
        result.castlingRights = castlingRigts
        result.epSquare = epSquare
        result.rule50 = rule50
        result.moveNumber = moveNumber

        result.updateBasicInfo()
        BoardUtil.updateZobristKeys(result)

        return result
    }
}