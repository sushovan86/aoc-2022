import kotlin.math.abs

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

val DIRECTION_MAP = mapOf(
    "R" to Direction.RIGHT,
    "L" to Direction.LEFT,
    "U" to Direction.UP,
    "D" to Direction.DOWN
)

data class Point(val x: Int = 0, val y: Int = 0) {

    infix fun moveTowards(movement: Direction): Point = when (movement) {
        Direction.LEFT -> copy(x = x - 1)
        Direction.RIGHT -> copy(x = x + 1)
        Direction.UP -> copy(y = y + 1)
        Direction.DOWN -> copy(y = y - 1)
    }

    infix fun isNeighbourTo(other: Point): Boolean =
        abs(x - other.x) <= 1 && abs(y - other.y) <= 1

    infix fun follow(other: Point): Point {

        val xOffset = (other.x - x).coerceIn(-1..1)
        val yOffset = (other.y - y).coerceIn(-1..1)

        return Point(x + xOffset, y + yOffset)
    }
}

class RopeBridge private constructor() {

    private val instructions = mutableListOf<Pair<Direction, Int>>()
    private val tailPositions = mutableSetOf<Point>()

    companion object {

        fun loadData(instructionStringList: List<String>): RopeBridge {

            val ropeBridge = RopeBridge()

            for (eachInstructionString in instructionStringList) {
                val (movement, value) = eachInstructionString.split(" ")
                val direction = DIRECTION_MAP[movement] ?: error("Invalid Movement $movement")
                ropeBridge.instructions += direction to value.toInt()
            }
            return ropeBridge
        }
    }

    fun getTailPositionCount() = tailPositions.size

    fun processInstructions(knotSize: Int): RopeBridge {

        tailPositions.clear()

        val ropeKnots = Array(knotSize) { Point() }
        tailPositions += ropeKnots[knotSize - 1]

        for ((direction, value) in instructions) {

            repeat(value) {
                processKnotsDirection(ropeKnots, direction, knotSize)
            }
        }
        return this
    }

    private fun processKnotsDirection(ropeKnots: Array<Point>, direction: Direction, knotSize: Int) {

        // First index in the array is head, last index is tail
        ropeKnots[0] = ropeKnots[0] moveTowards direction

        for (knotIndex in 1 until knotSize) {

            val previousKnot = ropeKnots[knotIndex - 1]
            var currentKnot = ropeKnots[knotIndex]

            if (currentKnot isNeighbourTo previousKnot) {
                break
            }

            currentKnot = currentKnot follow previousKnot
            ropeKnots[knotIndex] = currentKnot

            if (knotIndex == ropeKnots.lastIndex) {
                tailPositions += currentKnot
            }
        }
    }
}

fun main() {

    val testInput1 = readInput("Day09_test1")
    val testRopeBridge1 = RopeBridge.loadData(testInput1)
    testRopeBridge1.processInstructions(2)
    check(testRopeBridge1.getTailPositionCount() == 13)

    val testInput2 = readInput("Day09_test2")
    val testRopeBridge2 = RopeBridge.loadData(testInput2)
    testRopeBridge2.processInstructions(10)
    check(testRopeBridge2.getTailPositionCount() == 36)

    val actualInput = readInput("Day09")
    val actualRopeBridge = RopeBridge.loadData(actualInput)

    actualRopeBridge.processInstructions(2)
    println(actualRopeBridge.getTailPositionCount())

    actualRopeBridge.processInstructions(10)
    println(actualRopeBridge.getTailPositionCount())
}