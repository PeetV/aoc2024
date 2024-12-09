fun main() {

    fun splitInput(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
        var isFile = true
        val files = mutableListOf<Int>()
        val gaps = mutableListOf<Int>()
        for (item in input.first().map { it.toString().toInt() }) {
            if (isFile) {
                files.add(item)
                isFile = false
            } else {
                gaps.add(item)
                isFile = true
            }
        }
        return files.toMutableList() to gaps.toMutableList()
    }

    fun part1(input: List<String>): Long {
        val (files, gaps) = splitInput(input)
        val filesWithIndex = files.indices.zip(files).toMutableList()
        var result = 0L
        var index = 0
        while (filesWithIndex.isNotEmpty()) {
            val current = filesWithIndex.removeFirst()
            for (counter in 0..<(current.second)) {
                result += index * current.first
                index += 1
            }
            if (filesWithIndex.isEmpty()) break
            var gapSize = gaps.removeFirst()
            val gapBuffer = mutableListOf<Int>()
            while (gapSize > 0) {
                val last = filesWithIndex.last()
                if (last.second > 0 && gapSize > 0) {
                    gapBuffer.add(last.first)
                    filesWithIndex[filesWithIndex.lastIndex] = last.first to (last.second - 1)
                    gapSize -= 1
                }
                if (filesWithIndex.last().second == 0) filesWithIndex.removeLast()
            }
            for (filler in gapBuffer) {
                result += index * filler
                index += 1
            }
        }
        return result
    }

    fun part2(input: List<String>): Long {
        val (files, gaps) = splitInput(input)
        val filesWithIndex = files.indices.zip(files).toMutableList()
        val gapsWithBuffer = gaps.map { it to mutableListOf<Int>() }.toMutableList()
        val filesMoved = mutableListOf<Int>()
        for (fileWithIndex in filesWithIndex.reversed()) {
            val indexedGaps = gapsWithBuffer.withIndex().toList()
            val available = indexedGaps.firstOrNull { (_, gap) -> fileWithIndex.second <= gap.first }?.index
            if (available != null) {
                val gap = indexedGaps[available].value
                val buffer = gap.second
                var added = 0
                (0..<(fileWithIndex.second)).forEach {
                    buffer.add(fileWithIndex.first)
                    added += 1
                }
                gapsWithBuffer[available] = (gap.first - added) to buffer
                filesMoved.add(fileWithIndex.first)
            }
        }
        println(filesWithIndex)
        println(filesMoved)
        val finalGaps = gapsWithBuffer.toMutableList()
        println(finalGaps)
        var result = 0L
        var index = 0L
        for (file in filesWithIndex) {
            val (fileIndex, fileCount) = if (filesMoved.contains(file.first)) 0 to file.second else file
            (0..<fileCount).forEach {
                println("index $index x fileIndex $fileIndex + result $result")
                result += index * fileIndex
                index += 1
            }
            if (finalGaps.isNotEmpty()) {
                val buffer = finalGaps.removeFirst()
                if (buffer.second.count() == 0) index += 1
                for (item in buffer.second) {
                    println("index $index x item $item + result $result")
                    result += index * item
                    index += 1
                }
            }
        }
        return result
    }

    // Test input from the `src/Day09_test.txt` file
    val testInput = readInput("Day09_test")
//    check(part1(testInput) == 1928L)
    println(part2(testInput))

    // Input from the `src/Day09.txt` file
    val input = readInput("Day09")
//    part1(input).println()
//    part2(input).println()

}
