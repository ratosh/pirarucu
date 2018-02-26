package pirarucu.board

object Rank {

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
        return square shr 3
    }

    fun getRank(token: Char): Int {
        return token - '1'
    }

    fun getRelativeRank(color: Int, rank: Int): Int {
        return rank xor color * 7
    }

    fun invertRank(rank: Int): Int {
        return rank xor 7
    }

    fun isValid(rank: Int): Boolean {
        return rank in RANK_1 until SIZE
    }
}
