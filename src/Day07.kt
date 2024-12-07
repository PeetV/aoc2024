fun main() {

    fun extractEquations(input: List<String>): List<Pair<Long, List<Long>>> {
        val result = mutableListOf<Pair<Long, List<Long>>>()
        for (row in input) {
            val (lhs, rhs) = row.split(":")
            val equationResult = lhs.trim().toLong()
            val equationInput = rhs.split("\\s+".toRegex()).filter { !it.trim().isEmpty() }.map { it.toLong() }
            result.add(equationResult to equationInput)
        }
        return result
    }

    fun equationIsValid(equation: Pair<Long, List<Long>>, includeJoin: Boolean = false): Boolean {
        val numberQueue = equation.second.toMutableList()
        var equationResults = mutableListOf<Long>()
        while (numberQueue.isNotEmpty()) {
            val nextVal = numberQueue.removeFirst()
            if (equationResults.isEmpty()) {
                equationResults.add(nextVal)
                continue
            }
            val multiplied = equationResults.map { it * nextVal }
            val added = equationResults.map { it + nextVal }
            if (includeJoin) {
                val joined = equationResults.map { "${it}${nextVal}".toLong() }
                equationResults = multiplied.toMutableList()
                equationResults.addAll(added)
                equationResults.addAll(joined)
            } else {
                equationResults = multiplied.toMutableList()
                equationResults.addAll(added)
            }
        }
        return equationResults.contains(equation.first)
    }

    fun part1(input: List<String>): Long {
        val equations = extractEquations(input)
        var result = 0L
        for (equation in equations) {
            if (equationIsValid(equation)) {
                result += equation.first
            }
        }
        return result
    }

    fun part2(input: List<String>): Long {
        val equations = extractEquations(input)
        var result = 0L
        for (equation in equations) {
            if (equationIsValid(equation, includeJoin = true)) {
                result += equation.first
            }
        }
        return result
    }

    // Test input from the `src/Day07_test.txt` file
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    // Input from the `src/Day07.txt` file
    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()

}
