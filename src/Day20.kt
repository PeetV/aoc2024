import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

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

    fun hasOpenNeighbour(grid: CharacterGrid, location: Pair<Int, Int>): Boolean {
        if (!grid.isInBounds(location)) return false
        val allowedToVisit = listOf('.', 'S', 'E')
        // Up
        if (location.second > 0) {
            val upC = grid.getCharacter(location.first to (location.second - 1))
            if (allowedToVisit.contains(upC)) return true
        }
        // Down
        if (location.second < (grid.numberOfRows - 1)) {
            val downC = grid.getCharacter(location.first to (location.second + 1))
            if (allowedToVisit.contains(downC)) return true
        }
        // Left
        if (location.first > 0) {
            val leftC = grid.getCharacter((location.first - 1) to location.second)
            if (allowedToVisit.contains(leftC)) return true
        }
        // Right
        if (location.first < (grid.numberOfColumns - 1)) {
            val rightC = grid.getCharacter((location.first + 1) to location.second)
            if (allowedToVisit.contains(rightC)) return true
        }
        return false
    }

    fun part1(input: List<String>, threshold: Int): Int {
        val grid = CharacterGrid(input)
        val graph = gridToGraph(grid)
        val startLocation = XYLocation(grid.findLocation('S')!!)
        val endLocation = XYLocation(grid.findLocation('E')!!)
        val path = graph.shortestPathDijkstra(fromNode = startLocation, toNode = endLocation, {it})
        val baselinePathSize = path.getOrThrow().count() - 1
        var solutionCount = 0
        runBlocking {
            val deferredValues = grid.indices.toList().map {
                val (columnIndex, rowIndex) = it
                async {
                    val grid2 = CharacterGrid(input)
                    val character = grid2.getCharacter(columnIndex to rowIndex)
                    if (character == '#' && hasOpenNeighbour(grid2, columnIndex to rowIndex)) {
                        grid2.setCharacter(columnIndex to rowIndex, '.')
                        val graph2 = gridToGraph(grid2)
                        val path2 = graph2.shortestPathDijkstra(fromNode = startLocation, toNode = endLocation, { it })
                        val path2Size = path2.getOrThrow().count() - 1
                        val gain = baselinePathSize - path2Size
                        gain
                    } else 0
                }
            }
            solutionCount = deferredValues.awaitAll().count { it >= threshold }
        }
        return solutionCount
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day20_test.txt` file
    val testInput = readInput("Day20_test")
    check(part1(testInput, 40) == 2)

    // Input from the `src/Day20.txt` file
    val input = readInput("Day20")
    part1(input, 100).println()
//    part2(input).println()

}
