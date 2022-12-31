import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class BeaconExclusionZone private constructor(
    private val sensorBeaconDistances: List<SensorBeaconDistance>
) {

    fun getNoBeaconPositionCountAtRow(rowNum: Int): Int = getCoverageRangePairsAtRow(rowNum)
        .let { rangePairsAtRow ->
            val beaconsAtRow = sensorBeaconDistances
                .filter { it.beaconCoordinate.y == rowNum }
                .distinctBy { it.beaconCoordinate.x }
                .count()
            val extremeLeftPointAtRow = rangePairsAtRow.minOf { it.first }
            val extremeRightPointAtRow = rangePairsAtRow.maxOf { it.second }
            (extremeRightPointAtRow - extremeLeftPointAtRow + 1) - beaconsAtRow
        }

    fun getTuningFrequency(maxRow: Int): Long =
        (0..maxRow)
            .firstNotNullOfOrNull { getProbableBeaconPositionForRow(it) }
            ?.let { it.x * MAX_COORDINATE_SIZE + it.y } ?: error("No position found")

    private fun getProbableBeaconPositionForRow(row: Int): Coordinate? {

        val ranges = getCoverageRangePairsAtRow(row)
            .sortedWith(compareBy({ it.first }, { it.second }))
        if (ranges.isEmpty()) {
            return Coordinate(row, 0)
        }

        var temp = ranges[0]
        for (index in 1 until ranges.size) {

            if (ranges[index].first - temp.second <= 1) {
                temp = min(temp.first, ranges[index].first) to max(temp.second, ranges[index].second)
            }
            else {
                return Coordinate(temp.second + 1, row)
            }
        }
        return null
    }

    private fun getCoverageRangePairsAtRow(rowNum: Int) = sensorBeaconDistances
        .mapNotNull { (sensor, _, distance) ->

            val offset = abs(sensor.y - rowNum)
            val startRange = sensor.x - distance + offset
            val endRange = sensor.x + distance - offset

            if (endRange >= startRange) startRange to endRange else null
        }

    data class SensorBeaconDistance(
        val sensorCoordinate: Coordinate,
        val beaconCoordinate: Coordinate,
        val distance: Int
    )

    data class Coordinate(val x: Int = 0, val y: Int = 0) {

        infix fun distanceTo(other: Coordinate): Int = abs(other.x - x) + abs(other.y - y)
    }

    companion object {

        const val MAX_COORDINATE_SIZE = 4_000_000L

        fun load(inputList: List<String>): BeaconExclusionZone = inputList
            .map {
                val (sensorString, beaconString) = it.split(":")
                val sensorCoordinate = extractCoordinate(sensorString)
                val beaconCoordinate = extractCoordinate(beaconString)
                val distance = sensorCoordinate distanceTo beaconCoordinate
                SensorBeaconDistance(sensorCoordinate, beaconCoordinate, distance)
            }
            .let(::BeaconExclusionZone)

        private fun extractCoordinate(string: String): Coordinate {
            val x = string.substringAfter("x=").substringBefore(",").toInt()
            val y = string.substringAfter("y=").toInt()
            return Coordinate(x, y)
        }
    }
}

fun main() {

    val testInput = readInput("Day15_test")
    val testBeaconExclusionZone = BeaconExclusionZone.load(testInput)
    check(testBeaconExclusionZone.getNoBeaconPositionCountAtRow(10) == 26)
    check(testBeaconExclusionZone.getTuningFrequency(20) == 56000011L)

    val input = readInput("Day15")
    val beaconExclusionZone = BeaconExclusionZone.load(input)
    println(beaconExclusionZone.getNoBeaconPositionCountAtRow(2_000_000))
    println(beaconExclusionZone.getTuningFrequency(4_000_000))
}