import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.set

typealias StackElement = ArrayDeque<Char>

fun StackElement.removeLast(n: Int): List<Char> {

    val list = ArrayList<Char>()
    repeat(n) {
        list += removeLast()
    }
    return list
}

fun StackElement.addLast(element: List<Char>, maintainOrder: Boolean = false) {
    if (maintainOrder) {
        element.reversed().forEach { addLast(it) }
    } else {
        element.forEach { addLast(it) }
    }
}

class Instruction(val moveCount: Int, val fromStack: Int, val toStack: Int)
typealias Instructions = List<Instruction>

class WorkingStack {

    private val stackElementByStackNumberMap = TreeMap<Int, StackElement>()
    private val stackElementByPositionMap = mutableMapOf<Int, StackElement>()

    fun initializeStackWithCharacterPosition(stackNumber: Int, position: Int) {
        val stackElement = StackElement();
        stackElementByStackNumberMap[stackNumber] = stackElement
        stackElementByPositionMap[position] = stackElement
    }

    fun addToStack(position: Int, stackValue: Char) {
        stackElementByPositionMap[position]?.addLast(stackValue)
    }

    fun executeInstructions(instructions: Instructions, maintainOrder: Boolean = false) {

        for (instruction in instructions) {

            val movedElements = stackElementByStackNumberMap[instruction.fromStack]
                ?.removeLast(instruction.moveCount)

            movedElements?.apply {
                stackElementByStackNumberMap[instruction.toStack]?.addLast(this, maintainOrder)
            }
        }
    }

    fun topStackElements() = stackElementByStackNumberMap
        .map { it.value.last() }
        .joinToString(separator = "")

    override fun toString(): String {

        val sb = StringBuilder();
        stackElementByStackNumberMap.forEach {
            sb.append("${it.key} --> ${it.value}")
            sb.append(System.lineSeparator())
        }

        return sb.toString()
    }
}


fun main() {

    val (workingStackTest1, instructionsTest1) = parseInput("Day05_test")
    workingStackTest1.executeInstructions(instructionsTest1)
    check(workingStackTest1.topStackElements() == "CMZ")

    val (workingStackTest2, instructionsTest2) = parseInput("Day05_test")
    workingStackTest2.executeInstructions(instructionsTest2, maintainOrder = true)
    check(workingStackTest2.topStackElements() == "MCD")

    val (workingStackActual1, instructionsActual1) = parseInput("Day05")
    workingStackActual1.executeInstructions(instructionsActual1)
    println(workingStackActual1.topStackElements())

    val (workingStackActual2, instructionsActual2) = parseInput("Day05")
    workingStackActual2.executeInstructions(instructionsActual2, maintainOrder = true)
    println(workingStackActual2.topStackElements())
}

private fun parseInput(fileName: String): Pair<WorkingStack, Instructions> {

    val testInput = File("resource", fileName).readText()

    val (workingStackData, instructionData) = testInput.split(System.lineSeparator().repeat(2))
    val workingStack = parseWorkingStackData(workingStackData)
    val instructions = parseInstructions(instructionData)

    return workingStack to instructions
}

fun parseInstructions(instructionData: String): Instructions = instructionData
    .lines()
    .map {
        val (count, fromStack, toStack) = it.split(" ", "move", "from", "to")
            .filterNot(String::isBlank)
        Instruction(count.toInt(), fromStack.toInt(), toStack.toInt())
    }


fun parseWorkingStackData(workingStackData: String): WorkingStack {

    val workingStack = WorkingStack();

    val lines = workingStackData.lines()
    lines.last()
        .withIndex()
        .filter { it.value.isDigit() }
        .forEach {
            workingStack.initializeStackWithCharacterPosition(it.value.digitToInt(), it.index)
        }

    lines.reversed()
        .drop(1)
        .forEach { line ->
            line.withIndex()
                .filter { it.value.isLetter() }
                .forEach { workingStack.addToStack(it.index, it.value) }
        }

    return workingStack
}


