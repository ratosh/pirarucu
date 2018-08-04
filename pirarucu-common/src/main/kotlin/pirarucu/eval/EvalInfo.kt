package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece

class EvalInfo {

    var zobristKey = 0L

    val mobilityArea = LongArray(Color.SIZE)
    val kingArea = LongArray(Color.SIZE)

    var passedPawnBitboard = Bitboard.EMPTY

    fun update(board: Board, attackInfo: AttackInfo) {
        attackInfo.update(board, Color.WHITE)
        attackInfo.update(board, Color.BLACK)
        if (board.zobristKey == zobristKey) {
            return
        }

        mobilityArea[Color.WHITE] = (board.colorBitboard[Color.WHITE] or
            attackInfo.attacksBitboard[Color.BLACK][Piece.PAWN]).inv()
        mobilityArea[Color.BLACK] = (board.colorBitboard[Color.BLACK] or
            attackInfo.attacksBitboard[Color.WHITE][Piece.PAWN]).inv()

        kingArea[Color.WHITE] = EvalConstants.KING_AREA_MASK[Color.WHITE][board.kingSquare[Color.WHITE]]
        kingArea[Color.BLACK] = EvalConstants.KING_AREA_MASK[Color.BLACK][board.kingSquare[Color.BLACK]]

        passedPawnBitboard = Bitboard.EMPTY
    }

}