package pirarucu.benchmark

import pirarucu.search.SearchConstants

fun main(args: Array<String>) {
    var speed = LongArray(1)
    for (tries in 0 until speed.size) {
        speed[tries] = Benchmark.runBenchmark(Benchmark.DEFAULT_BENCHMARK_DEPTH)
    }
    for (index in SearchConstants.hits.indices) {
        if (SearchConstants.tries[index] > 0) {
            println("$index " + SearchConstants.hits[index] * 1_000 / SearchConstants.tries[index])
        } else {
            println("$index unknown")
        }
    }
    println("Time taken " + speed.sum())
    println("Max " + speed.max()!!)
    println("Min " + speed.min()!!)
}