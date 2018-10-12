package pirarucu.util.position

import pirarucu.util.epd.EpdInfo
import java.util.Queue
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future

class InvalidPositionFilter(var threads: Int = 1) {

    fun filter(list: List<EpdInfo>): List<EpdInfo> {
        val threadList = mutableListOf<Future<List<EpdInfo>>>()
        val executor = Executors.newFixedThreadPool(threads)!!
        val pool = ConcurrentLinkedQueue(list)

        for (index in 0 until threads) {
            threadList.add(executor.submit(InvalidPositionCheckerThread(pool)))
        }
        val result = mutableListOf<EpdInfo>()
        for (thread in threadList) {
            result.addAll(thread.get())
        }
        executor.shutdown()

        return result
    }

    class InvalidPositionCheckerThread(private val pool: Queue<EpdInfo>) : Callable<List<EpdInfo>> {

        private val invalidPositionChecker = InvalidPositionChecker()

        override fun call(): List<EpdInfo> {
            val epdInfoList = mutableListOf<EpdInfo>()
            while (pool.isNotEmpty()) {
                val epdInfo = pool.poll()
                if (epdInfo != null) {
                    if (invalidPositionChecker.isValid(epdInfo.fenPosition)) {
                        epdInfoList.add(epdInfo)
                    }
                }
            }
            return epdInfoList
        }
    }
}