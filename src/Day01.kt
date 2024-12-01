import kotlin.math.abs

fun main() {

    fun extractLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val numberPairRegex = Regex("""\s*(\d+)\s+(\d+)""")
        val numberPairs = input.map { line ->
            numberPairRegex.find(line)?.let { matchResult ->
                matchResult.groupValues[1].toInt() to matchResult.groupValues[2].toInt()
            }
        }
        val list1Sorted = numberPairs.map { it?.first ?: 0 }.sorted()
        val list2Sorted = numberPairs.map { it?.second ?: 0 }.sorted()
        return list1Sorted to list2Sorted
    }

    fun part1(input: List<String>): Int {
        val (list1Sorted, list2Sorted) = extractLists(input)
        return list1Sorted.zip(list2Sorted).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val (list1Sorted, list2Sorted) = extractLists(input)
        return list1Sorted.sumOf { list1Number ->
            list1Number * list2Sorted.count { it == list1Number }
        }
    }

    // Test input from the `src/Day01_test.txt` file
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Input from the `src/Day01.txt` file
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()

}
