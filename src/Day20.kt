fun main() {

    fun gridToGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph =  NodeMappedGraph<XYLocation, Double>()
        val allowedToVisit = listOf('.', 'S', 'E')
        for ((columnIndex, rowIndex) in grid.indices) {
            val location = XYLocation(columnIndex, rowIndex)
            val character = grid.getCharacter(location.xy)
            if (allowedToVisit.contains(character)) {
                if (!graph.hasNode(location)) graph.addNode(location)
                for (direction in cityBlockDirections) {
                    val nextLocation = location.nextLocation(direction)
                    if (!grid.isInBounds(nextLocation.xy)) continue
                    if (!graph.hasNode(nextLocation)) graph.addNode(nextLocation)
                    val nextCharacter = grid.getCharacter(nextLocation.xy)
                    if (allowedToVisit.contains(nextCharacter)) {
                        graph.addEdge(fromNode = location, toNode = nextLocation, 1.0)
                    }
                }
            }
        }
        return graph
    }

    fun part1(input: List<String>, threshold: Int): Int {
        val grid = CharacterGrid(input)
        val graph = gridToGraph(grid)
        val startLocation = XYLocation(grid.findLocation('S')!!)
        val endLocation = XYLocation(grid.findLocation('E')!!)
        val path = graph.shortestPathDijkstra(fromNode = startLocation, toNode = endLocation, {it})
        val baselinePathSize = path.getOrThrow().count() - 1

        var solutionCount = 0
        for ((columnIndex, rowIndex) in grid.indices) {
            val grid2 = CharacterGrid(input)
            val character = grid2.getCharacter(columnIndex to rowIndex)
            if (character == '#') {
                grid2.setCharacter(columnIndex to rowIndex, '.')
                val graph2 = gridToGraph(grid)
                val path2 = graph2.shortestPathDijkstra(fromNode = startLocation, toNode = endLocation, {it})
                val path2Size = path2.getOrThrow().count() - 1
                val gain = baselinePathSize - path2Size
                if (gain >= threshold) solutionCount += 1
            }
        }

        return solutionCount
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day20_test.txt` file
    val testInput = readInput("Day20_test")
    println(part1(testInput, 40))
//    check(part1(testInput) == 1)

    // Input from the `src/Day20.txt` file
//    val input = readInput("Day20")
//    part1(input).println()
//    part2(input).println()

}
