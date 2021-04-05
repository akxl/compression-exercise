package compression

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HuffmanTest {

    @Test
    fun `test compression of 4 equally likely characters`() {
        val example = "abcd"
        val result = compress(Raw(example))
        val compressed = result.first
            .map { it.string }
            .toSet()
        assertEquals(setOf("00", "01", "10", "11"), compressed)
    }

    @Test
    fun `test compression of not equally likely characters`() {
        val example = "aaabbbcd"
        val result = compress(Raw(example))
        val compressed = result.first
            .map { it.string }
            .toSet()
        assertEquals(setOf("00", "1", "010", "011"), compressed)
    }

    @Test
    fun `test compression and decompression of 4 equally likely characters`() {
        val example = "abcd"
        val compressionResult = compress(Raw(example))
        val decompressedResult = decompress(compressionResult.first, compressionResult.second)
        assertEquals(example, decompressedResult.string)
    }

    @Test
    fun `test compression and decompression of non equally likely characters`() {
        val example = "asdkjafgjur3sd"
        val compressionResult = compress(Raw(example))
        val decompressedResult = decompress(compressionResult.first, compressionResult.second)
        assertEquals(example, decompressedResult.string)
    }

}