fun main() {

    fun gridToGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph = NodeMappedGraph<XYLocation, Double>()
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

    fun reachablePoints(from: XYLocation, grid: CharacterGrid): List<XYLocation> {
        val result = mutableListOf<XYLocation>()
        for (step1Direction in cityBlockDirections) {
            val step1Location = from.nextLocation(step1Direction)
            val step1Char = grid.getCharacter(step1Location.xy)
            if (step1Char != '#') continue
            for (step2Direction in cityBlockDirections) {
                val step2Location = step1Location.nextLocation(step2Direction)
                val step2Char = grid.getCharacter(step2Location.xy)
                if ((step2Char == '.' || step2Char == 'E')) {
                    result.add(step2Location)
                }
            }
        }
        return result
    }

    fun part1(input: List<String>, threshold: Int): Int {
        val results = mutableListOf<Int>()
        val grid = CharacterGrid(input)
        val graph = gridToGraph(grid)
        val startLocation = XYLocation(grid.findLocation('S')!!)
        val endLocation = XYLocation(grid.findLocation('E')!!)
        val path = graph.shortestPathDijkstra(fromNode = startLocation, toNode = endLocation) { it }.getOrThrow()
        for (from in path) {
            val fromIndex = path.indexOf(from)
            if (fromIndex == -1) throw IndexOutOfBoundsException("Unexpected index")
            val options = reachablePoints(from, grid)
            for (to in options) {
                val toIndex = path.indexOf(to)
                if (toIndex == -1) throw IndexOutOfBoundsException("Unexpected index")
                val saving = toIndex - fromIndex - 2
                if (saving > 0) {
                    results.add(saving)
                }
            }
        }
        return results.count { it >= threshold }
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
