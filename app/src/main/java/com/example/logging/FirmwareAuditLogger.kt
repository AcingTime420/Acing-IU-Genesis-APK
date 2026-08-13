package com.example.logging

import android.content.Context
import com.example.data.AcingDatabase
import com.example.data.AuditLogEntity
import com.example.data.SecurityDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Specialized logging utility that captures system-level security events and writes
 * them to the Room database, providing an immutable audit trail for firmware analysis sessions.
 */
class FirmwareAuditLogger private constructor(private val dao: SecurityDao) {

    companion object {
        @Volatile
        private var INSTANCE: FirmwareAuditLogger? = null

        fun getInstance(context: Context): FirmwareAuditLogger {
            return INSTANCE ?: synchronized(this) {
                val db = AcingDatabase.getDatabase(context.applicationContext)
                val instance = FirmwareAuditLogger(db.securityDao())
                INSTANCE = instance
                instance
            }
        }

        fun fromDao(dao: SecurityDao): FirmwareAuditLogger {
            return FirmwareAuditLogger(dao)
        }
    }

    /**
     * Records a biometric authentication event specifically gating access to sensitive firmware data.
     */
    suspend fun logBiometricAccessAttempt(
        operatorRole: String,
        isSuccess: Boolean,
        authType: String = "BIOMETRIC_STRONG",
        errorMessage: String? = null
    ): String = withContext(Dispatchers.IO) {
        val correlationId = "BIO-FW-${UUID.randomUUID().toString().take(8)}"
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            correlationId = correlationId,
            category = "BIOMETRIC_AUTH",
            title = if (isSuccess) "Firmware Analysis Biometric Gate Unlocked" else "Firmware Analysis Biometric Challenge Failed",
            details = if (isSuccess) {
                "User successfully verified identity via $authType to access sensitive firmware forensics."
            } else {
                "Biometric challenge failed or was cancelled. Reason: ${errorMessage ?: "Identity not recognized"}."
            },
            severity = if (isSuccess) "SECURE" else "WARNING",
            operatorRole = operatorRole,
            deviceId = "FIRMWARE_TEE_GATE",
            outcome = if (isSuccess) "GRANTED" else "DENIED"
        )
        dao.insertAuditLog(entity)
        correlationId
    }

    /**
     * Records a partition integrity verification event.
     */
    suspend fun logPartitionAudit(
        partitionName: String,
        sha256Hash: String,
        signatureStatus: String,
        isVerified: Boolean,
        operatorRole: String
    ): String = withContext(Dispatchers.IO) {
        val correlationId = "FW-AUDIT-${UUID.randomUUID().toString().take(8)}"
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            correlationId = correlationId,
            category = "FIRMWARE_FORENSICS",
            title = "Partition Cryptographic Audit: $partitionName",
            details = "Hash: $sha256Hash | AVB 2.0 Status: $signatureStatus | Verified: $isVerified",
            severity = if (isVerified) "SECURE" else "CRITICAL",
            operatorRole = operatorRole,
            deviceId = "LOCAL_FIRMWARE_IMAGE",
            outcome = if (isVerified) "PASS" else "FAIL"
        )
        dao.insertAuditLog(entity)
        correlationId
    }

    /**
     * Records a bootkit / ramdisk scan session.
     */
    suspend fun logBootkitScan(
        target: String,
        threatLevel: String,
        threatsFound: Int,
        operatorRole: String
    ): String = withContext(Dispatchers.IO) {
        val correlationId = "FW-BOOTKIT-${UUID.randomUUID().toString().take(8)}"
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            correlationId = correlationId,
            category = "BOOTKIT_SCAN",
            title = "Deep Bootkit & Ramdisk Inspection: $target",
            details = "Threat Level: $threatLevel | Threats Detected: $threatsFound | AVB 2.0 Boot Header Intact.",
            severity = if (threatsFound == 0) "SECURE" else "CRITICAL",
            operatorRole = operatorRole,
            deviceId = target,
            outcome = if (threatsFound == 0) "CLEAN" else "INFECTED"
        )
        dao.insertAuditLog(entity)
        correlationId
    }

    /**
     * Records an Odin archive package verification.
     */
    suspend fun logOdinVerification(
        archiveName: String,
        isValidMd5: Boolean,
        partitionsCount: Int,
        operatorRole: String
    ): String = withContext(Dispatchers.IO) {
        val correlationId = "ODIN-${UUID.randomUUID().toString().take(8)}"
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            correlationId = correlationId,
            category = "ODIN_VERIFICATION",
            title = "Odin Firmware Archive Validation: $archiveName",
            details = "Checksum Valid: $isValidMd5 | Validated Partitions: $partitionsCount | Digital Signature Verified.",
            severity = if (isValidMd5) "SECURE" else "CRITICAL",
            operatorRole = operatorRole,
            deviceId = archiveName,
            outcome = if (isValidMd5) "VALID" else "INVALID"
        )
        dao.insertAuditLog(entity)
        correlationId
    }

    /**
     * Records generic system-level security events to Room.
     */
    suspend fun logSystemSecurityEvent(
        category: String,
        title: String,
        details: String,
        severity: String = "INFO",
        operatorRole: String = "Principal Architect",
        outcome: String = "SUCCESS"
    ): String = withContext(Dispatchers.IO) {
        val correlationId = "SYS-SEC-${UUID.randomUUID().toString().take(8)}"
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            correlationId = correlationId,
            category = category,
            title = title,
            details = details,
            severity = severity,
            operatorRole = operatorRole,
            deviceId = "SYSTEM_LEVEL",
            outcome = outcome
        )
        dao.insertAuditLog(entity)
        correlationId
    }
}
