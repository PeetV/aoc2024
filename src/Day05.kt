fun main() {

    fun extractData(input: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val rules = mutableListOf<Pair<Int, Int>>()
        val updates = mutableListOf<List<Int>>()
        var findingRules = true
        for (line in input) {
            if (line.isBlank()) {
                findingRules = false
                continue
            }
            if (findingRules) {
                val (first, second) = line.trim().split("|")
                rules.add(first.toInt() to second.toInt())
            } else {
                updates.add(line.trim().split(",").map { it.toInt() })
            }
        }
        return rules to updates
    }

    fun orderingCorrect(rule: Pair<Int, Int>, update: List<Int>): Boolean? {
        val (first, second) = rule
        if (!update.contains(first) || !update.contains(second)) return null
        return update.indexOf(first) < update.indexOf(second)
    }

    fun <T> moveElementBefore(list: MutableList<T>, firstIndex: Int, secondIndex: Int) {
        val elementToMove = list.removeAt(secondIndex)
        val insertIndex = if (secondIndex < firstIndex) firstIndex - 1 else firstIndex
        list.add(insertIndex, elementToMove)
    }

    fun fixUpdate(rules: List<Pair<Int, Int>>, update: List<Int>): List<Int> {
        var result = update.toMutableList()
        for ((first, second) in rules) {
            if (!result.contains(first) || !result.contains(second)) continue
            val indexOfFirst = result.indexOf(first)
            val indexOfSecond = result.indexOf(second)
            if (indexOfFirst > indexOfSecond) {
                moveElementBefore(result, indexOfSecond, indexOfFirst)
                result = fixUpdate(rules, result).toMutableList()
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val (rules, updates) = extractData(input)
        var result = 0
        for (update in updates) {
            val checks = rules.map { rule -> orderingCorrect(rule, update) }
            if (checks.filter { it != null }.all { it!! }) {
                val middle = update.count() / 2
                result += update[middle]
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val (rules, updates) = extractData(input)
        var result = 0
        for (update in updates) {
            val checks = rules.map { rule -> orderingCorrect(rule, update) }
            if (!checks.filter { it != null }.all { it!! }) {
                val fixedUpdate = fixUpdate(rules, update)
                val middle = fixedUpdate.count() / 2
                result += fixedUpdate[middle]
            }
        }
        return result
    }

    // Test input from the `src/Day05_test.txt` file
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    // Input from the `src/Day05.txt` file
    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()

}
