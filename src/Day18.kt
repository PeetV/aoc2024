fun main() {

    fun buildGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph = NodeMappedGraph<XYLocation, Double>()
        for ((columnIndex, rowIndex) in grid.indices) {
            val location = XYLocation(columnIndex, rowIndex)
            if (!graph.hasNode(location)) graph.addNode(location).getOrThrow()
            for (direction in cityBlockDirections) {
                val nextLocation = location.nextLocation(direction)
                if (!grid.isInBounds(nextLocation.xy)) continue
                val nextLocationChar = grid.getCharacter(nextLocation.xy)
                if (nextLocationChar == '.') {
                    if (!graph.hasNode(nextLocation)) graph.addNode(nextLocation).getOrThrow()
                    graph.addEdge(location, nextLocation, 1.0)
                }
            }
        }
        return graph
    }

    fun part1(input: List<String>, byteCount: Int): Int {
        val coordinates = input.map { it.split(",") }.map { (a, b) -> a.toInt() to b.toInt()}
        val maxX = coordinates.maxOf { it.first }
        val maxY = coordinates.maxOf { it.second }
        val grid = CharacterGrid( List<String>(maxY + 1){ ".".repeat(maxX + 1) } )
        coordinates.slice(0..<byteCount).forEach { grid.setCharacter(it, '#') }
        val graph = buildGraph(grid)
        val path = graph.shortestPathDijkstra(XYLocation(0, 0), XYLocation(maxX, maxY), { it }).getOrThrow()
        return path.count() - 1
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day18_test.txt` file
    val testInput = readInput("Day18_test")
    check(part1(testInput, 12) == 22)

    // Input from the `src/Day18.txt` file
    val input = readInput("Day18")
    part1(input, 1024).println()
//    part2(input).println()

}
