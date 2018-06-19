package pirarucu.eval

import pirarucu.board.Bitboard
import pirarucu.board.File
import pirarucu.board.Square
import pirarucu.game.GameConstants

object EvalConstants {

    const val SCORE_DRAW = 0

    const val SCORE_MAX: Int = Short.MAX_VALUE.toInt()
    const val SCORE_MIN: Int = -SCORE_MAX

    const val SCORE_MATE = SCORE_MAX - GameConstants.MAX_PLIES

    const val SCORE_UNKNOWN = Short.MIN_VALUE.toInt()

    const val SCORE_KNOW_WIN = 10000

    var PAWN_EVAL_CACHE = true

    val PAWN_SHIELD_MASK = LongArray(Square.SIZE)

    init {
        for (square in Square.A1 until Square.SIZE) {
            val file = File.getFile(square)
            PAWN_SHIELD_MASK[square] = Bitboard.FILES[file] or Bitboard.FILES_ADJACENT[file]
        }
    }
}