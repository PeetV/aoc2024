import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * An enum of directions on a grid.
 */
enum class Direction {
    North, South, West, East, NorthWest, NorthEast, SouthWest, SouthEast
}

/**
 * Read lines from the given input text file name (without including the .txt extension in the name).
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Convert a string to a md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16).padStart(32, '0')

/**
 * Shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * A grid of characters.
 */
class CharacterGrid(input: List<String>) {

    val numberOfRows: Int = input.count()
    val numberOfColumns: Int = input.first().count()
    val data = input.map { it.toCharArray().toMutableList() }

    init {
        if (!input.all { it.count() == numberOfColumns }) {
            throw IllegalArgumentException("Input rows must all be the same length to create a grid")
        }
    }

    override fun toString(): String = data.joinToString("\n") { it.joinToString("") }

    fun toStringList(): List<String> = data.map { it.joinToString("") }

    /**
     * A sequence of locations in the grid, iterating by column then row, top down.
     */
    val indices: Sequence<Pair<Int, Int>>
        get() = sequence {
            for (rowIndex in 0..<numberOfRows) {
                for (columnIndex in 0..<numberOfColumns) {
                    yield(columnIndex to rowIndex)
                }
            }
        }

    /**
     * A sequence of rows returned as strings.
     */
    val rowStrings: Sequence<String>
        get() = sequence {
            for (row in data) {
                yield(row.joinToString(""))
            }
        }

    /**
     * A sequence of columns returned as strings.
     */
    val columnStrings: Sequence<String>
        get() = sequence {
            for (columnIndex in 0..<numberOfColumns) {
                val column = (0..<numberOfRows).map { rowIndex -> data[rowIndex][columnIndex] }
                yield(column.joinToString(""))
            }
        }

    /**
     * Get the character at a location.
     */
    fun getCharacter(location: Pair<Int, Int>): Char = data[location.second][location.first]

    /**
     * Set the character at a location.
     */
    fun setCharacter(location: Pair<Int, Int>, character: Char) {
        data[location.second][location.first] = character
    }

    /**
     * Count the number of occurrences of a character.
     */
    fun countCharacter(character: Char): Int {
        var result = 0
        for ((columnIndex, rowIndex) in indices) {
            if (data[rowIndex][columnIndex] == character) {
                result += 1
            }
        }
        return result
    }

    /**
     * Find the first location of a character searching columns by row (left to right, row by row).
     */
    fun findLocation(character: Char): Pair<Int, Int>? {
        for ((columnIndex, rowIndex) in indices) {
            if (data[rowIndex][columnIndex] == character) {
                return columnIndex to rowIndex
            }
        }
        return null
    }

    /**
     * Check if a location is inside the bounds of the grid.
     */
    fun isInBounds(location: Pair<Int, Int>): Boolean {
        if (location.first < 0 || location.first > (numberOfColumns - 1)) return false
        if (location.second < 0 || location.second > (numberOfRows - 1)) return false
        return true
    }

}

/**
 * A location on a two-dimensional grid, where X is the column position and Y is the row position.
 */
data class XYLocation(var x: Int, var y: Int, var direction: Direction?) {

    /**
     * Get the x and y coordinates of the location as a 'Pair'.
     */
    var xy: Pair<Int, Int>
        get() = x to y
        set(xy) {
            x = xy.first
            y = xy.second
        }

    override fun toString(): String = "$xy"

    /**
     * Get the location one step away in the specified direction.
     */
    fun nextLocation(direction: Direction, step: Int = 1): XYLocation {
        this.direction = direction
        return when (direction) {
            Direction.North -> XYLocation(x, y - step, direction)
            Direction.South -> XYLocation(x, y + step, direction)
            Direction.West -> XYLocation(x - step, y, direction)
            Direction.East -> XYLocation(x + step, y, direction)
            Direction.NorthWest -> XYLocation(x - step, y - step, direction)
            Direction.NorthEast -> XYLocation(x + step, y - step, direction)
            Direction.SouthWest -> XYLocation(x - step, y + step, direction)
            Direction.SouthEast -> XYLocation(x + step, y + step, direction)
        }
    }

}

/**
 * Get the combinations of `length` from a list.
 *
 * Code from [reddit](https://www.reddit.com/r/Kotlin/comments/isg16h/what_is_the_fastest_way_combination_in_kotlin).
 */
fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> = sequence {
    val pool = this@combinations as? List<T> ?: toList()
    val n = pool.size
    if (length > n) return@sequence
    val indices = IntArray(length) { it }
    while (true) {
        yield(indices.map { pool[it] })
        var i = length
        do {
            i--
            if (i == -1) return@sequence
        } while (indices[i] == i + n - length)
        indices[i]++
        for (j in i + 1..<length) indices[j] = indices[j - 1] + 1
    }
}

/**
 * Get the permutations of `length` from a list.
 *
 * Code from [reddit](https://www.reddit.com/r/Kotlin/comments/isg16h/what_is_the_fastest_way_combination_in_kotlin).
 */
fun <T> Iterable<T>.permutations(length: Int? = null): Sequence<List<T>> = sequence {
    val pool = this@permutations as? List<T> ?: toList()
    val n = pool.size
    val r = length ?: n
    if (r > n) return@sequence
    val indices = IntArray(n) { it }
    val cycles = IntArray(r) { n - it }
    yield(List(r) { pool[indices[it]] })
    if (n == 0) return@sequence
    cyc@ while (true) {
        for (i in r - 1 downTo 0) {
            cycles[i]--
            if (cycles[i] == 0) {
                val temp = indices[i]
                for (j in i..<n - 1) indices[j] = indices[j + 1]
                indices[n - 1] = temp
                cycles[i] = n - i
            } else {
                val j = n - cycles[i]
                indices[i] = indices[j].also { indices[j] = indices[i] }
                yield(List(r) { pool[indices[it]] })
                continue@cyc
            }
        }
        return@sequence
    }
}
