fun main() {

    class Robot(var location: Pair<Int, Int>, val velocity: Pair<Int, Int>, val gridShape: Pair<Int, Int>) {

        override fun toString() = "Robot[location=$location, velocity=$velocity]"

        fun move(): Robot {
            val (x, y) = location
            val (vx, vy) = velocity
            val (width, height) = gridShape
            var nextX = x + vx
            while (nextX < 0) nextX += width
            while (nextX >= width) nextX -= width
            var nextY = y + vy
            while (nextY < 0) nextY += height
            while (nextY >= height) nextY -= height
            location = nextX to nextY
            return this
        }

        fun moveTimes(by: Int): Robot {
            repeat(by) { move() }
            return this
        }

        fun inQuadrant(): Int {
            val (width, height) = gridShape
            val (x, y) = location
            val xBoundary = width / 2
            val yBoundary = height / 2
            return when {
                x in 0..<xBoundary && y in 0..<yBoundary -> 1
                x in (xBoundary + 1)..<width && y in 0..<yBoundary -> 2
                x in 0..<xBoundary && y in (yBoundary +1)..<height -> 3
                x in (xBoundary + 1)..<width && y in (yBoundary + 1)..<height -> 4
                else -> 0
            }
        }

    }

    fun parseRobots(input: List<String>, gridShape: Pair<Int, Int>): List<Robot> {
        // Regex pattern to match p=x,y and v=x,y formats
        val pattern = Regex("""(p|v)=(-?\d+),(-?\d+)""")
        // Group the input by parsing all values
        val groupedData = input.mapNotNull { line ->
            val matches = pattern.findAll(line).toList()
            if (matches.size == 2) {
                val locationMatch = matches.first { it.groupValues[1] == "p" }
                val velocityMatch = matches.first { it.groupValues[1] == "v" }
                Robot(
                    location = Pair(
                        locationMatch.groupValues[2].toInt(),
                        locationMatch.groupValues[3].toInt()
                    ),
                    velocity = Pair(
                        velocityMatch.groupValues[2].toInt(),
                        velocityMatch.groupValues[3].toInt()
                    ),
                    gridShape
                )
            } else null
        }
        return groupedData
    }

    fun part1(input: List<String>, width: Int, height: Int): Int {
        val robots = parseRobots(input, width to height)
        robots.forEach { it.moveTimes(100) }
        val quadrants = robots.map { it.inQuadrant() }
        val counts = quadrants.groupingBy { it }.eachCount()
        return counts.keys.filter { it != 0 }.map { counts[it]!! }.fold(1) { c, i -> c * i }
    }

    fun part2(input: List<String>, width: Int, height: Int): Int {
        val robots = parseRobots(input, width to height)
        var seconds = 0
        val maxSeconds = 10_000_000
        while (seconds < maxSeconds) {
            seconds += 1
            robots.forEach { it.move() }
            val quadrants = robots.map { it.inQuadrant() }
            val counts = quadrants.groupingBy { it }.eachCount()
            if ((counts[1]!! + counts[3]!!) == (counts[2]!! + counts[4]!!) &&
                robots.count { it.location.second == 0 } == 1 &&
                robots.count { it.location.second == 1 } == 3) break
        }
        println("Reached $seconds seconds")
        val grid = CharacterGrid(List<String>(height) { ".".repeat(width) })
        for (robot in robots) {
            grid.setCharacter(robot.location, 'X')
        }
        println(grid)
        return seconds
    }

    // Test input from the `src/Day14_test.txt` file
    val testInput = readInput("Day14_test")
    check(part1(testInput, 11, 7) == 12)

    // Input from the `src/Day14.txt` file
    val input = readInput("Day14")
//    part1(input, 101, 103).println()
    part2(input, 101, 103).println()

}
