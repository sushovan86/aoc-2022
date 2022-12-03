interface CalculateScore {
    fun selfMoveScore(): Int
    fun outcome(): Int
    fun totalScore(): Int = selfMoveScore() + outcome()
}

data class Round(
    val opponentMove: String,
    val selfMove: String
)

class Strategy1Score(private val round: Round) : CalculateScore {
    override fun selfMoveScore(): Int = when (round.selfMove) {
        "X" -> 1
        "Y" -> 2
        "Z" -> 3
        else -> error("Invalid self move ${round.selfMove}")
    }

    override fun outcome(): Int = (round.opponentMove + round.selfMove).let { move ->

        when (move) {
            "AX", "BY", "CZ" -> 3
            "BX", "CY", "AZ" -> 0
            "CX", "AY", "BZ" -> 6
            else -> error("Invalid combination of ${round.opponentMove} and ${round.selfMove}")
        }
    }
}

class Strategy2Score(private val round: Round) : CalculateScore {
    override fun selfMoveScore(): Int = (round.opponentMove + round.selfMove).let { move ->

        when (move) {
            "AY", "BX", "CZ" -> 1
            "BY", "CX", "AZ" -> 2
            "CY", "AX", "BZ" -> 3
            else -> error("Invalid combination of ${round.opponentMove} and ${round.selfMove}")
        }
    }

    override fun outcome(): Int = when (round.selfMove) {
        "X" -> 0
        "Y" -> 3
        "Z" -> 6
        else -> error("Invalid self move ${round.selfMove}")
    }
}

fun String.toRound() = this.split(" ")
    .let {
        Round(it[0], it[1])
    }

fun main() {

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val actualInput = readInput("Day02")
    println(part1(actualInput))
    println(part2(actualInput))
}

fun part2(lines: List<String>): Int = lines
    .map(String::toRound)
    .map(::Strategy2Score)
    .sumOf(Strategy2Score::totalScore)

fun part1(lines: List<String>): Int = lines
    .map(String::toRound)
    .map(::Strategy1Score)
    .sumOf(Strategy1Score::totalScore)
