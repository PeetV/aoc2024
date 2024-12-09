fun main() {

    fun splitInput(input: List<String>): Pair<List<Int>, List<Int>> {
        val files = mutableListOf<Int>()
        val frees = mutableListOf<Int>()
        var isFile = true
        for (value in input.first()) {
            if (isFile) {
                files.add(value.toString().toInt())
                isFile = false
            } else {
                frees.add(value.toString().toInt())
                isFile = true
            }
        }
        return files to frees
    }

    fun part1(input: List<String>): Int {
        val (files, frees) = splitInput(input)
        val allocations = frees.map { it to mutableListOf<Int>() }
        val filesIndices = files.zip(files.indices).toMutableList()
        println(input)
        println(files)
        println(frees)
        println(allocations)
        println(filesIndices)
        var allocationsIndex = 0
        var filesIndex = filesIndices.lastIndex
        var allocationsFull = false
        while (!allocationsFull) {
            var (size, id) = filesIndices[filesIndex]
            var (capacity, buffer) = allocations[allocationsIndex]
            if (buffer.count() == capacity) {
                allocationsIndex += 1
                if (allocationsIndex > allocations.lastIndex) { break }
                val (nextCapacity, nextBuffer) = allocations[allocationsIndex]
                capacity = nextCapacity
                buffer = nextBuffer
            }
            if (size > 0) {
                buffer.add(id)
                size -= 1
                filesIndices[filesIndex] = size to id
            } else {
                filesIndex -= 1
                if (filesIndex < 0) break
            }
            allocationsFull = allocations.all { it.first == it.second.count() }
        }
        println(allocations)
        println(filesIndices)
        println(filesIndices.zip(allocations).toList())
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test input from the `src/Day09_test.txt` file
    val testInput = readInput("Day09_test")
    println(part1(testInput))
//    check(part1(testInput) == 1)

    // Input from the `src/Day09.txt` file
//    val input = readInput("Day09")
//    part1(input).println()
//    part2(input).println()

}
