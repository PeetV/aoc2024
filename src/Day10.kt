import ktml.NodeMappedGraph

fun main() {

    fun buildGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph = NodeMappedGraph<XYLocation, Double>()
        // Add nodes
        for ((columnIndex, rowIndex) in grid.indices) {
            graph.addNode(XYLocation(columnIndex, rowIndex))
        }
        // Add edges
        for ((columnIndex, rowIndex) in grid.indices) {
            val location = XYLocation(columnIndex, rowIndex)
            val locationVal = grid.getCharacter(location.xy).toString().toInt()
            for (direction in listOf(Direction.North, Direction.South, Direction.East, Direction.West)) {
                val nextLocation = location.nextLocation(direction)
                if (grid.isInBounds(nextLocation.xy)) {
                    val nextLocationVal = grid.getCharacter(nextLocation.xy).toString().toInt()
                    if (nextLocationVal - locationVal == 1) {
                        graph.addEdge(fromNode = location, toNode = nextLocation, 1.0).onFailure { throw it }
                    }
                }
            }
        }
        return graph
    }

    fun part1(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val zeroLocations = grid.findLocations('0').map { XYLocation(it.first, it.second) }
        val nineLocations = grid.findLocations('9').map { XYLocation(it.first, it.second) }
        var result = 0
        for (zeroLocation in zeroLocations) {
            for (nineLocation in nineLocations) {
                val path = graph.shortestPathDijkstra(fromNode = zeroLocation, toNode = nineLocation, { it })
                if (path.isSuccess) {
                    result += 1
                }
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day10_test.txt` file
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)

    // Input from the `src/Day10.txt` file
    val input = readInput("Day10")
    part1(input).println()
//    part2(input).println()

}
