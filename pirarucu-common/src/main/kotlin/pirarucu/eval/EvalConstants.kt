package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Square
import pirarucu.game.GameConstants
import pirarucu.move.BitboardMove
import pirarucu.tuning.TunableConstants

object EvalConstants {

    const val SCORE_DRAW = 0

    const val SCORE_MAX: Int = Short.MAX_VALUE.toInt()
    const val SCORE_MIN: Int = -SCORE_MAX

    const val SCORE_MATE = SCORE_MAX - GameConstants.MAX_PLIES

    const val SCORE_UNKNOWN = Short.MIN_VALUE.toInt()

    const val SCORE_KNOW_WIN = 10000

    val SCORE_DRAWISH_MATERIAL = TunableConstants.MATERIAL_SCORE[Piece.PAWN]

    var PAWN_EVAL_CACHE = true

    val PAWN_SHIELD_MASK = LongArray(Square.SIZE)
    val KING_AREA_MASK = Array(Color.SIZE) { LongArray(Square.SIZE) }

    init {
        for (square in Square.A1 until Square.SIZE) {
            val file = File.getFile(square)
            val bitboard = Bitboard.getBitboard(square)
            PAWN_SHIELD_MASK[square] = Bitboard.FILES[file] or Bitboard.FILES_ADJACENT[file]
            KING_AREA_MASK[Color.WHITE][square] = BitboardMove.KING_MOVES[square] or bitboard
            if (Square.isValid(square + BitboardMove.NORTH)) {
                KING_AREA_MASK[Color.WHITE][square] = KING_AREA_MASK[Color.WHITE][square] or
                    BitboardMove.KING_MOVES[square + BitboardMove.NORTH]
            }
            KING_AREA_MASK[Color.BLACK][square] = BitboardMove.KING_MOVES[square] or bitboard
            if (Square.isValid(square + BitboardMove.SOUTH)) {
                KING_AREA_MASK[Color.BLACK][square] = KING_AREA_MASK[Color.BLACK][square] or
                    BitboardMove.KING_MOVES[square + BitboardMove.SOUTH]
            }
        }
    }
}