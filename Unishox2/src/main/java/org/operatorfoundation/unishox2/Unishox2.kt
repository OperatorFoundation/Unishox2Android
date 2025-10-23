package org.operatorfoundation.unishox2

/**
 * Kotlin bindings for the Unishox2 text compression library.
 * 
 * Unishox2 is a compression algorithm optimized for short Unicode strings.
 */
object Unishox2 {
    init {
        System.loadLibrary("unishox2_jni")
    }

    /**
     * Compresses the given text using Unishox2 algorithm.
     * 
     * @param text The string to compress
     * @return The compressed data as a byte array
     * @throws IllegalArgumentException if text is empty
     */
    external fun compress(text: String): ByteArray

    /**
     * Decompresses data that was compressed with Unishox2.
     * 
     * @param compressed The compressed byte array
     * @return The decompressed string
     * @throws IllegalArgumentException if compressed data is invalid or empty
     */
    external fun decompress(compressed: ByteArray): String
}
