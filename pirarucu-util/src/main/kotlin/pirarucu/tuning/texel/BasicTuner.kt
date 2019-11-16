package pirarucu.tuning.texel

import pirarucu.tuning.TunableConstants
import pirarucu.tuning.calculator.ICalculator
import pirarucu.tuning.evaluator.IEvaluator
import pirarucu.util.PlatformSpecific
import pirarucu.util.epd.EpdInfo

object BasicTuner {
    fun optimize(evaluator: IEvaluator,
                 calculator: ICalculator,
                 tuningController: TexelTuningController,
                 epdList: List<EpdInfo>) {
        println("Total entries ${epdList.size}")
        evaluator.evaluate(epdList)
        calculator.computeConstant(epdList)
        var bestError = calculator.calculate(epdList)
        println("Start error $bestError")
        val startTime = PlatformSpecific.currentTimeMillis()
        tuningController.initialResult(bestError)

        while (true) {
            while (tuningController.hasNext()) {
                if (tuningController.next()) {
                    TunableConstants.update()
                    evaluator.evaluate(epdList)
                    val error = calculator.calculate(epdList)
                    tuningController.reportCurrent(error)
                    if (error < bestError) {
                        bestError = error
                    }
                }
            }
            val timeTaken = PlatformSpecific.currentTimeMillis() - startTime
            println("Total time taken $timeTaken millis")
            if (tuningController.finishInteraction()) {
                println("Seems like we are not improving")
                break
            }
        }

        println("Optimization done.")
        tuningController.printBestElements()
    }
}