fun main() {

    fun String.itemsAsRange(): IntRange {
        val (start, end) = split("-")
        return start.toInt()..end.toInt()
    }

    fun String.splitByComma() = split(",")

    operator fun IntRange.contains(other: IntRange): Boolean =
        first <= other.first && last >= other.last

    infix fun IntRange.containsSome(other: IntRange): Boolean =
        last >= other.first && first <= other.last

    fun part1(lines: List<String>): Int = lines
        .map(String::splitByComma)
        .count { (firstElf, secondElf) ->
            firstElf.itemsAsRange() in secondElf.itemsAsRange()
                    || secondElf.itemsAsRange() in firstElf.itemsAsRange()
        }

    fun part2(lines: List<String>): Int = lines
        .map(String::splitByComma)
        .count { (firstElf, secondElf) ->
            firstElf.itemsAsRange() containsSome secondElf.itemsAsRange()
        }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val actualInput = readInput("Day04")
    println(part1(actualInput))
    println(part2(actualInput))
}