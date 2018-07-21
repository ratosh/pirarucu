package pirarucu.board

object Rank {
    private const val RANK_1_TOKEN = '1'
    const val RANK_SHIFT = 3

    const val INVALID = -1

    const val RANK_1 = 0
    const val RANK_2 = 1
    const val RANK_3 = 2
    const val RANK_4 = 3
    const val RANK_5 = 4
    const val RANK_6 = 5
    const val RANK_7 = 6
    const val RANK_8 = 7

    const val SIZE = 8

    fun getRank(square: Int): Int {
        return square shr RANK_SHIFT
    }

    fun getRank(token: Char): Int {
        return token - RANK_1_TOKEN
    }

    fun getRelativeRank(color: Int, rank: Int): Int {
        return rank xor color * RANK_8
    }

    fun invertRank(rank: Int): Int {
        return rank xor RANK_8
    }

    fun isValid(rank: Int): Boolean {
        return rank in RANK_1 until SIZE
    }

    fun toString(rank: Int): Char {
        return RANK_1_TOKEN + rank
    }
}
