fun main() {

    fun antennaLocations(grid: CharacterGrid): MutableMap<Char, MutableList<XYLocation>> {
        val frequencies = mutableMapOf<Char, MutableList<XYLocation>>()
        for ((columnIndex, rowIndex) in grid.indices) {
            val character = grid.getCharacter(columnIndex to rowIndex)
            if (character == '.') continue
            val workingList = frequencies[character] ?: mutableListOf()
            workingList.add(XYLocation(columnIndex, rowIndex, null))
            frequencies[character] = workingList
        }
        return frequencies
    }

    fun antiNodeLocations(antennaPair: List<XYLocation>): List<XYLocation> {
        if (antennaPair.count() != 2) throw Exception("Antenna pair expected but got ${antennaPair.count()}")
        val (higher, lower) = antennaPair.sortedByDescending { it.y }
        val deltaX = higher.x - lower.x
        val deltaY = higher.y - lower.y
        val higherNode = XYLocation(higher.x + deltaX, higher.y + deltaY, null)
        val lowerNode = XYLocation(lower.x - deltaX, lower.y - deltaY, null)
        return listOf(higherNode, lowerNode)
    }

    fun antiNodeLocations2(antennaPair: List<XYLocation>, grid: CharacterGrid): List<XYLocation> {
        val result = mutableSetOf<XYLocation>()
        if (antennaPair.count() != 2) throw Exception("Antenna pair expected but got ${antennaPair.count()}")
        val (higher, lower) = antennaPair.sortedByDescending { it.y }
        val deltaX = higher.x - lower.x
        val deltaY = higher.y - lower.y
        var currentNode = higher
        while (true) {
            val nextNode = XYLocation(currentNode.x + deltaX, currentNode.y + deltaY, null)
            if (grid.isInBounds(nextNode.xy)) {
                result.add(nextNode)
                currentNode = nextNode
            } else break
        }
        currentNode = lower
        while (true) {
            val nextNode = XYLocation(currentNode.x - deltaX, currentNode.y - deltaY, null)
            if (grid.isInBounds(nextNode.xy)) {
                result.add(nextNode)
                currentNode = nextNode
            } else break
        }
        return result.toList()
    }

    fun part1(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val antennas = antennaLocations(grid)
        val nodes = mutableSetOf<XYLocation>()
        for (frequency in antennas.keys) {
            val locationPairs = antennas[frequency]!!.combinations(2).toList()
            for (locationPair in locationPairs) {
                val antiNodes = antiNodeLocations(locationPair)
                for (antiNode in antiNodes) {
                    if (grid.isInBounds(antiNode.xy)) nodes.add(antiNode)
                }
            }
        }
        return nodes.count()
    }

    fun part2(input: List<String>): Int {
        val grid = CharacterGrid(input)
        val antennas = antennaLocations(grid)
        val nodes = mutableSetOf<XYLocation>()
        for (frequency in antennas.keys) {
            val locationPairs = antennas[frequency]!!.combinations(2).toList()
            for (locationPair in locationPairs) {
                val antiNodes = antiNodeLocations2(locationPair, grid)
                nodes.addAll(antiNodes)
            }
        }
        nodes.forEach {  node ->
            if (grid.getCharacter(node.xy) == '.') grid.setCharacter(node.xy, '#')
        }
        return (grid.numberOfRows * grid.numberOfColumns) - grid.countCharacter('.')
    }

    // Test input from the `src/Day08_test.txt` file
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    // Input from the `src/Day08.txt` file
    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()

}
