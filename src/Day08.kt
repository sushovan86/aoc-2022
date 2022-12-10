class Forest(private val treesHeight: Array<Array<Int>>) {

    private val rowCount = treesHeight.size
    private val columnCount = treesHeight[0].size

    fun getVisibleTreeCount(): Int {

        // No need to consider trees in the outer edges
        val leftInnerIndex = 1
        val rightInnerIndex = columnCount - 2
        val bottomInnerIndex = rowCount - 2

        var innerVisibleCount = 0
        for (row in leftInnerIndex..bottomInnerIndex) {
            for (column in leftInnerIndex..rightInnerIndex) {
                if (isVisible(row, column)) innerVisibleCount++
            }
        }

        val outerVisibleTreeCount = (2 * (rowCount + columnCount) - 4)
        return outerVisibleTreeCount + innerVisibleCount
    }

    fun getMaxScenicScore(): Int {

        // No need to consider trees in the outer edges
        val leftInnerIndex = 1
        val rightInnerIndex = columnCount - 2
        val bottomInnerIndex = rowCount - 2

        var maxScenicScore = 0
        for (row in leftInnerIndex..bottomInnerIndex) {
            for (column in leftInnerIndex..rightInnerIndex) {
                val scenicScore = getScenicScore(row, column)
                if (scenicScore > maxScenicScore) maxScenicScore = scenicScore
            }
        }

        return maxScenicScore
    }

    private inline fun IntProgression.scenicScore(
        currentTreeHeight: Int,
        coordinateAt: (Int) -> Pair<Int, Int>
    ): Int {

        var offset = 0
        for (index in this) {

            offset++

            val (row, column) = coordinateAt(index)
            val treeHeightAtIndex = treesHeight[row][column]
            if (treeHeightAtIndex >= currentTreeHeight) {
                break
            }
        }
        return offset
    }

    private inline fun IntProgression.isVisible(
        currentTreeHeight: Int,
        coordinateAt: (Int) -> Pair<Int, Int>
    ): Boolean =
        this.any {
            val (row, column) = coordinateAt(it)
            treesHeight[row][column] >= currentTreeHeight
        }.not()


    private fun getScenicScore(row: Int, column: Int): Int {

        val currentTreeHeight = treesHeight[row][column]

        val left = column - 1 downTo 0
        val right = column + 1 until columnCount
        val top = row - 1 downTo 0
        val bottom = row + 1 until rowCount

        val leftScenicScore = left.scenicScore(currentTreeHeight) { row to it }
        val rightScenicScore = right.scenicScore(currentTreeHeight) { row to it }
        val topScenicScore = top.scenicScore(currentTreeHeight) { it to column }
        val bottomScenicScore = bottom.scenicScore(currentTreeHeight) { it to column }

        return leftScenicScore * rightScenicScore * topScenicScore * bottomScenicScore
    }

    private fun isVisible(row: Int, column: Int): Boolean = sequence {

        val currentTreeHeight = treesHeight[row][column]

        val left = column - 1 downTo 0
        val right = column + 1 until columnCount
        val top = row - 1 downTo 0
        val bottom = row + 1 until rowCount

        yield(left.isVisible(currentTreeHeight) { row to it })
        yield(right.isVisible(currentTreeHeight) { row to it })
        yield(top.isVisible(currentTreeHeight) { it to column })
        yield(bottom.isVisible(currentTreeHeight) { it to column })

    }.any { it }

    companion object {
        fun loadData(lines: List<String>): Forest {

            val matrix = Array(lines.size) { Array(lines[0].length) { 0 } }
            for ((row, line) in lines.withIndex()) {
                for ((column, field) in line.withIndex()) {
                    matrix[row][column] = field.digitToInt()
                }
            }
            return Forest(matrix)
        }
    }

    override fun toString(): String {

        val sb = StringBuilder()
        for (row in treesHeight) {
            for (columnValue in row) {
                sb.append("$columnValue ")
            }
            sb.append(System.lineSeparator())
        }
        return sb.toString()
    }
}

fun main() {

    val testInput = readInput("Day08_test")
    val forrestTest = Forest.loadData(testInput)
    check(forrestTest.getVisibleTreeCount() == 21)
    check(forrestTest.getMaxScenicScore() == 8)

    val actualInput = readInput("Day08")
    val forrest = Forest.loadData(actualInput)
    println(forrest.getVisibleTreeCount())
    println(forrest.getMaxScenicScore())
}