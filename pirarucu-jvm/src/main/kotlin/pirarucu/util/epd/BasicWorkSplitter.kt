package pirarucu.util.epd

import java.util.concurrent.Phaser
import java.util.concurrent.RecursiveAction

abstract class BasicWorkSplitter(
    protected val list: List<EpdInfo>,
    protected val start: Int,
    protected val end: Int,
    private val workload: Int,
    private val phaser: Phaser
) : RecursiveAction() {

    init {
        phaser.register()
    }

    override fun compute() {
        if (end - start > workload) {
            val subTaskList = createSubTaskList()
            for (task in subTaskList) {
                task.fork()
            }
        } else {
            evaluate()
        }
        phaser.arrive()
    }

    private fun createSubTaskList(): List<BasicWorkSplitter> {
        val result = mutableListOf<BasicWorkSplitter>()

        val halfSize = (end - start) / 2
        result.add(createSubTask(list, start, start + halfSize, workload, phaser))
        result.add(createSubTask(list, start + halfSize, end, workload, phaser))

        return result
    }

    abstract fun createSubTask(
        list: List<EpdInfo>,
        start: Int,
        end: Int,
        workload: Int,
        phaser: Phaser
    ): BasicWorkSplitter

    abstract fun evaluate()

    companion object {
        const val WORKLOAD = 10000
    }
}