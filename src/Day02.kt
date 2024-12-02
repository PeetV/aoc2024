import kotlin.math.abs

fun main() {

    fun extractLists(input: List<String>): List<List<Int>> {
        return input.map { line -> line.trim().split(" ").map { it.toInt() } }
    }

    fun isSafe(input: List<Int>): Int {
        val differenceList = input.zipWithNext().map { it.second - it.first }
        val allIncreasing = differenceList.all { it > 0 }
        val allDecreasing = differenceList.all { it < 0 }
        if (!(allIncreasing || allDecreasing)) return 0
        if (differenceList.map { abs(it) }.any { it > 3 }) return 0
        return 1
    }

    fun isDampenerSafe(input: List<Int>): Int {
        for (index in input.indices) {
            val adjustedInput = input.toMutableList()
            adjustedInput.removeAt(index)
            if (isSafe(adjustedInput) == 1) return 1
        }
        return 0
    }

    fun part1(input: List<List<Int>>): Int {
        return input.sumOf { isSafe(it) }
    }

    fun part2(input: List<List<Int>>): Int {
        return input.sumOf {
            if (isSafe(it) == 1) {
                1
            } else {
                isDampenerSafe(it)
            }
        }
    }

    // Test input from the `src/Day02_test.txt` file
    val testInput = extractLists(readInput("Day02_test"))
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Input from the `src/Day02.txt` file
    val input = extractLists(readInput("Day02"))
    part1(input).println()
    part2(input).println()
}
