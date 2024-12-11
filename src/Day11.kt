fun main() {

    fun part1(input: List<String>, iterations: Int): Int {
        var buffer = input.first().split(" ").map { it.toLong() }
        var counter = 0
        while (counter < iterations) {
            counter += 1
            var newBuffer = mutableListOf<Long>()
            for (stone in buffer) {
                when {
                    stone == 0L -> newBuffer.add(1L)
                    "$stone".count() < 2 -> newBuffer.add(stone * 2024L)
                    "$stone".count() % 2 == 0 -> {
                        val digits = "$stone"
                        val length = digits.count() / 2
                        newBuffer.add(digits.slice(0..<length).toLong())
                        newBuffer.add(digits.slice(length..digits.lastIndex).toLong())
                    }
                    else -> newBuffer.add(stone * 2024L)
                }
            }
            buffer = newBuffer
        }
        return buffer.count()
    }

    fun part2(input: List<String>, iterations: Int): Int {
        return part1(input, iterations)
    }

    // Test input from the `src/Day11_test.txt` file
    val testInput = readInput("Day11_test")
    check(part1(testInput, 25) == 55312)

    // Input from the `src/Day11.txt` file
    val input = readInput("Day11")
    part1(input, 25).println()
//    part2(input, 75).println()

}
