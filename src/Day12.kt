fun main() {

    fun buildGraph(grid: CharacterGrid): NodeMappedGraph<XYLocation, Double> {
        val graph = NodeMappedGraph<XYLocation, Double>()
        for ((columnIndex, rowIndex) in grid.indices) {
            graph.addNode(XYLocation(columnIndex, rowIndex))
        }
        for ((columnIndex, rowIndex) in grid.indices) {
            val location = XYLocation(columnIndex, rowIndex)
            val character = grid.getCharacter(location.xy)
            for (direction in listOf(Direction.North, Direction.South, Direction.East, Direction.West)) {
                val nextLocation = location.nextLocation(direction)
                if (grid.isInBounds(nextLocation.xy)) {
                    val nextCharacter = grid.getCharacter(nextLocation.xy)
                    if (character == nextCharacter) {
                        graph.addEdge(location, nextLocation, 1.0)
                    }
                }
            }
        }
        return graph
    }

    fun calculateRegionPrice(region: List<XYLocation>, grid: CharacterGrid): Int {
        val area = region.count()
        val perimeter = region.sumOf { location ->
            var result = 0
            for (direction in listOf(Direction.North, Direction.South, Direction.East, Direction.West)) {
                val nextLocation = location.nextLocation(direction)
                if (!grid.isInBounds(nextLocation.xy)) {
                    result += 1
                } else {
                    if (grid.getCharacter(nextLocation.xy) != grid.getCharacter(location.xy)) {
                        result += 1
                    }
                }
            }
            result
        }
        return area * perimeter
    }

    fun calculateBulkPrice(region: List<XYLocation>, grid: CharacterGrid): Int {
        val area = region.count()
        var priceSum = 0

        println("area $area price $priceSum")
        return area * priceSum
    }

    fun part1(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val locationQueue = graph.nodes.toMutableList()
        var result = 0
        while (locationQueue.isNotEmpty()) {
            val location = locationQueue.removeFirst()
            val region = graph.walkDepthFirst(location, 10_000, includeBacktrack = false).getOrThrow()
            region.forEach { locationQueue.remove(it) }
            result += calculateRegionPrice(region, grid)
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val locationQueue = graph.nodes.toMutableList()
        var result = 0
        while (locationQueue.isNotEmpty()) {
            val location = locationQueue.removeFirst()
            val region = graph.walkDepthFirst(location, 10_000, includeBacktrack = false).getOrThrow()
            region.forEach { locationQueue.remove(it) }
            result += calculateBulkPrice(region, grid)
        }
        return result
    }

    // Test input from the `src/Day12_test.txt` file
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930)
    println(part2(testInput))

    // Input from the `src/Day12.txt` file
    val input = readInput("Day12")
    part1(input).println()
//    part2(input).println()

}
