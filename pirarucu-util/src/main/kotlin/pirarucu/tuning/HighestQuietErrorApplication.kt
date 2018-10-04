package pirarucu.tuning

import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureNanoTime

object HighestQuietErrorApplication {

    private const val numberOfThreads = 1
    private val workers = arrayOfNulls<HighestQuietErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\quiet_labeled.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // setup
        for (i in workers.indices) {
            workers[i] = HighestQuietErrorCalculator(10)
        }
        val iterator = epdFileLoader.getEpdInfoList()
        var workerIndex = 0
        for (epdInfo in iterator) {
            workers[workerIndex]!!.addEpdInfo(epdInfo)
            workerIndex = if (workerIndex == numberOfThreads - 1) 0 else workerIndex + 1
        }
        val time = measureNanoTime {
            println("Quiet error " + executeQuietTest())
        }
        println("Time taken " + time / 1_000_000)
        for (worker in workers) {
            println(worker.toString())
        }
        executor.shutdown()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun executeQuietTest(): Double {
        val list = ArrayList<Future<Double>>()
        for (i in workers.indices) {
            val submit = executor.submit(workers[i])
            list.add(submit)
        }
        var totalError = 0.0
        for (future in list) {
            val value = future.get()
            totalError += value!!
        }
        return totalError / numberOfThreads
    }
}
