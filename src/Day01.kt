fun main() {

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day01_test.txt` file
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 3)

    // Input from the `src/Day01.txt` file
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
