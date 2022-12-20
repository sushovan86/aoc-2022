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
        Direction.UP -> copy(y = y - 1)
        Direction.DOWN -> copy(y = y + 1)
    }

    private infix fun isNeighbourTo(other: Point): Boolean =
        abs(x - other.x) <= 1 && abs(y - other.y) <= 1

    infix fun follow(other: Point): Point = if (this isNeighbourTo other) {
        this
    } else {

        val xOffset = (other.x - x).coerceIn(-1..1)
        val yOffset = (other.y - y).coerceIn(-1..1)

        Point(x + xOffset, y + yOffset)
    }
}

class RopeBridge(private val instructions: List<String>) {

    private var head: Point = Point()
    private var tail: Point = Point()

    private val tailPositions = mutableSetOf<Point>()

    private fun processEachMovement(movement: String, value: Int) {

        val direction = DIRECTION_MAP[movement] ?: error("Invalid Movement $movement")

        repeat(value) {
            head = head moveTowards direction
            tail = tail follow head
            tailPositions += tail
        }
    }

    fun getTailPositionCount() = tailPositions.size

    fun processInstructions() = instructions
        .map { it.split(" ") }
        .forEach { (movement, value) -> processEachMovement(movement, value.toInt()) }
        .let {
            this
        }
}

fun main() {

    val testInput = readInput("Day09_test")

    val testRopeBridge = RopeBridge(testInput).processInstructions()
    check(testRopeBridge.getTailPositionCount() == 13)

    val actualInput = readInput("Day09")
    val actualRopeBridge = RopeBridge(actualInput).processInstructions()
    println(actualRopeBridge.getTailPositionCount())

}