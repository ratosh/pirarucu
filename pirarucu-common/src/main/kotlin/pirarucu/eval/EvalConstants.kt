package pirarucu.eval

import pirarucu.game.GameConstants

object EvalConstants {

    const val SCORE_DRAW = 0

    const val SCORE_MAX: Int = Short.MAX_VALUE.toInt()
    const val SCORE_MIN: Int = -SCORE_MAX

    const val SCORE_MATE = SCORE_MAX - GameConstants.MAX_PLIES

    const val SCORE_UNKNOWN = Short.MIN_VALUE.toInt()

    const val SCORE_KNOW_WIN = 10000

    const val PAWN_EVAL_CACHE = true
}