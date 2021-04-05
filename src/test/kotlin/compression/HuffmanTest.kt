package compression

import org.junit.jupiter.api.Test

class HuffmanTest {

    @Test
    fun testCompression() {
        val example = "abcd"
        val a = compress(Raw(example))
        println(a.first)

    }

}