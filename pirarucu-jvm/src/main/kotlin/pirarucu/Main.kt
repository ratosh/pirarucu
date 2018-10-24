package pirarucu

import pirarucu.benchmark.Benchmark
import pirarucu.board.Bitboard
import pirarucu.board.BoardUtil
import pirarucu.board.CastlingRights
import pirarucu.board.Color
import pirarucu.board.File
import pirarucu.board.Piece
import pirarucu.board.Rank
import pirarucu.board.Square
import pirarucu.board.factory.BoardFactory
import pirarucu.board.factory.FenFactory
import pirarucu.eval.DrawEvaluator
import pirarucu.eval.EvalConstants
import pirarucu.eval.Evaluator
import pirarucu.eval.PawnEvaluator
import pirarucu.eval.StaticExchangeEvaluator
import pirarucu.game.GameConstants
import pirarucu.hash.HashConstants
import pirarucu.hash.Zobrist
import pirarucu.move.BitboardMove
import pirarucu.move.Move
import pirarucu.move.MoveType
import pirarucu.search.MultiThreadedSearch
import pirarucu.tuning.TunableConstants
import pirarucu.uci.InputHandler
import pirarucu.uci.UciInput
import pirarucu.util.Perft
import pirarucu.util.SplitValue
import pirarucu.util.XorShiftRandom
import kotlin.system.measureNanoTime

fun main(args: Array<String>) {
    initialize()
    if (args.isNotEmpty()) {
        when (args[0]) {
            "bench" -> {
                val benchDepth = if (args.size > 1) {
                    Integer.parseInt(args[1])
                } else {
                    Benchmark.DEFAULT_BENCHMARK_DEPTH
                }
                Benchmark.runBenchmark(benchDepth)
            }
            else -> {
                println("Unknown argument")
            }
        }
    } else {
        val uciInput = UciInput(InputHandler())
        while (true) {
            try {
                val line = readLine()
                line ?: return
                uciInput.process(line)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * Pre load needed classes.
 */
fun initialize() {
    val nanoTime = measureNanoTime {
        BoardFactory
        FenFactory
        Bitboard
        BoardUtil
        CastlingRights
        Color
        File
        Piece
        Rank
        Square
        DrawEvaluator
        EvalConstants
        Evaluator
        PawnEvaluator
        StaticExchangeEvaluator
        GameConstants
        HashConstants
        Zobrist
        BitboardMove
        Move
        MoveType
        TunableConstants
        Perft
        SplitValue
        XorShiftRandom
        MultiThreadedSearch
    }
    val msTime = nanoTime / 1_000_000
    println("Initialized in $msTime ms")
}
