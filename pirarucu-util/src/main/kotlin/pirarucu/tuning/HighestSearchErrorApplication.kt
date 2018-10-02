package pirarucu.tuning

import pirarucu.hash.TranspositionTable
import pirarucu.uci.UciOutput
import pirarucu.util.EpdFileLoader
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

object HighestSearchErrorApplication {

    private const val numberOfThreads = 1
    private val workers = arrayOfNulls<HighestSearchErrorCalculator>(numberOfThreads)
    private val executor = Executors.newFixedThreadPool(numberOfThreads)!!
    private val epdFileLoader = EpdFileLoader("g:\\chess\\epds\\FENS_jeffrey.epd")

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        TranspositionTable.resize(2)
        UciOutput.silent = true
        // setup
        for (i in workers.indices) {
            workers[i] = HighestSearchErrorCalculator(10, 8)
        }
        var workerIndex = 0
        val iterator = epdFileLoader.getEpdInfoList()
        for (epdInfo in iterator) {
            workers[workerIndex]!!.addEpdInfo(epdInfo)
            workerIndex = if (workerIndex == numberOfThreads - 1) 0 else workerIndex + 1
        }
        println("Total error " + executeTest())
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
