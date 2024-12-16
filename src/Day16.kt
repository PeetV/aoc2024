fun main() {

    fun buildGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph = NodeMappedGraph<XYLocation, Double>()
        for ((columnIndex, rowIndex) in grid.indices) {
            val location = XYLocation(columnIndex, rowIndex)
            val moveLocations = listOf('.', 'S', 'E')
            val character = grid.getCharacter(location.xy)
            if (moveLocations.contains(character)) {
                if (!graph.hasNode(location)) graph.addNode(location).getOrThrow()
                for (direction in listOf(Direction.North, Direction.South, Direction.West, Direction.East)) {
                    val nextLocation = location.nextLocation(direction)
                    val nextCharacter = grid.getCharacter(nextLocation.xy)
                    if (moveLocations.contains(nextCharacter)) {
                        if (!graph.hasNode(nextLocation)) graph.addNode(nextLocation).getOrThrow()
                        graph.addEdge(location, nextLocation, 1.0).getOrThrow()
                    }
                }
            }
        }
        return graph
    }

    fun part1(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val startLocation = XYLocation(grid.findLocation('S')!!)
        val endLocation = XYLocation(grid.findLocation('E')!!)
        val path = graph.shortestPathDijkstra(startLocation, endLocation, { it }).getOrThrow().toMutableList()
        graph.println()
        path.println()
        var result = 0
        var currentDirection = Direction.East
        var currentLocation = path.removeFirst()
        while (path.isNotEmpty()) {

        }
        return result
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day16_test.txt` file
    val testInput = readInput("Day16_test1")
    part1(testInput).println()
//    check(part1(testInput) == 1)

    // Input from the `src/Day16.txt` file
//    val input = readInput("Day16")
//    part1(input).println()
//    part2(input).println()

}
