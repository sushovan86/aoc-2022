import java.io.File

fun main() {

    fun findFirstOccurrence(windowSize: Int, input: String) = input
        .windowedSequence(windowSize)
        .indexOfFirst { it.toSet().size == windowSize } + windowSize

    val testInput = File("resource", "Day06_test").readText()
    check(findFirstOccurrence(4, testInput) == 11)
    check(findFirstOccurrence(14, testInput) == 26)

    val actualInput = File("resource", "Day06").readText()
    println(findFirstOccurrence(4, actualInput))
    println(findFirstOccurrence(14, actualInput))
}