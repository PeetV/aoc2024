fun main() {

    fun getBlanks(input: List<String>): List<List<String>> {
        val blanks = mutableListOf<MutableList<String>>()
        var workingBlank = mutableListOf<String>()
        for ((lineIndex, line) in input.withIndex()) {
            if (line.isNotEmpty()) {
                workingBlank.add(line)
            }
            if (line.isEmpty() || lineIndex == input.lastIndex) {
                blanks.add(workingBlank)
                workingBlank = mutableListOf()
            }
        }
        return blanks
    }

    fun countPins(blank: List<String>): List<Int> {
        val grid = CharacterGrid(blank.slice(0..<blank.lastIndex))
        return grid.columnStrings.toList().map { it.count { char -> char == '#' } }
    }

    fun part1(input: List<String>): Int {
        val blanks = getBlanks(input)
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()
        for (blank in blanks) {
            if (blank.first() == "#####") {
                locks.add(countPins(blank.reversed()))
            } else {
                keys.add(countPins(blank))
            }
        }
        var result = 0
        for (key in keys) {
            for (lock in locks) {
                val keyFits = lock.zip(key).all { (a, b) -> (a + b) <= 5 }
                if (keyFits) result += 1
            }
        }
        return result
    }

    // Test input from the `src/Day20_test.txt` file
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 3)

    // Input from the `src/Day20.txt` file
    val input = readInput("Day25")
    part1(input).println()

}
