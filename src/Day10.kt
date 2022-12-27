class Signal private constructor(private val instructions: List<String>) {

    enum class STRATEGY {
        RECORD_SIGNAL_STRENGTH, RENDER_CRT
    }

    private var cycle = 1
    private var register = 1
    var signalStrength = 0
        private set

    private var crtPosition = 0
    var crtDisplay: String = ""
        private set

    private fun reset() {
        cycle = 1
        register = 1
        signalStrength = 0
        crtDisplay = ""
    }

    private fun recordSignal(strategy: STRATEGY) =
        if (strategy == STRATEGY.RECORD_SIGNAL_STRENGTH) calculateSignalStrength() else renderCRT()

    private fun calculateSignalStrength() {
        if (cycle % 40 == 20) {
            signalStrength += (cycle * register)
        }
        cycle++
    }

    private fun renderCRT() {

        if (cycle % 40 == 1) {
            crtDisplay += "\n"
            crtPosition = 0
        }

        crtDisplay += if (register in (crtPosition - 1)..(crtPosition + 1)) {
            "#"
        } else {
            "."
        }
        cycle++
        crtPosition++
    }

    companion object {
        fun loadData(inputList: List<String>) = Signal(inputList)
    }

    fun process(strategy: STRATEGY): Signal {

        reset()

        for ((line, input) in instructions.withIndex()) {

            when (val operand = input.substringBefore(" ")) {
                "addx" -> {
                    recordSignal(strategy)

                    val param = input.substringAfter(" ").trim().toIntOrNull()
                        ?: error("Missing param against operand $operand at line ${line + 1}")
                    recordSignal(strategy)

                    register += param
                }

                "noop" -> recordSignal(strategy)
                else -> error("Invalid operand at ${line + 1}")
            }
        }
        return this
    }
}

fun main() {

    val testInputList = readInput("Day10_test")
    val signal = Signal.loadData(testInputList)

    signal.process(Signal.STRATEGY.RECORD_SIGNAL_STRENGTH)
    check(signal.signalStrength == 13140)

    signal.process(Signal.STRATEGY.RENDER_CRT)
    check(
        """
        |##..##..##..##..##..##..##..##..##..##..
        |###...###...###...###...###...###...###.
        |####....####....####....####....####....
        |#####.....#####.....#####.....#####.....
        |######......######......######......####
        |#######.......#######.......#######.....
    """.trimMargin() == signal.crtDisplay.trim()
    )

    val actualInputList = readInput("Day10")
    val actualSignal = Signal.loadData(actualInputList)

    actualSignal.process(Signal.STRATEGY.RECORD_SIGNAL_STRENGTH)
    println(actualSignal.signalStrength)

    actualSignal.process(Signal.STRATEGY.RENDER_CRT)
    print(actualSignal.crtDisplay)
}