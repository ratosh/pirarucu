package pirarucu.tuning.sts

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.eval.AttackInfo
import pirarucu.search.MainSearch
import pirarucu.search.SearchInfo
import pirarucu.search.SearchOptions
import pirarucu.util.EpdInfo
import java.util.concurrent.Callable

class StsErrorCalculator : Callable<Int> {

    private val epdInfoList = mutableListOf<EpdInfo>()
    private var board: Board = Board()

    private var searchOptions = SearchOptions()
    private var mainSearch = MainSearch(searchOptions)

    fun addEpdInfo(epdInfo: EpdInfo) {
        epdInfoList.add(epdInfo)
    }

    init {
        searchOptions.minSearchTimeLimit = 60000L
        searchOptions.maxSearchTimeLimit = 60000L
        searchOptions.searchTimeIncrement = 1000L
    }

    var depth: Int
        get() = searchOptions.depth
        set(value) {
            searchOptions.depth = value
        }

    override fun call(): Int? {
        return calculateError()
    }

    private fun calculateError(): Int {
        var score = 0
        searchOptions.stop = false
        for (entry in epdInfoList) {
            BoardFactory.setBoard(entry.fenPosition, board)
            searchOptions.stop = false

            mainSearch.search(board)

            score += entry.getMoveScore(board, mainSearch.searchInfo.bestMove)
        }
        return score
    }

}
