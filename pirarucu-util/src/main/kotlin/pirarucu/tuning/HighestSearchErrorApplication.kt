package pirarucu.tuning

import pirarucu.uci.UciOutput
import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureNanoTime

object HighestSearchErrorApplication {

    private const val numberOfThreads = 4
    private val workers = arrayOfNulls<HighestSearchErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\test_set.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        UciOutput.silent = true
        // setup
        for (i in workers.indices) {
            workers[i] = HighestSearchErrorCalculator(10, 6)
        }
        var workerIndex = 0
        val iterator = epdFileLoader.getEpdInfoList()
        for (epdInfo in iterator) {
            workers[workerIndex]!!.addEpdInfo(epdInfo)
            workerIndex = if (workerIndex == numberOfThreads - 1) 0 else workerIndex + 1
        }
        val timeTaken = measureNanoTime {
            println("Total error " + executeTest())
        }
        println("Time taken " + (timeTaken / 1_000_000))

        for (worker in workers) {
            println(worker.toString())
        }
        executor.shutdown()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun executeTest(): Double {
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
