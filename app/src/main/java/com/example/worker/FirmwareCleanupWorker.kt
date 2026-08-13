package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.SecurityAuditDatabase
import com.example.data.SecurityAuditEventEntity
import java.io.File

/**
 * WorkManager task that deletes files in the internal cache directory with a '.temp' extension
 * whenever the device enters a charging state to optimize storage for the firmware analysis suite.
 */
class FirmwareCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val cacheDir = applicationContext.cacheDir
            var deletedCount = 0
            var reclaimedBytes = 0L

            fun cleanTempFiles(dir: File) {
                if (!dir.exists() || !dir.isDirectory) return
                val files = dir.listFiles() ?: return
                for (file in files) {
                    if (file.isDirectory) {
                        cleanTempFiles(file)
                    } else if (file.name.endsWith(".temp", ignoreCase = true) || file.name.endsWith(".tmp", ignoreCase = true)) {
                        val size = file.length()
                        if (file.delete()) {
                            deletedCount++
                            reclaimedBytes += size
                        }
                    }
                }
            }

            cleanTempFiles(cacheDir)

            // Write an audit log entry to the SecurityAuditDatabase
            val auditDb = SecurityAuditDatabase.getDatabase(applicationContext)
            val auditMessage = if (deletedCount > 0) {
                "Storage optimization completed while charging: Purged $deletedCount '.temp' cache file(s), reclaiming ${reclaimedBytes / 1024} KB."
            } else {
                "Storage optimization routine completed while charging: Internal cache directory verified clean (0 '.temp' files found)."
            }

            auditDb.securityAuditDao().insertAuditEvent(
                SecurityAuditEventEntity(
                    timestamp = System.currentTimeMillis(),
                    security_level = "STORAGE_OPTIMIZATION",
                    message = auditMessage
                )
            )

            Log.i("FirmwareCleanupWorker", auditMessage)
            Result.success()
        } catch (e: Exception) {
            Log.e("FirmwareCleanupWorker", "Error executing FirmwareCleanupWorker: ${e.message}", e)
            Result.failure()
        }
    }
}
