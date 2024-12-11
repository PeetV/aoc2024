fun main() {

    fun processStone(stone: Long): List<Long> {
        val result = mutableListOf<Long>()
        when {
            stone == 0L -> result.add(1L)
            "$stone".count() < 2 -> result.add(stone * 2024L)
            "$stone".count() % 2 == 0 -> {
                val digits = "$stone"
                val length = digits.count() / 2
                result.add(digits.slice(0..<length).toLong())
                result.add(digits.slice(length..digits.lastIndex).toLong())
            }

            else -> result.add(stone * 2024L)
        }
        return result
    }

    fun part1(input: List<String>, iterations: Int): Long {
        var stoneCounts = input.first().split(" ").map { it.toLong() }.groupingBy { it }.eachCount().toList()
            .associate { it.first to it.second.toLong() }.toMutableMap()
        repeat(iterations) {
            var newCounts = mutableMapOf<Long, Long>()
            for (stone in stoneCounts.keys) {
                val count = stoneCounts[stone]!!
                val processed = processStone(stone)
                for (stone in processed) {
                    newCounts[stone] = newCounts.getOrDefault(stone, 0) + count
                }
            }
            stoneCounts = newCounts
        }
        return stoneCounts.values.sum()
    }

    fun part2(input: List<String>, iterations: Int): Long {
        return part1(input, iterations)
    }

    // Test input from the `src/Day11_test.txt` file
    val testInput = readInput("Day11_test")
    check(part1(testInput, 25) == 55312L)

    // Input from the `src/Day11.txt` file
    val input = readInput("Day11")
    part1(input, 25).println()
    part2(input, 75).println()

}
