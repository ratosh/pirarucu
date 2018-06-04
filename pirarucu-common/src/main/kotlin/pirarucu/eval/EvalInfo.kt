package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Board
import pirarucu.board.Color
import pirarucu.board.Piece

class EvalInfo {

    var zobristKey = 0L

    var mobilityArea = LongArray(Color.SIZE)

    var protectedOutpost = LongArray(Color.SIZE)
    var unprotectedOutpost = LongArray(Color.SIZE)


    fun update(board: Board, attackInfo: AttackInfo) {
        attackInfo.update(board, Color.WHITE)
        attackInfo.update(board, Color.BLACK)
        if (board.zobristKey == zobristKey) {
            return
        }

        mobilityArea[Color.WHITE] = (board.emptyBitboard or board.colorBitboard[Color.BLACK]) and
            attackInfo.attacksBitboard[Color.BLACK][Piece.PAWN].inv()
        mobilityArea[Color.BLACK] = (board.emptyBitboard or board.colorBitboard[Color.WHITE]) and
            attackInfo.attacksBitboard[Color.WHITE][Piece.PAWN].inv()

        protectedOutpost[Color.WHITE] = Bitboard.OUTPOST[Color.WHITE] and
            attackInfo.attacksBitboard[Color.WHITE][Piece.PAWN]
        protectedOutpost[Color.BLACK] = Bitboard.OUTPOST[Color.BLACK] and
            attackInfo.attacksBitboard[Color.BLACK][Piece.PAWN]

        unprotectedOutpost[Color.WHITE] = Bitboard.OUTPOST[Color.WHITE] and
            attackInfo.attacksBitboard[Color.WHITE][Piece.PAWN].inv()
        unprotectedOutpost[Color.BLACK] = Bitboard.OUTPOST[Color.BLACK] and
            attackInfo.attacksBitboard[Color.BLACK][Piece.PAWN].inv()


    }

}