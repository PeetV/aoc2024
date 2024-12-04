fun main() {

    fun countMatches(input: String): Int {
        var result = 0
        for (index in 0..(input.count() - 4)) {
            val word = input.slice(index..<(index + 4))
            if (word == "XMAS" || word == "SAMX") {
                result += 1
            }
        }
        return result
    }

    fun extractColumns(input: List<String>): List<String> {
        val numberOfRows = input.count()
        val numberOfColumns = input.first().count()
        return (0..<numberOfColumns).map { columnIndex ->
            val column = mutableListOf<Char>()
            (0..<numberOfRows).map { rowIndex ->
                column.add(input[rowIndex][columnIndex])
            }
            column.joinToString("")
        }
    }

    fun extractRightUpDiagonals(input: List<String>): List<String> {
        val result = mutableListOf<String>()
        val numberOfRows = input.count()
        val numberOfColumns = input.first().count()
        for (columnIndex in 0..<(numberOfColumns - 3)) {
            for (rowIndex in 3..<numberOfRows) {
                val diagonal = mutableListOf<Char>()
                (rowIndex downTo (rowIndex - 5)).zip(columnIndex..<(columnIndex + 4)).map { coordinate ->
                    diagonal.add(input[coordinate.first][coordinate.second])
                }
                result.add(diagonal.joinToString(""))
            }
        }
        return result
    }

    fun extractLeftUpDiagonals(input: List<String>): List<String> {
        val result = mutableListOf<String>()
        val numberOfRows = input.count()
        val numberOfColumns = input.first().count()
        for (columnIndex in 0..<(numberOfColumns - 3)) {
            for (rowIndex in 0..<(numberOfRows - 3)) {
                val diagonal = mutableListOf<Char>()
                (rowIndex..(rowIndex + 5)).zip(columnIndex..<(columnIndex + 4)).map { coordinate ->
                    diagonal.add(input[coordinate.first][coordinate.second])
                }
                result.add(diagonal.joinToString(""))
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val rowsCount = input.sumOf { countMatches(it) }
        val columns = extractColumns(input)
        val columnsCount = columns.sumOf { countMatches(it) }
        val rightUpDiagonals = extractRightUpDiagonals(input)
        val rightUpDiagonalsCount = rightUpDiagonals.sumOf { countMatches(it) }
        val leftUpDiagonals = extractLeftUpDiagonals(input)
        val leftUpDiagonalsCount = leftUpDiagonals.sumOf { countMatches(it) }
        return rowsCount + columnsCount + rightUpDiagonalsCount + leftUpDiagonalsCount
    }

    fun part2(input: List<String>): Int {
        val numberOfRows = input.count()
        val numberOfColumns = input.first().count()
        var result = 0
        for (rowIndex in 1..<(numberOfRows - 1)) {
            for (columnIndex in 1..<(numberOfColumns - 1)) {
                if (input[rowIndex][columnIndex] != 'A') continue
                val topLeft = input[rowIndex - 1][columnIndex - 1]
                val topRight = input[rowIndex - 1][columnIndex + 1]
                val bottomLeft = input[rowIndex + 1][columnIndex - 1]
                val bottomRight = input[rowIndex + 1][columnIndex + 1]
                val shape = listOf(topLeft, bottomRight, topRight, bottomLeft)
                when (shape) {
                    listOf('M', 'S', 'M', 'S') -> result += 1
                    listOf('S', 'M', 'S', 'M') -> result += 1
                    listOf('M', 'S', 'S', 'M') -> result += 1
                    listOf('S', 'M', 'M', 'S') -> result += 1
                }
            }
        }
        return result
    }

    // Test input from the `src/Day04_test.txt` file
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    // Input from the `src/Day04.txt` file
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()

}
