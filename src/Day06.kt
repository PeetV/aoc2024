fun main() {

    fun nextLocation(grid: CharacterGrid, location: Pair<Int, Int>, direction: Direction): Pair<Int, Int>? {
        val nextLocation = when (direction) {
            Direction.Up -> location.first to (location.second - 1)
            Direction.Down -> location.first to (location.second + 1)
            Direction.Left -> (location.first - 1) to location.second
            Direction.Right -> (location.first + 1) to location.second
        }
        if (!grid.isInBounds(nextLocation)) return null
        return nextLocation
    }

    fun changeDirectionOnBlock(direction: Direction): Direction {
        return when (direction) {
            Direction.Up -> Direction.Right
            Direction.Down -> Direction.Left
            Direction.Left -> Direction.Up
            Direction.Right -> Direction.Down
        }
    }

    fun part1(input: List<String>): Int {
        val characterGrid = CharacterGrid(input)
        var location = characterGrid.findLocation('^') ?: (0 to 0)
        var direction = Direction.Up
        var iterations = 0
        val maxIterations = 10_000
        var visited = mutableSetOf(location)
        while (iterations < maxIterations) {
            iterations += 1
            val next = nextLocation(characterGrid, location, direction)
            if (next == null) break
            if (characterGrid.getCharacter(next) == '#') {
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
                    val newCharacterGrid = CharacterGrid(input)
                    if (newCharacterGrid.getCharacter(columnIndex to rowIndex) == '#') continue
                    newCharacterGrid.setCharacter(columnIndex to rowIndex, '#')
                    part1(newCharacterGrid.toStringList())
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
