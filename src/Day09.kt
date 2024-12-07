fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day09_test.txt` file
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1)

    // Input from the `src/Day09.txt` file
    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()

}
