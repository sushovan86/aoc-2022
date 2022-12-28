class HillClimbing private constructor(
    private val areaElevationMap: MutableMap<Coordinate, Int>,
    private val startCoordinate: Coordinate,
    private val endCoordinate: Coordinate,
    private val width: Int,
    private val height: Int
) {

    data class Coordinate(val x: Int = 0, val y: Int = 0)

    private val visited = mutableSetOf<Coordinate>()
    private val queue = ArrayDeque<Coordinate>()
    private val parentCoordinateMap = mutableMapOf<Coordinate, Coordinate>()

    private fun initialize(start: Coordinate) {
        visited.clear()
        parentCoordinateMap.clear()
        queue.clear()

        queue += start
        visited += start
    }

    private fun getAdjacentCoordinates(coordinate: Coordinate) = buildList(4) {

        if (coordinate.y > 0) {
            add(coordinate.copy(y = coordinate.y - 1)) // UP
        }
        if (coordinate.y < height - 1) {
            add(coordinate.copy(y = coordinate.y + 1)) // DOWN
        }
        if (coordinate.x > 0) {
            add(coordinate.copy(x = coordinate.x - 1)) // LEFT
        }
        if (coordinate.x < width - 1) {
            add(coordinate.copy(x = coordinate.x + 1)) // RIGHT
        }
    }

    companion object {

        fun load(inputList: List<String>): HillClimbing {

            val yEnd = inputList.size
            val xEnd = inputList[0].length

            val areaElevationMap = mutableMapOf<Coordinate, Int>()
            var startCoordinate = Coordinate()
            var endCoordinate = Coordinate()

            for ((yCoordinate, inputLine) in inputList.withIndex()) {
                for ((xCoordinate, elevation) in inputLine.withIndex()) {

                    when (elevation) {
                        'S' -> {
                            startCoordinate = Coordinate(xCoordinate, yCoordinate)
                            areaElevationMap[startCoordinate] = 0
                        }

                        'E' -> {
                            endCoordinate = Coordinate(xCoordinate, yCoordinate)
                            areaElevationMap[endCoordinate] = 'z' - 'a'
                        }

                        else -> areaElevationMap[Coordinate(xCoordinate, yCoordinate)] = elevation - 'a'
                    }
                }
            }
            return HillClimbing(areaElevationMap, startCoordinate, endCoordinate, xEnd, yEnd)
        }
    }

    fun findShortestPath(start: Coordinate = startCoordinate): Int {

        initialize(start)

        while (queue.isNotEmpty()) {

            val currentCoordinate = queue.removeFirst()

            if (currentCoordinate == endCoordinate) {
                return findDistanceToStart(currentCoordinate, start)
            }

            val currentElevation = areaElevationMap.getValue(currentCoordinate)

            getAdjacentCoordinates(currentCoordinate)
                .filter {
                    (it !in visited) &&
                            (areaElevationMap.getValue(it) - currentElevation <= 1)
                }
                .forEach {
                    queue += it
                    visited += it
                    parentCoordinateMap[it] = currentCoordinate
                }
        }
        return Int.MAX_VALUE
    }

    private fun findDistanceToStart(currentCoordinate: Coordinate, start: Coordinate): Int {

        var temp: Coordinate? = currentCoordinate
        var distance = 0
        while (temp != start) {
            temp = parentCoordinateMap[temp]
            distance++
        }
        return distance
    }

    fun findShortestPathForLowestStart() = areaElevationMap
        .filter { it.value == 0 }
        .map { findShortestPath(it.key) }
        .min()

}

fun main() {

    val testInput = readInput("Day12_test")
    val testHillClimbing = HillClimbing.load(testInput)
    check(testHillClimbing.findShortestPath() == 31)
    check(testHillClimbing.findShortestPathForLowestStart() == 29)

    val actualInput = readInput("Day12")
    val actualHillClimbing = HillClimbing.load(actualInput)
    println(actualHillClimbing.findShortestPath())
    println(actualHillClimbing.findShortestPathForLowestStart())
}