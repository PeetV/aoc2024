import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

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

    fun iterateStone(stone: Long, iterations: Int): Int {
        val iterationBuffer = MutableList<MutableList<Long>>(iterations) { mutableListOf() }
        iterationBuffer[0] = processStone(stone).toMutableList()
        var result = 0
        val resultCache = mutableMapOf<Long, List<Long>>()
        while (iterationBuffer.any { it.isNotEmpty() }) {
            // If the last slot contains values remove them and add to counter
            val lastSlot = iterationBuffer.last()
            if (lastSlot.isNotEmpty()) {
                result += lastSlot.count()
                iterationBuffer[iterationBuffer.lastIndex] = mutableListOf()
                if (iterationBuffer.all { it.isEmpty() }) break
            }
            // Find the last non-empty slot
            val indexOfLast = iterationBuffer.indexOfLast { it.isNotEmpty() }
            if (indexOfLast == -1) break
            // Process one element from the last non-empty slot and add results to the next slot
            val nextStone = iterationBuffer[indexOfLast].removeFirst()
            val processResult = if (resultCache.keys.contains(nextStone)) {
                 resultCache[nextStone]!!
            } else {
                val cacheItem = processStone(nextStone)
                resultCache[nextStone] = cacheItem
                cacheItem
            }
            iterationBuffer[indexOfLast + 1].addAll(processResult)
        }
        return result
    }

    fun part1(input: List<String>, iterations: Int): Int {
        var result = 0
        runBlocking {
            val deferreds: List<Deferred<Int>> = input.first().split(" ").map  { stone ->
                async {
                    iterateStone(stone.toLong(), iterations)
                }
            }
            result = deferreds.awaitAll().sum()
        }
        return result
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
    part2(input, 75).println()

}
