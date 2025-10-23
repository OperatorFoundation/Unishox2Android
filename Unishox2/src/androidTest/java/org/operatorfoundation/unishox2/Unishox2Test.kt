package org.operatorfoundation.unishox2

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Unishox2Test {

    @Test
    fun testBasicCompressDecompress() {
        val original = "Hello, World!"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
        assertTrue("Compressed size should be non-zero", compressed.isNotEmpty())
    }

    @Test
    fun testEmptyString() {
        assertThrows(IllegalArgumentException::class.java) {
            Unishox2.compress("")
        }
    }

    @Test
    fun testShortString() {
        val original = "Hi"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testLongString() {
        val original = "This is a much longer string that should compress well because it contains repeated patterns and common words that Unishox2 can efficiently encode. " +
                "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog."
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
        assertTrue("Long strings should compress", compressed.size < original.toByteArray().size)
    }

    @Test
    fun testUnicodeCharacters() {
        val original = "Hello 世界 🌍 Привет مرحبا"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testSpecialCharacters() {
        val original = "!@#$%^&*()_+-=[]{}|;':\",./<>?"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testNumbers() {
        val original = "0123456789 42 3.14159 -273.15"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testWhitespace() {
        val original = "   spaces   \n\ttabs\t\nnewlines\n   "
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testRepeatedPatterns() {
        val original = "aaaaaaaaaa bbbbbbbbbb cccccccccc"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testJsonLikeString() {
        val original = """{"name":"John","age":30,"city":"New York"}"""
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testUrlString() {
        val original = "https://example.com/path/to/resource?param1=value1&param2=value2"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testMultipleCompressions() {
        val strings = listOf(
            "First string",
            "Second string with more content",
            "Third string 123",
            "Fourth string with unicode: 你好"
        )

        strings.forEach { original ->
            val compressed = Unishox2.compress(original)
            val decompressed = Unishox2.decompress(compressed)
            assertEquals(original, decompressed)
        }
    }

    @Test
    fun testInvalidCompressedData() {
        val invalidData = ByteArray(10) { 0xFF.toByte() }

        assertThrows(IllegalArgumentException::class.java) {
            Unishox2.decompress(invalidData)
        }
    }

    @Test
    fun testEmptyCompressedData() {
        assertThrows(IllegalArgumentException::class.java) {
            Unishox2.decompress(ByteArray(0))
        }
    }

    @Test
    fun testCompressionRatio() {
        val original = "The quick brown fox jumps over the lazy dog. " +
                "The quick brown fox jumps over the lazy dog. " +
                "The quick brown fox jumps over the lazy dog."
        val compressed = Unishox2.compress(original)

        val originalSize = original.toByteArray().size
        val compressedSize = compressed.size
        val ratio = compressedSize.toDouble() / originalSize.toDouble()

        println("Original: $originalSize bytes, Compressed: $compressedSize bytes, Ratio: ${"%.2f".format(ratio)}")
        assertTrue("Repeated text should compress well", ratio < 0.8)
    }

    @Test
    fun testIdempotency() {
        val original = "Testing idempotency of compression"

        val compressed1 = Unishox2.compress(original)
        val compressed2 = Unishox2.compress(original)

        assertArrayEquals("Same input should produce same output", compressed1, compressed2)
    }

    @Test
    fun testRoundTripMultipleTimes() {
        var current = "Initial string for multiple round trips"

        repeat(5) {
            val compressed = Unishox2.compress(current)
            current = Unishox2.decompress(compressed)
        }

        assertEquals("Initial string for multiple round trips", current)
    }

    @Test
    fun testMixedLanguages() {
        val original = "English, Español, Français, Deutsch, 中文, 日本語, 한국어, العربية"
        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    @Test
    fun testNewlinesAndFormatting() {
        val original = """
            Line 1
            Line 2
            Line 3
            
            Line 5 after blank
        """.trimIndent()

        val compressed = Unishox2.compress(original)
        val decompressed = Unishox2.decompress(compressed)

        assertEquals(original, decompressed)
    }

    private fun <T : Throwable> assertThrows(expectedClass: Class<T>, block: () -> Unit) {
        try {
            block()
            fail("Expected ${expectedClass.simpleName} to be thrown")
        } catch (e: Throwable) {
            if (!expectedClass.isInstance(e)) {
                fail("Expected ${expectedClass.simpleName} but got ${e.javaClass.simpleName}: ${e.message}")
            }
        }
    }
}