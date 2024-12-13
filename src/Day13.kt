fun main() {

    fun extractInputs(input: String): List<Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>>? {
        // Regex pattern to match the input format
        val pattern = """
        Button A: X\+(\d+), Y\+(\d+)
        Button B: X\+(\d+), Y\+(\d+)
        Prize: X=(\d+), Y=(\d+)
        """.trimIndent().toRegex(RegexOption.MULTILINE)
        // Find all matches in the input string
        val matches = pattern.findAll(input)
        // Transform matches into list of inputs
        return matches.map { matchResult ->
            val (ax, ay, bx, by, px, py) = matchResult.destructured

            Triple(
                Pair(ax.toInt(), ay.toInt()),  // Button A coordinates
                Pair(bx.toInt(), by.toInt()),  // Button B coordinates
                Pair(px.toInt(), py.toInt())   // Prize coordinates
            )
        }.toList().takeIf { it.isNotEmpty() }
    }

    fun minimumTokens(a: Pair<Int, Int>, b: Pair<Int, Int>, target: Pair<Int, Int>): Int? {
        val graph = NodeMappedGraph<XYLocation, Char>()
        val startNode = XYLocation(0, 0)
        // build the graph
        graph.addNode(startNode)
        val maxIterations = 10_000
        var iterations = 0
        while (iterations <= maxIterations) {
            iterations += 1
            val endNodes = graph.nodes.filter { graph.childNodes(it).getOrThrow().count() == 0 }
            if (endNodes.all { it.x > target.first || it.y > target.second }) break
            for (endNode in endNodes) {
                val aNode = XYLocation(endNode.x + a.first, endNode.y + a.second)
                val bNode = XYLocation(endNode.x + b.first, endNode.y + b.second)
                if (!graph.hasNode(aNode)) graph.addNode(aNode)
                if (!graph.hasNode(bNode)) graph.addNode(bNode)
                graph.addEdge(endNode, aNode, 'a')
                graph.addEdge(endNode, bNode, 'b')
            }
        }
        if (iterations > maxIterations) throw Exception("Maximum iterations exceeded")
        // find the shortest path to the target
        val targetNode = XYLocation(target)
        if (!graph.hasNode(targetNode)) return null
        val path = graph.shortestPathDijkstra(startNode, targetNode, { if (it == 'a') 3.0 else 1.0 })
        if (path.isFailure) return null
        // Calculate the number of tokens
        val weight = graph.pathWeight(path.getOrThrow(), { if (it == 'a') 3.0 else 1.0 })
        return if (weight.isFailure) null else weight.getOrThrow().toInt()
    }

    fun minimumTokens2(a: Pair<Long, Long>, b: Pair<Long, Long>, target: Pair<Long, Long>): Long {

        return 0
    }

    fun part1(input: List<String>): Int {
        val inputs = extractInputs(input.joinToString("\n"))
        var total = 0
        for (input in inputs!!) {
            val (a, b, target) = input
            total += minimumTokens(a, b, target) ?: 0
        }
        return total
    }

    fun part2(input: List<String>): Int {
        println(minimumTokens2(94L to 34L, 22L to 67L, 10000000008400L to 10000000005400L))
        return 0
    }

    // Test input from the `src/Day13_test.txt` file
    val testInput = readInput("Day13_test")
//    check(part1(testInput) == 480)

//    val testInput2 = readInput("Day13_test2")
//    println(part2(testInput2))

    // Input from the `src/Day13.txt` file
//    val input = readInput("Day13")
//    part1(input).println()
//    part2(input).println()

}
