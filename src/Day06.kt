fun main() {

    fun nextLocation(grid: Grid, location: Pair<Int, Int>, direction: Grid.Direction): Pair<Int, Int>? {
        val nextLocation = when (direction) {
            Grid.Direction.Up -> location.first to (location.second - 1)
            Grid.Direction.Down -> location.first to (location.second + 1)
            Grid.Direction.Left -> (location.first - 1) to location.second
            Grid.Direction.Right -> (location.first + 1) to location.second
            else -> return null
        }
        if (!grid.isInBounds(nextLocation)) return null
        return nextLocation
    }

    fun changeDirectionOnBlock(direction: Grid.Direction): Grid.Direction {
        return when (direction) {
            Grid.Direction.Up -> Grid.Direction.Right
            Grid.Direction.Down -> Grid.Direction.Left
            Grid.Direction.Left -> Grid.Direction.Up
            Grid.Direction.Right -> Grid.Direction.Down
            else -> Grid.Direction.Up
        }
    }

    fun part1(input: List<String>): Int {
        val grid = Grid(input)
        var location = grid.findLocation('^') ?: (0 to 0)
        var direction = Grid.Direction.Up
        var iterations = 0
        val maxIterations = 10_000
        var visited = mutableSetOf(location)
        while (iterations < maxIterations) {
            iterations += 1
            val next = nextLocation(grid, location, direction)
            if (next == null) break
            if (grid.getCharacter(next) == '#') {
                direction = changeDirectionOnBlock(direction)
                continue
            }
            location = next
            visited.add(location)
        }
        if (iterations >= maxIterations) {
            throw Exception("Maximum iterations reached")
        }
        return visited.count()
    }

    fun part2(input: List<String>): Int {
        var infiniteLoopCount = 0
        val numberOfRows = input.count()
        val numberOfColumns = input.first().count()
        for (rowIndex in 0..<numberOfRows) {
            for (columnIndex in 0..<numberOfColumns) {
                try {
                    val newGrid = Grid(input)
                    if (newGrid.getCharacter(columnIndex to rowIndex) == '#') continue
                    newGrid.setCharacter(columnIndex to rowIndex, '#')
                    part1(newGrid.toListString())
                } catch (_: Throwable) {
                    infiniteLoopCount += 1
                }
            }
        }
        return infiniteLoopCount
    }

    // Test input from the `src/Day06_test.txt` file
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    // Input from the `src/Day06.txt` file
    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()

}
