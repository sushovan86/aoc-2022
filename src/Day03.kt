fun main() {

    fun Char.priority(): Int = when (this) {
        in 'a'..'z' -> this - 'a' + 1
        in 'A'..'Z' -> this - 'A' + 27
        else -> 0
    }

    infix fun String.commonItemsIn(other: String) = if (this.isBlank() || other.isBlank()) {
        ""
    } else {
        (this.toSet() intersect other.toSet())
            .joinToString()
    }

    fun part1(lines: List<String>): Int = lines
        .sumOf { items: String ->
            val (firstPart, secondPart) = items.chunked(items.length / 2)
            val commonItems = firstPart commonItemsIn secondPart
            commonItems.firstOrNull()?.priority() ?: 0
        }

    fun part2(lines: List<String>): Int = lines
        .chunked(3)
        .sumOf { itemsOf3Elves: List<String> ->
            val (elf1Items, elf2Items, elf3Items) = itemsOf3Elves
            val commonItems = elf1Items commonItemsIn elf2Items commonItemsIn elf3Items
            commonItems.firstOrNull()?.priority() ?: 0

        }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val actualInput = readInput("Day03")
    println(part1(actualInput))
    println(part2(actualInput))
}

