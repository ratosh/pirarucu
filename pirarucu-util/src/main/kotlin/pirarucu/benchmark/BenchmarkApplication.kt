package pirarucu.benchmark

fun main() {
    val speed = LongArray(1)
    for (tries in speed.indices) {
        speed[tries] = Benchmark.runBenchmark()
    }
    println("Time taken " + speed.sum())
    println("Max " + speed.max()!!)
    println("Min " + speed.min()!!)
}