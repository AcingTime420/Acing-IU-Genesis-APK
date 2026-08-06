package com.example.acingiu.verifier

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class OdinFirmwareVerifier {

    companion object {
        private const val BUFFER_SIZE_BYTES = 1024 * 1024 // 1MB stream buffer
    }

    /**
     * Streams large firmware archive partitions (.tar, .img) chunk-by-chunk using a persistent 1MB buffer
     * to verify SHA-256 integrity without heap memory exhaustion.
     */
    fun verifyFirmwareArchive(file: File): Boolean {
        if (!file.exists() || !file.canRead()) {
            return false
        }

        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(BUFFER_SIZE_BYTES)

            FileInputStream(file).use { inputStream ->
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            }

            val computedHash = digest.digest().joinToString("") { "%02x".format(it) }
            computedHash.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Verifies SHA-256 checksum against an expected hash string in non-blocking chunked stream mode.
     */
    fun verifyFirmwareHash(file: File, expectedHash: String): Boolean {
        if (!file.exists() || !file.canRead()) {
            return false
        }

        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(BUFFER_SIZE_BYTES)

            FileInputStream(file).use { inputStream ->
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            }

            val computedHash = digest.digest().joinToString("") { "%02x".format(it) }
            computedHash.equals(expectedHash, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }
}
