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

    fun pathToStart(pathTree: NodeMappedGraph<XYLocation, Double>, endNode: XYLocation): List<XYLocation> {
        val result = mutableListOf<XYLocation>(endNode)
        var currentNode = endNode
        while (true) {
            val parentNodes = pathTree.parentNodes(currentNode).getOrThrow()
            if (parentNodes.isEmpty()) break
            val parentNode = parentNodes.first()
            result.add(parentNode)
            currentNode = parentNode
        }
        return result
    }

    fun enumeratePaths(graph: NodeMappedGraph<XYLocation, Double>, from: XYLocation, to: XYLocation): List<List<XYLocation>> {
        val pathTree = NodeMappedGraph<XYLocation, Double>()
        pathTree.addNode(from).getOrThrow()
        var endNodes = mutableListOf(from)
        while (endNodes.isNotEmpty()) {
            val newEndNodes = mutableListOf<XYLocation>()
            for (endNode in endNodes) {
                val children = graph.childNodes(endNode).getOrThrow()
                if (children.isEmpty()) continue
                val path = pathToStart(pathTree, endNode)
                for (child in children) {
                    if (path.contains(child)) continue
                    if (!pathTree.hasNode(child)) pathTree.addNode(child)
                    pathTree.addEdge(endNode, child, 1.0).getOrThrow()
                    newEndNodes.add(child)
                }
            }
            endNodes = newEndNodes
        }
        println("Path tree populated")
        val toParents = pathTree.parentNodes(to).getOrThrow()
        val result = mutableListOf<List<XYLocation>>()
        for (parent in toParents) {
            val path = pathToStart(pathTree, parent).reversed().toMutableList()
            path.add(to)
            result.add(path)
        }
        return result
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
//    testInput = readInput("Day16_test2")
//    check(part1(testInput) == 11048)

    // Input from the `src/Day16.txt` file
//    val input = readInput("Day16")
//    part1(input).println()
//    part2(input).println()

}
