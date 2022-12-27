typealias WorryLevelOperation = (Long) -> Long

class MonkeyProblem {

    private data class Monkey(
        val monkeyIndex: Int,
        val items: List<Long>,
        val operation: String,
        val rightOperand: String,
        val divisorPredicateOperand: Int,
        val trueTargetMonkey: Int,
        val falseTargetMoney: Int
    ) {

        var inspections: Long = 0
        val worryItems = ArrayDeque<Long>()

        fun initialize() {
            inspections = 0
            worryItems.clear()
            worryItems += items
        }

        fun applyMonkeyOperation(oldValue: Long): Long = when (operation) {
            "*" -> oldValue * (rightOperand.toLongOrNull() ?: oldValue)
            "+" -> oldValue + (rightOperand.toLongOrNull() ?: oldValue)
            else -> error("Invalid Operation $operation for Monkey $monkeyIndex")
        }
    }

    private val monkeyList: MutableList<Monkey> = mutableListOf()
    private val commonDivisor: Long by lazy {
        monkeyList.map { it.divisorPredicateOperand.toLong() }.reduce(Long::times)
    }

    val worryLevelDivisor: WorryLevelOperation = { old -> old / 3 }
    val worryLevelModulus: WorryLevelOperation = { old -> old % commonDivisor }

    fun calculateMonkeyBusiness(rounds: Int, worryLevelOperation: WorryLevelOperation): Long {

        monkeyList.forEach(Monkey::initialize)
        repeat(rounds) {

            for (monkey in monkeyList) {

                while (monkey.worryItems.isNotEmpty()) {

                    val item = monkey.worryItems.removeFirst()
                    monkey.inspections++

                    val worryLevel = worryLevelOperation(monkey.applyMonkeyOperation(item))
                    if (worryLevel % monkey.divisorPredicateOperand == 0L) {
                        monkeyList[monkey.trueTargetMonkey].worryItems += worryLevel
                    } else {
                        monkeyList[monkey.falseTargetMoney].worryItems += worryLevel
                    }
                }
            }
        }

        return monkeyList
            .map { it.inspections }
            .sortedDescending()
            .take(2)
            .reduce(Long::times)
    }

    companion object {


        private val MONKEY_INPUT_SEPARATOR: String = System.lineSeparator() + System.lineSeparator()

        private fun String.toMonkey(monkeyIndex: Int): Monkey {

            val (startingItemsLine,
                operationLine,
                testPredicateLine,
                trueTargetLine,
                falseTargetLine) = lines().drop(1)

            val items = startingItemsLine.substringAfter(":")
                .split(",").map { it.trim().toLong() }
            val parsedOperation = operationLine.substringAfter("=").trim()
                .substringAfter("old ").trim()
            val (operation, rightOperand) = parsedOperation.split(" ")
            val testOperand = testPredicateLine.substringAfter("by ").trim().toInt()
            val trueTargetMonkey = trueTargetLine.substringAfterLast(" ").trim().toInt()
            val falseTargetMonkey = falseTargetLine.substringAfterLast(" ").trim().toInt()

            return Monkey(
                monkeyIndex,
                items,
                operation,
                rightOperand,
                testOperand,
                trueTargetMonkey,
                falseTargetMonkey
            )
        }

        fun load(input: String): MonkeyProblem {

            val monkeyProblem = MonkeyProblem()
            for ((index, monkeyGroupInput) in input.split(MONKEY_INPUT_SEPARATOR).withIndex()) {
                monkeyProblem.monkeyList += monkeyGroupInput.toMonkey(index)
            }
            return monkeyProblem
        }
    }

}


fun main() {

    val testInputString = readInputAsText("Day11_test")
    val testMonkeyProblem = MonkeyProblem.load(testInputString)
    check(testMonkeyProblem.calculateMonkeyBusiness(20, testMonkeyProblem.worryLevelDivisor) == 10605L)
    check(testMonkeyProblem.calculateMonkeyBusiness(10_000, testMonkeyProblem.worryLevelModulus) == 2713310158L)

    val actualInputString = readInputAsText("Day11")
    val actualMonkeyProblem = MonkeyProblem.load(actualInputString)
    println(actualMonkeyProblem.calculateMonkeyBusiness(20, actualMonkeyProblem.worryLevelDivisor))
    println(actualMonkeyProblem.calculateMonkeyBusiness(10_000, actualMonkeyProblem.worryLevelModulus))
}