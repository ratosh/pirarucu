package pirarucu.eval

import pirarucu.board.Color
import pirarucu.tuning.TunableConstants
import pirarucu.util.SplitValue

object EvalDebug {

    var ENABLED = false

    val psqScore = IntArray(Color.SIZE)
    val material = IntArray(Color.SIZE)
    val materialImbalance = IntArray(Color.SIZE)

    var score = 0
    var phase = 0
    var result = 0

    override fun toString(): String {
        val buffer = StringBuilder()

        buffer.append("--- PSQ \n")
        buffer.append("WHITE: " + SplitValue.getFirstPart(psqScore[Color.WHITE]) +
            " | " + SplitValue.getSecondPart(psqScore[Color.WHITE]) + "\n")
        buffer.append("BLACK: " + SplitValue.getFirstPart(psqScore[Color.BLACK]) +
            " | " + SplitValue.getSecondPart(psqScore[Color.BLACK]) + "\n")

        buffer.append("--- MATERIAL \n")
        buffer.append("WHITE: " + SplitValue.getFirstPart(material[Color.WHITE]) +
            " | " + SplitValue.getSecondPart(material[Color.WHITE]) + "\n")
        buffer.append("BLACK: " + SplitValue.getFirstPart(material[Color.BLACK]) +
            " | " + SplitValue.getSecondPart(material[Color.BLACK]) + "\n")

        buffer.append("--- MATERIAL IMBALANCE \n")
        buffer.append("WHITE: " + SplitValue.getFirstPart(materialImbalance[Color.WHITE]) +
            " | " + SplitValue.getSecondPart(materialImbalance[Color.WHITE]) + "\n")
        buffer.append("BLACK: " + SplitValue.getFirstPart(materialImbalance[Color.BLACK]) +
            " | " + SplitValue.getSecondPart(materialImbalance[Color.BLACK]) + "\n")
        buffer.append("--- \n")
        buffer.append("Score: " + SplitValue.getFirstPart(score) +
            " | " + SplitValue.getSecondPart(score) + "\n")

        val phaseMax = TunableConstants.PHASE_MAX
        buffer.append("--- Current phase ($phase|$phaseMax)\n")
        buffer.append("Result: $result\n")

        return buffer.toString()
    }
}