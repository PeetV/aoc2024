import kotlin.math.abs

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
            val char = grid.getCharacter(location.xy)
            val locationVal = char.toString().toInt()
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
                if (abs(zeroLocation.x - nineLocation.x) > 10) continue
                if (abs(zeroLocation.y - nineLocation.y) > 10) continue
                val path = graph.shortestPathDijkstra(fromNode = zeroLocation, toNode = nineLocation, { it })
                if (path.isSuccess) {
                    result += 1
                }
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val zeroLocations = grid.findLocations('0').map { XYLocation(it.first, it.second) }
        var result = 0
        for (zeroLocation in zeroLocations) {
            var stack = mutableListOf<XYLocation>(zeroLocation)
            var level = 0
            while (level < 9) {
                val newStack = mutableListOf<XYLocation>()
                for (location in stack) {
                    val locationChildren = graph.childNodes(location).getOrThrow()
                    newStack.addAll(locationChildren)
                    level += 1
                }
                stack = newStack
            }
            result += stack.count()
        }
        return result
    }

    // Test input from the `src/Day10_test.txt` file
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    println(part2(testInput))
//    check(part2(testInput) == 81)

    // Input from the `src/Day10.txt` file
//    val input = readInput("Day10")
//    part1(input).println()
//    part2(input).println()

}
