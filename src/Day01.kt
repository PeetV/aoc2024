import kotlin.math.abs

fun main() {

    fun extractLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val numberPairRegex = Regex("""\s*(\d+)\s+(\d+)""")
        return input.map { line ->
            val matchResult = numberPairRegex.find(line)!!
            matchResult.groupValues[1].toInt() to matchResult.groupValues[2].toInt()
        }.unzip()
    }

    fun part1(list1: List<Int>, list2: List<Int>): Int {
        return list1.sorted().zip(list2.sorted()).sumOf { abs(it.first - it.second) }
    }

    fun part2(list1: List<Int>, list2: List<Int>): Int {
        val counts = list2.groupingBy { it }.eachCount()
        return list1.sumOf { it * counts.getOrDefault(it, 0) }
    }

    // Test input from the `src/Day01_test.txt` file
    val (a, b) = extractLists(readInput("Day01_test"))
    check(part1(a, b) == 11)
    check(part2(a, b) == 31)

    // Input from the `src/Day01.txt` file
    val (c, d) = extractLists(readInput("Day01"))
    part1(c, d).println()
    part2(c, d).println()

}
