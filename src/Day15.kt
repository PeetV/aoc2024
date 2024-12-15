fun main() {

    fun processFile(input: List<String>): Pair<CharacterGrid, String> {
        val blankLine = input.indexOfFirst { it.isBlank() }
        val grid = CharacterGrid(input.slice(0..<blankLine))
        val moves = input.slice((blankLine + 1)..input.lastIndex).joinToString("")
        return grid to moves
    }

    fun charToDirection(input: Char): Direction {
        return when (input) {
            '<' -> Direction.West
            '>' -> Direction.East
            '^' -> Direction.North
            'v' -> Direction.South
            else -> Direction.North
        }
    }

    fun part1(input: List<String>): Int {
        val (grid, moves) = processFile(input)
        var robotLocation = XYLocation(grid.findLocation('@')!!)
        moves@ for (move in moves) {
            val direction = charToDirection(move)
            val nextLocation = robotLocation.nextLocation(direction)
            val nextLocationCharacter = grid.getCharacter(nextLocation.xy)
            if (nextLocationCharacter == '#') continue
            if (nextLocationCharacter == '.') {
                grid.swap(robotLocation.xy, nextLocation.xy)
                robotLocation = nextLocation
                continue
            }
            if (nextLocationCharacter == 'O') {
                val locationStack = mutableListOf(nextLocation)
                while (true) {
                    val nextBoxLocation = locationStack.last().nextLocation(direction)
                    val nextBoxLocationCharacter = grid.getCharacter(nextBoxLocation.xy)
                    if (nextBoxLocationCharacter == '#') continue@moves
                    if (nextBoxLocationCharacter == '.') {
                        locationStack.add(nextBoxLocation)
                        break
                    }
                    if (nextBoxLocationCharacter == 'O') locationStack.add(nextBoxLocation)
                }
                while (locationStack.count() > 1) {
                    val last = locationStack.removeLast()
                    val secondLast = locationStack.last()
                    grid.swap(last.xy, secondLast.xy)
                }
                grid.swap(robotLocation.xy, nextLocation.xy)
                robotLocation = nextLocation
            }
        }
        val result = grid.findLocations('O').sumOf { (it.second * 100) + it.first }
        return result
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day15_test1.txt` file
    var testInput = readInput("Day15_test1")
    check(part1(testInput) == 2028)

    // Test input from the `src/Day15_test2.txt` file
    testInput = readInput("Day15_test2")
    check(part1(testInput) == 10092)

    // Input from the `src/Day15.txt` file
    val input = readInput("Day15")
    part1(input).println()
//    part2(input).println()

}
