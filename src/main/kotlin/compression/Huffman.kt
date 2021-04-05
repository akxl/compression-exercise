package compression

import java.lang.StringBuilder
import kotlin.Comparator

// https://cs.stanford.edu/people/eroberts/courses/soco/projects/data-compression/lossless/huffman/index.htm

private const val EXAMPLE_STRING = "abcd"

inline class Compressed(val string: String)
inline class Raw(val string: String)

private class Node(
    var compressed: Compressed?,
    var raw: Raw?,
    var frequency: Int,
    var left: Node?,
    var right: Node?,
    var parent: Node?
)


fun compress(rawString: Raw): Pair<List<Compressed>, Map<Compressed, Raw>> {
    val countOfAllChars = rawString.string
        .toCharArray()
        .toList()
        .groupingBy { it }
        .eachCount()

    val leafNodesInProgress = countOfAllChars
        .map { Node(
            compressed = null,
            raw = Raw(it.key.toString()),
            frequency = it.value,
            left = null,
            right = null,
            parent = null
        )}.toSet()

    val workingCopy = leafNodesInProgress.toMutableSet()

    var nodeWithMinimumFrequency: Node
    var nodeWithNextMinimumFrequency: Node
    var totalFrequency: Int
    var combinedNode: Node

    while (workingCopy.size > 1) {
        // Get the least frequent two nodes
        nodeWithMinimumFrequency = workingCopy.minWithOrNull(
            Comparator.comparingInt { it.frequency }
        )!!
        workingCopy.remove(nodeWithMinimumFrequency)

        nodeWithNextMinimumFrequency = workingCopy.minWithOrNull(
            Comparator.comparingInt { it.frequency }
        )!!
        workingCopy.remove(nodeWithNextMinimumFrequency)

        // Combine the two nodes and add it to the working set
        totalFrequency = nodeWithMinimumFrequency.frequency + nodeWithNextMinimumFrequency.frequency
        combinedNode = Node(
            compressed = null, raw = null, frequency = totalFrequency, left = nodeWithNextMinimumFrequency,
            right = nodeWithMinimumFrequency, parent = null
        )
        workingCopy.add(combinedNode)

        // Assign the parent to the two nodes
        nodeWithMinimumFrequency.parent = combinedNode
        nodeWithNextMinimumFrequency.parent = combinedNode
    }

    // Once we have one node left, assign the compressed string
    val rootNode = workingCopy.first()
    rootNode.left?.let { assignCompressedString(it, "",true) }
    rootNode.right?.let { assignCompressedString(it, "", false) }

    // get Map of Raw string to Compresses string
    val rawToCompressedMap = leafNodesInProgress
        .map { it.raw!!.string to it.compressed!!.string }.toMap()

    val compressed = rawString.string.toCharArray()
        .map { char -> rawToCompressedMap[char.toString()] }
        .map { Compressed(it!!) }

    val compressedToMap = leafNodesInProgress.map { it.compressed!! to it.raw!! }.toMap()

    return compressed to compressedToMap

}

private fun assignCompressedString(node: Node, workingCompressedString: String, isLeft: Boolean) {
    val newWorkingCompressedString = if (isLeft) {
        workingCompressedString + "0"
    } else {
        workingCompressedString + "1"
    }

    if (node.left == null && node.right == null) {
        node.compressed = Compressed(newWorkingCompressedString)
        return
    }

    node.left?.let { assignCompressedString(it, newWorkingCompressedString, true) }
    node.right?.let { assignCompressedString(it, newWorkingCompressedString, false) }

}

fun decompress(compressed: List<Compressed>, compressedToRaw: Map<Compressed, Raw>): Raw {
    val stringBuilder = StringBuilder()
    val op: (StringBuilder, Raw) -> StringBuilder = {sb, r -> sb.append(r.string)}
    return compressed
        .map { compressedToRaw[it]!! }
        .fold(stringBuilder, op)
        .toString()
        .let { Raw(it) }
}