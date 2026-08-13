package com.example.acingiu.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Service to export Room database security audit logs into a structured, timestamped JSON report file.
 */
class AuditLogExportService(private val context: Context) {

    sealed class ExportResult {
        data class Success(
            val filePath: String,
            val fileName: String,
            val recordCount: Int,
            val jsonPreview: String
        ) : ExportResult()

        data class Error(val message: String) : ExportResult()
    }

    suspend fun exportLogsToJsonReport(logs: List<com.example.data.AuditLogEntity>): ExportResult = withContext(Dispatchers.IO) {
        try {
            val rootObj = JSONObject()
            val timestampStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date())

            rootObj.put("reportTitle", "Acing IU Zero-Trust Security Audit Log Export")
            rootObj.put("exportTimestamp", timestampStr)
            rootObj.put("totalLogsCount", logs.size)
            rootObj.put("securityClassification", "RESTRICTED // SECOPS AUDIT REPORT")
            rootObj.put("platformVersion", "v1.3.1-GENESIS")

            val jsonLogsArray = JSONArray()
            for (log in logs) {
                val itemObj = JSONObject()
                itemObj.put("id", log.id)
                itemObj.put("category", log.category)
                itemObj.put("title", log.title)
                itemObj.put("details", log.details)
                itemObj.put("severity", log.severity)
                itemObj.put("operatorRole", log.operatorRole)
                itemObj.put("timestamp", log.timestamp)
                itemObj.put(
                    "formattedTime",
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(log.timestamp))
                )
                itemObj.put("correlationId", log.correlationId)
                itemObj.put("outcome", log.outcome)
                jsonLogsArray.put(itemObj)
            }
            rootObj.put("auditLogs", jsonLogsArray)

            val fileName = "Acing_AuditLog_Export_${System.currentTimeMillis()}.json"
            val exportDir = File(context.getExternalFilesDir(null), "security_reports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            val outputFile = File(exportDir, fileName)
            outputFile.writeText(rootObj.toString(4))

            ExportResult.Success(
                filePath = outputFile.absolutePath,
                fileName = fileName,
                recordCount = logs.size,
                jsonPreview = rootObj.toString(2)
            )
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Failed to export JSON report")
        }
    }
}
