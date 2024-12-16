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

    fun pathTurns(path: List<XYLocation>): Int {
        val workingPath = path.toMutableList()
        var turns = 0
        var currentDirection = Direction.East
        var currentLocation = workingPath.removeFirst()
        while (workingPath.isNotEmpty()) {
            val nextLocation = workingPath.removeFirst()
            if (path.isEmpty()) break
            val nextDirection = currentLocation.directionTo(nextLocation)
            if (nextDirection != currentDirection) {
                turns += 1
                currentDirection = nextDirection
            }
            currentLocation = nextLocation
        }
        return turns
    }

    fun enumeratePaths(graph: NodeMappedGraph<XYLocation, Double>, from: XYLocation, to: XYLocation): List<List<XYLocation>> {
        var paths = mutableListOf(mutableListOf(from))
        var iterations = 0
        while (iterations < 10_000) {
            iterations += 1
            if (paths.all { it.last() == to }) break
            paths = paths.filter { graph.childNodes(it.last()).getOrThrow().count() > 0 }.toMutableList()
            val newPaths = mutableListOf<MutableList<XYLocation>>()
            for (path in paths) {
                if (path.last() == to) {
                    newPaths.add(path)
                    continue
                }
                for (childNode in graph.childNodes(path.last()).getOrThrow()) {
                    val newPath = path.toMutableList()
                    if (newPath.contains(childNode)) continue
                    newPath.add(childNode)
                    newPaths.add(newPath)
                }
            }
            paths = newPaths
        }
        return paths
    }

    fun part1(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val graph = buildGraph(grid)
        val startLocation = XYLocation(grid.findLocation('S')!!)
        val endLocation = XYLocation(grid.findLocation('E')!!)
        val paths = enumeratePaths(graph, startLocation, endLocation)
        val pathsTurns = paths.map { pathTurns(it) }
        val results = paths.zip(pathsTurns).map { it.first.count() - 1 + (it.second * 1000) }
        val indexOfLowestResult = results.indexOf(results.min())
        val finalPath = paths[indexOfLowestResult]
        val pathSteps = finalPath.count() - 1
        val turns = pathsTurns[indexOfLowestResult]
        return pathSteps + (turns * 1000)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day16_test1.txt` file
    var testInput = readInput("Day16_test1")
    check(part1(testInput) == 7036)

    // Test input from the `src/Day16_test2.txt` file
    testInput = readInput("Day16_test2")
    check(part1(testInput) == 11048)

    // Input from the `src/Day16.txt` file
//    val input = readInput("Day16")
//    part1(input).println()
//    part2(input).println()

}
