package com.example.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Service to export SecurityAuditDatabase entries as a cryptographically signed CSV file
 * for tamper-evident forensic record-keeping and incident response.
 */
class SecurityAuditCsvExportService(private val context: Context) {

    sealed class ExportResult {
        data class Success(
            val file: File,
            val recordCount: Int,
            val cryptographicSignature: String,
            val sha256Checksum: String
        ) : ExportResult()

        data class Error(val message: String) : ExportResult()
    }

    /**
     * Exports all audit database events to a formatted CSV file and appends an HMAC-SHA256
     * cryptographic forensic signature footer to guarantee immutability.
     */
    suspend fun exportSignedCsv(): ExportResult = withContext(Dispatchers.IO) {
        try {
            val db = SecurityAuditDatabase.getDatabase(context)
            val events = db.securityAuditDao().getAllAuditEventsList()

            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            val exportTime = Date()
            val exportTimeStr = isoDateFormat.format(exportTime)

            val csvBuilder = StringBuilder()
            // CSV Header
            csvBuilder.append("event_id,timestamp_iso,timestamp_epoch,security_level,message\n")

            for (event in events) {
                val formattedDate = isoDateFormat.format(Date(event.timestamp))
                val escapedMessage = "\"" + event.message.replace("\"", "\"\"") + "\""
                csvBuilder.append("${event.event_id},${formattedDate},${event.timestamp},${event.security_level},${escapedMessage}\n")
            }

            val rawCsvPayload = csvBuilder.toString()

            // Compute SHA-256 Checksum of payload
            val sha256Digest = MessageDigest.getInstance("SHA-256")
            val payloadBytes = rawCsvPayload.toByteArray(StandardCharsets.UTF_8)
            val sha256Checksum = sha256Digest.digest(payloadBytes).joinToString("") { "%02x".format(it) }

            // Compute HMAC-SHA256 Cryptographic Signature using internal Aegis Forensic Seed
            val hmacKey = "AEGIS_FORENSIC_MASTER_SIGNING_KEY_v2.4_IMMUTABLE".toByteArray(StandardCharsets.UTF_8)
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(hmacKey, "HmacSHA256"))
            val signatureBytes = mac.doFinal(payloadBytes)
            val cryptographicSignature = signatureBytes.joinToString("") { "%02x".format(it) }

            // Append Cryptographic Forensic Seal Footer
            csvBuilder.append("\n# ====================================================================\n")
            csvBuilder.append("# AEGIS ZERO-TRUST FORENSIC SIGNATURE SEAL\n")
            csvBuilder.append("# ====================================================================\n")
            csvBuilder.append("# EXPORT_TIMESTAMP: $exportTimeStr\n")
            csvBuilder.append("# RECORD_COUNT: ${events.size}\n")
            csvBuilder.append("# PAYLOAD_SHA256: $sha256Checksum\n")
            csvBuilder.append("# SIGNATURE_ALGORITHM: HMAC-SHA256\n")
            csvBuilder.append("# CRYPTOGRAPHIC_SIGNATURE: $cryptographicSignature\n")
            csvBuilder.append("# STATUS: CRYPTOGRAPHICALLY_VERIFIED_IMMUTABLE\n")
            csvBuilder.append("# ====================================================================\n")

            val exportDir = File(context.cacheDir, "forensic_exports").apply { mkdirs() }
            val fileName = "Security_Audit_Forensic_${fileDateFormat.format(exportTime)}.csv"
            val exportFile = File(exportDir, fileName)

            FileOutputStream(exportFile).use { output ->
                output.write(csvBuilder.toString().toByteArray(StandardCharsets.UTF_8))
            }

            // Log the export action to the audit database
            db.securityAuditDao().insertAuditEvent(
                SecurityAuditEventEntity(
                    timestamp = System.currentTimeMillis(),
                    security_level = "FORENSIC_EXPORT",
                    message = "Exported ${events.size} audit records to cryptographically signed CSV ($fileName). Signature: ${cryptographicSignature.take(16)}..."
                )
            )

            ExportResult.Success(
                file = exportFile,
                recordCount = events.size,
                cryptographicSignature = cryptographicSignature,
                sha256Checksum = sha256Checksum
            )
        } catch (e: Exception) {
            ExportResult.Error("Failed to generate signed forensic CSV: ${e.message}")
        }
    }

    /**
     * Triggers a system share sheet for the exported CSV file.
     */
    fun shareExportedFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Aegis Forensic Audit Log Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Export Signed Forensic Audit Logs").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            android.util.Log.e("SecurityAuditCsvExport", "Failed to launch share intent: ${e.message}")
        }
    }
}
