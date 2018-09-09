package pirarucu.tuning.sts

import pirarucu.board.Board
import pirarucu.board.factory.BoardFactory
import pirarucu.search.MainSearch
import pirarucu.search.SearchOptions
import pirarucu.search.SimpleSearchInfoListener
import pirarucu.util.EpdInfo
import java.util.concurrent.Callable

class StsErrorCalculator : Callable<Int> {

    private val epdInfoList = mutableListOf<EpdInfo>()
    private val board: Board = Board()

    private val searchOptions = SearchOptions()
    private val mainSearch = MainSearch(searchOptions, SimpleSearchInfoListener())

    fun addEpdInfo(epdInfo: EpdInfo) {
        epdInfoList.add(epdInfo)
    }

    init {
        searchOptions.minSearchTime = 60000L
        searchOptions.maxSearchTime = 60000L
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
        for (entry in epdInfoList) {
            BoardFactory.setBoard(entry.fenPosition, board)
            searchOptions.startControl()

            mainSearch.search(board)

            score += entry.getMoveScore(board, mainSearch.searchInfo.bestMove)
        }
        return score
    }

}
