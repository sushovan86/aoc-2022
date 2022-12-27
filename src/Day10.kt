class Signal private constructor(private val instructions: List<String>) {

    enum class STRATEGY {
        PART1, PART2
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
        if (strategy == STRATEGY.PART1) recordSignalPart1() else recordSignalPart2()

    private fun recordSignalPart1() {
        if (cycle % 40 == 20) {
            signalStrength += (cycle * register)
        }
        cycle++
    }

    private fun recordSignalPart2() {

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

    signal.process(Signal.STRATEGY.PART1)
    check(signal.signalStrength == 13140)

    signal.process(strategy = Signal.STRATEGY.PART2)
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

    actualSignal.process(Signal.STRATEGY.PART1)
    println(actualSignal.signalStrength)

    actualSignal.process(Signal.STRATEGY.PART2)
    print(actualSignal.crtDisplay)
}