fun main() {

    fun String.itemsAsRange(): IntRange {
        val (start, end) = this.split("-")
        return (start.toInt()..end.toInt())
    }

    fun part1(lines: List<String>): Int = lines.count { line ->
        val (firstElf, secondElf) = line.split(",")
        (firstElf.itemsAsRange() subtract secondElf.itemsAsRange()).isEmpty()
                || (secondElf.itemsAsRange() subtract firstElf.itemsAsRange()).isEmpty()
    }

    fun part2(lines: List<String>): Int = lines.count { line ->
        val (firstElf, secondElf) = line.split(",")
        (firstElf.itemsAsRange() intersect secondElf.itemsAsRange()).isNotEmpty()
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val actualInput = readInput("Day04")
    println(part1(actualInput))
    println(part2(actualInput))
}