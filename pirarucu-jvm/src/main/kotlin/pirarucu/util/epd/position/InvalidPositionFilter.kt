package pirarucu.util.epd.position

import pirarucu.util.epd.BasicWorkSplitter
import pirarucu.util.epd.EpdInfo
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Phaser

class InvalidPositionFilter(threads: Int = 1) {
    private val forkJoinPool = ForkJoinPool(threads)

    fun filter(list: List<EpdInfo>): List<EpdInfo> {
        val phaser = Phaser()
        phaser.register()
        val worker = WorkerThread(list, phaser)
        forkJoinPool.invoke(worker)
        phaser.arriveAndAwaitAdvance()

        val result = mutableListOf<EpdInfo>()
        for (entry in list) {
            if (entry.valid) {
                result.add(entry)
            }
        }

        return result
    }

    class WorkerThread(
        list: List<EpdInfo>,
        start: Int,
        end: Int,
        workload: Int,
        phaser: Phaser
    ) : BasicWorkSplitter(list, start, end, workload, phaser) {

        constructor(list: List<EpdInfo>, phaser: Phaser) : this(list, 0, list.size, WORKLOAD, phaser)

        override fun createSubTask(
            list: List<EpdInfo>,
            start: Int,
            end: Int,
            workload: Int,
            phaser: Phaser
        ): WorkerThread {
            return WorkerThread(list, start, end, workload, phaser)
        }

        override fun evaluate() {
            val invalidPositionChecker = InvalidPositionChecker()
            for (index in start until end) {
                val entry = list[index]
                entry.valid = invalidPositionChecker.isValid(entry.fenPosition)
            }
        }
    }
}