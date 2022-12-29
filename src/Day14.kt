import kotlin.math.abs
import kotlin.math.sign

class RegolithReservoir private constructor(private val rocksCoordinates: Set<Coordinate>) {

    data class Coordinate(val x: Int = 0, val y: Int = 0) {

        infix fun lineTo(other: Coordinate): List<Coordinate> {

            val dx = other.x - x
            val dy = other.y - y

            val coordinateList = mutableListOf(this)

            // Add In between coordinates
            (1 until maxOf(abs(dx), abs(dy))).map {
                coordinateList += Coordinate(x + it * dx.sign, y + it * dy.sign)
            }
            coordinateList += other
            return coordinateList
        }
    }

    private val initialSandCoordinate = Coordinate(500, 0)
    private val restingSandCoordinates = mutableSetOf<Coordinate>()

    private val depth: Int = rocksCoordinates.maxOf { it.y }

    private fun Coordinate.nextSandCoordinate() = sequence {
        yield(copy(y = y + 1))
        yield(copy(x = x - 1, y = y + 1))
        yield(copy(x = x + 1, y = y + 1))

    }.firstOrNull { it !in rocksCoordinates && it !in restingSandCoordinates }

    companion object {

        fun load(inputList: List<String>): RegolithReservoir {

            val rocksCoordinates = inputList.flatMap { line ->

                line.split("->")
                    .map {
                        val (first, second) = it.split(",")
                        Coordinate(first.trim().toInt(), second.trim().toInt())
                    }
                    .zipWithNext()
                    .flatMap { (start, end) -> start lineTo end }

            }.toSet()
            return RegolithReservoir(rocksCoordinates)
        }
    }

    fun getUnitsOfSandInRest(): Int {

        restingSandCoordinates.clear()

        var currentSandCoordinate = initialSandCoordinate
        while (currentSandCoordinate.y < depth) {

            val nextSandCoordinate = currentSandCoordinate.nextSandCoordinate()
            if (nextSandCoordinate != null) {
                currentSandCoordinate = nextSandCoordinate
            } else {
                restingSandCoordinates += currentSandCoordinate
                currentSandCoordinate = initialSandCoordinate
            }
        }
        return restingSandCoordinates.size
    }

    fun getUnitsOfSandToBlockSource(): Int {

        restingSandCoordinates.clear()
        val floor = depth + 2

        var currentSandCoordinate = initialSandCoordinate
        while (initialSandCoordinate !in restingSandCoordinates) {

            val nextSandCoordinate = currentSandCoordinate.nextSandCoordinate()
            if (nextSandCoordinate == null || nextSandCoordinate.y == floor) {
                restingSandCoordinates += currentSandCoordinate
                currentSandCoordinate = initialSandCoordinate
            } else {
                currentSandCoordinate = nextSandCoordinate
            }
        }
        return restingSandCoordinates.size
    }
}

fun main() {

    val testInput = readInput("Day14_test")
    val testRegolithReservoir = RegolithReservoir.load(testInput)
    check(testRegolithReservoir.getUnitsOfSandInRest() == 24)
    check((testRegolithReservoir.getUnitsOfSandToBlockSource() == 93))

    val actualInput = readInput("Day14")
    val regolithReservoir = RegolithReservoir.load(actualInput)
    println(regolithReservoir.getUnitsOfSandInRest())
    println(regolithReservoir.getUnitsOfSandToBlockSource())
}