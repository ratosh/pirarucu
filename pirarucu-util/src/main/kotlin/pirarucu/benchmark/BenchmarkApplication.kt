package pirarucu.benchmark

fun main() {
    val speed = LongArray(1)
    for (tries in 0 until speed.size) {
        speed[tries] = Benchmark.runBenchmark(Benchmark.DEFAULT_BENCHMARK_DEPTH)
    }
    println("Time taken " + speed.sum())
    println("Max " + speed.max()!!)
    println("Min " + speed.min()!!)
}