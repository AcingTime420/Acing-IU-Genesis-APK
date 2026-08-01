package com.example.firmware

import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FirmwareMetadata(
    val fileName: String,
    val size: Long,
    val sha256Hash: String,
    val partitionInfo: String,
    val magicHeader: String,
    val severityLevel: String
)

class FirmwareScanner {
    
    suspend fun scanFirmwareFile(file: File): FirmwareMetadata = withContext(Dispatchers.IO) {
        val bytes = file.readBytes()
        val hash = calculateSHA256(bytes)
        val magic = extractMagicHeader(bytes)
        val partition = guessPartition(file.name)
        val severity = analyzeSeverity(magic, partition)

        FirmwareMetadata(
            fileName = file.name,
            size = file.length(),
            sha256Hash = hash,
            partitionInfo = partition,
            magicHeader = magic,
            severityLevel = severity
        )
    }

    private fun calculateSHA256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun extractMagicHeader(bytes: ByteArray): String {
        if (bytes.size < 4) return "UNKNOWN"
        return bytes.take(4).joinToString("") { "%02x".format(it) }
    }

    private fun guessPartition(fileName: String): String {
        val lower = fileName.lowercase()
        return when {
            lower.contains("boot") -> "boot"
            lower.contains("system") -> "system"
            lower.contains("vendor") -> "vendor"
            lower.contains("vbmeta") -> "vbmeta"
            lower.contains("recovery") -> "recovery"
            else -> "unknown_partition"
        }
    }

    private fun analyzeSeverity(magic: String, partition: String): String {
        return if (partition == "boot" || partition == "vbmeta") "HIGH"
        else if (partition == "system" || partition == "vendor") "MEDIUM"
        else "LOW"
    }
}
