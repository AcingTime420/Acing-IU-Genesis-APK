package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AcingDatabase
import com.example.data.AuditLogEntity
import java.io.File

/**
 * WorkManager worker that automatically clears stale firmware analysis cache files,
 * temporary Odin archives, and temporary AI interaction logs when the device is charging,
 * optimizing storage for the zero-trust security platform.
 */
class FirmwareCacheCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val cacheDir = applicationContext.cacheDir
            val filesDir = applicationContext.filesDir
            var deletedFilesCount = 0
            var reclaimedBytes = 0L

            // 1. Scan and delete temporary firmware analysis cache files & AI interaction logs
            val targetDirs = listOfNotNull(cacheDir, filesDir)
            val staleCutoff = System.currentTimeMillis() - (12 * 60 * 60 * 1000L) // 12 hours

            targetDirs.forEach { dir ->
                if (dir.exists() && dir.isDirectory) {
                    val matchingFiles = dir.listFiles { file ->
                        val name = file.name.lowercase()
                        (name.startsWith("firmware_") ||
                         name.startsWith("odin_temp_") ||
                         name.startsWith("ai_interaction_") ||
                         name.startsWith("temp_ai_") ||
                         name.startsWith("partition_cache_") ||
                         name.endsWith(".tmp") ||
                         name.endsWith(".tar.md5.tmp")) &&
                         (file.lastModified() < staleCutoff || name.endsWith(".tmp"))
                    }

                    matchingFiles?.forEach { file ->
                        val size = file.length()
                        if (file.delete()) {
                            deletedFilesCount++
                            reclaimedBytes += size
                        }
                    }
                }
            }

            // 2. Log system-level audit record into Room Database
            val db = AcingDatabase.getDatabase(applicationContext)
            val dao = db.securityDao()

            val detailsMsg = if (deletedFilesCount > 0) {
                "Purged $deletedFilesCount stale firmware cache and AI interaction log file(s). Reclaimed ${reclaimedBytes / 1024} KB of storage."
            } else {
                "Storage optimization complete. No stale firmware cache or temporary AI interaction logs required purging."
            }

            dao.insertAuditLog(
                AuditLogEntity(
                    category = "STORAGE_OPTIMIZATION",
                    title = "Firmware Cache & AI Log Storage Optimization",
                    details = detailsMsg,
                    severity = "INFO",
                    operatorRole = "WorkManager Automation (Charging Trigger)",
                    outcome = "SUCCESS"
                )
            )

            Log.i("FirmwareCacheCleanupWorker", detailsMsg)
            Result.success()
        } catch (e: Exception) {
            Log.e("FirmwareCacheCleanupWorker", "Error executing firmware cache cleanup: ${e.message}", e)
            Result.failure()
        }
    }
}
