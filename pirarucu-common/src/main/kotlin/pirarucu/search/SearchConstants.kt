package pirarucu.search

object SearchConstants {
    val ASPIRATION_WINDOW_SIZE = arrayOf(20, 22, 24, 26, 28, 30, 32, 34)

    const val NEGATIVE_SEE_DEPTH = 7
    const val NEGATIVE_SEE_MARGIN = -20

    const val LMR_MIN_DEPTH = 2
    const val LMR_MIN_MOVES = 1

    const val IID_DEPTH = 10

    const val LMP_DEPTH = 6
    const val LMP_MULTIPLIER = 3
    const val LMP_MIN_MOVES = 3

    const val PROB_CUT_LOW_DEPTH = 1
    const val PROB_CUT_DEPTH = 4
    const val PROB_CUT_MARGIN = 100

    const val SINGULAR_DETECTION_DEPTH = 8
    const val SINGULAR_DETECTION_MOVES = 5
}