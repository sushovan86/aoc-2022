import java.io.File
import java.util.*

val EMPTY_LINE_SEPARATOR = System.lineSeparator().repeat(2)

fun main() {

    fun String.splitByEmptyLine() = this.splitToSequence(EMPTY_LINE_SEPARATOR)

    fun topElvesCalories(numberOfElves: Int, input: String): List<Int> {

        val priorityQueue = PriorityQueue<Int>()

        input.splitByEmptyLine()
            .map {
                it.lines().sumOf(String::toInt)
            }
            .forEach {

                priorityQueue.add(it)
                if (priorityQueue.size > numberOfElves) {
                    priorityQueue.poll()
                }
            }

        return priorityQueue.toList()
    }

    fun part1(input: String): Int {
        return topElvesCalories(1, input)[0]
    }

    fun part2(input: String): Int {
        return topElvesCalories(3, input).sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = File("resource/Day01_test").readText()
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = File("resource/Day01").readText()
    println(part1(input))
    println(part2(input))
}
