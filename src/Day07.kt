fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day07_test.txt` file
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 1)

    // Input from the `src/Day07.txt` file
    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()

}
