fun main() {

    fun part1(input: List<String>): Int {
        val mulRegex = Regex("""mul\((\d+),(\d+)\)""")
        return mulRegex.findAll(input.joinToString("")).map { matchResult ->
            matchResult.groupValues[1].toInt() * matchResult.groupValues[2].toInt()
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val adjustedInput = "do()${input.joinToString("")}don't()"
        val doDoNotRegex = Regex("""do\(\)(.*?)don't\(\)""")
        return doDoNotRegex.findAll(adjustedInput).map { matchResult ->
            part1(listOf(matchResult.groupValues[1]))
        }.sum()
    }

    // Test input from the `src/Day03_test1.txt` file
    val testInput1 = readInput("Day03_test1")
    check(part1(testInput1) == 161)

    // Test input from the `src/Day03_test2.txt` file
    val testInput2 = readInput("Day03_test2")
    check(part2(testInput2) == 48)

    // Input from the `src/Day03.txt` file
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()

}
