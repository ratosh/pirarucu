package pirarucu.board.factory

import pirarucu.board.Board
import pirarucu.board.BoardUtil
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.move.BitboardMove
import kotlin.math.max

object BoardFactory {
    const val STARTER_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    private const val SPLITTER = '/'

    private const val BOARD_POSITION = 0
    private const val COLOR_POSITION = 1
    private const val CASTLING_POSITION = 2
    private const val EP_SQUARE_POSITION = 3
    private const val RULE_50_POSITION = 4
    private const val MOVE_NUMBER_POSITION = 5

    fun getBoard(): Board {
        return getBoard(STARTER_FEN)
    }

    fun getBoard(fen: String): Board {
        val result = Board()
        setBoard(fen, result)
        return result
    }

    fun setBoard(fen: String, board: Board) {
        board.reset()
        var square = Square.A8

        val tokenList = fen.split(' ')

        for (token in tokenList[BOARD_POSITION]) {
            if (token == SPLITTER) {
                square -= BitboardMove.DOUBLE_PAWN_FORWARD[Color.WHITE]
                continue
            }

            val rank = Rank.getRank(token)

            if (Rank.isValid(rank)) {
                square += rank + 1
            } else {
                val piece = Piece.getPiece(token)
                val pieceColor = Piece.getPieceColor(token)
                board.putPiece(pieceColor, piece, square)
                square += 1
            }
        }

        val colorToMove = Color.getColor(tokenList[COLOR_POSITION].single())

        val castlingRigts = CastlingRights.getCastlingRight(tokenList[CASTLING_POSITION])

        val epSquare = Square.getSquare(tokenList[EP_SQUARE_POSITION])

        var rule50 = 0
        var moveNumber = 0
        if (tokenList.size > RULE_50_POSITION) {
            rule50 = tokenList[RULE_50_POSITION].toInt()
            if (tokenList.size > MOVE_NUMBER_POSITION) {
                moveNumber = tokenList[MOVE_NUMBER_POSITION].toInt()
            }
        }

        moveNumber = max(Color.SIZE * (moveNumber - Color.BLACK), Color.WHITE) + colorToMove

        board.colorToMove = colorToMove
        board.nextColorToMove = Color.invertColor(colorToMove)
        board.castlingRights = castlingRigts
        board.epSquare = epSquare
        board.rule50 = rule50
        board.moveNumber = moveNumber

        board.setInitialKingSquare()
        board.updateBasicInfo()
        board.basicEvalInfo.update(board)
        board.basicEvalInfo.updatePinned(board)
        BoardUtil.updateZobristKeys(board)
    }
}
