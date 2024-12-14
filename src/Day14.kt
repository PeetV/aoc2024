fun main() {

    class Robot(var location: Pair<Int, Int>, velocity: Pair<Int, Int>, gridShape: Pair<Int, Int>)

    fun part1(input: List<String>, width: Int, height: Int): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day14_test.txt` file
    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 1)
    println(part1(testInput, 11, 7))

    // Input from the `src/Day14.txt` file
    val input = readInput("Day14")
//    part1(input, 101, 103).println()
//    part2(input).println()

}
