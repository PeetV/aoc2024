import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * An enum of directions on a grid.
 */
enum class Direction {
    Up, Down, Left, Right // UpLeft, UpRight, DownLeft, DownRight
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

    override fun toString(): String = data.joinToString("\n") { it.joinToString("") }

    fun toStringList(): List<String> = data.map { it.joinToString("") }

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