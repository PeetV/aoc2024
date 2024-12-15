fun main() {

    fun processFile(input: List<String>): Pair<CharacterGrid, String> {
        val blankLine = input.indexOfFirst { it.isBlank() }
        val grid = CharacterGrid(input.slice(0..<blankLine))
        val moves = input.slice((blankLine + 1)..input.lastIndex).joinToString("")
        return grid to moves
    }

    fun part1(input: List<String>): Int {
        val (grid, moves) = processFile(input)
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day15_test1.txt` file
    var testInput = readInput("Day15_test1")
    part1(testInput).println()
//    check(part1(testInput) == 1)

    // Test input from the `src/Day15_test2.txt` file
    testInput = readInput("Day15_test2")
//    check(part1(testInput) == 1)

    // Input from the `src/Day15.txt` file
//    val input = readInput("Day15")
//    part1(input).println()
//    part2(input).println()

}
