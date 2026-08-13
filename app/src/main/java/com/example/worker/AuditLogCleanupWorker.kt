package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AcingDatabase
import com.example.data.AuditLogEntity
import java.io.File
import java.io.FileWriter

/**
 * WorkManager task that automatically clears 'AuditLogEntity' records older than 30 days
 * to prevent excessive local storage growth, while triggering a final archival export before deletion.
 */
class AuditLogCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AcingDatabase.getDatabase(applicationContext)
            val dao = db.securityDao()

            // 30 days in milliseconds
            val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000L
            val cutoffTimestamp = System.currentTimeMillis() - thirtyDaysMs

            val expiredLogs = dao.getLogsOlderThan(cutoffTimestamp)

            if (expiredLogs.isNotEmpty()) {
                // 1. Trigger final archival export before deletion
                val archiveFileName = "audit_log_archive_${System.currentTimeMillis()}.json"
                val archiveFile = File(applicationContext.filesDir, archiveFileName)

                val exportContent = buildString {
                    append("[\n")
                    expiredLogs.forEachIndexed { index, log ->
                        append("  {\n")
                        append("    \"id\": ${log.id},\n")
                        append("    \"timestamp\": ${log.timestamp},\n")
                        append("    \"category\": \"${log.category.replace("\"", "\\\"")}\",\n")
                        append("    \"title\": \"${log.title.replace("\"", "\\\"")}\",\n")
                        append("    \"details\": \"${log.details.replace("\"", "\\\"")}\",\n")
                        append("    \"severity\": \"${log.severity}\",\n")
                        append("    \"operatorRole\": \"${log.operatorRole}\",\n")
                        append("    \"deviceId\": \"${log.deviceId}\",\n")
                        append("    \"outcome\": \"${log.outcome}\"\n")
                        append("  }${if (index < expiredLogs.size - 1) "," else ""}\n")
                    }
                    append("]\n")
                }

                FileWriter(archiveFile).use { writer ->
                    writer.write(exportContent)
                }

                Log.i("AuditLogCleanupWorker", "Exported ${expiredLogs.size} expired audit logs to ${archiveFile.absolutePath}")

                // 2. Clear records older than 30 days
                val deletedCount = dao.deleteLogsOlderThan(cutoffTimestamp)

                // 3. Log a record of the cleanup action
                dao.insertAuditLog(
                    AuditLogEntity(
                        category = "STORAGE_GOVERNANCE",
                        title = "Automated 30-Day Audit Log Cleanup",
                        details = "Archived $deletedCount records to $archiveFileName and purged them from Room storage.",
                        severity = "INFO",
                        operatorRole = "WorkManager Automation",
                        outcome = "SUCCESS"
                    )
                )

                Log.i("AuditLogCleanupWorker", "Successfully cleared $deletedCount audit logs older than 30 days.")
            } else {
                Log.i("AuditLogCleanupWorker", "No audit logs older than 30 days found for cleanup.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AuditLogCleanupWorker", "Error executing audit log cleanup worker: ${e.message}", e)
            Result.failure()
        }
    }
}
