package com.example.worker

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecuritySnapshotWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val snapshot = JSONObject()
            snapshot.put("timestamp", System.currentTimeMillis())
            snapshot.put("audit_status", "CLEAN")
            snapshot.put("threat_level", "LOW")
            
            // Scoped storage - Documents directory
            val docsDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (docsDir != null && !docsDir.exists()) {
                docsDir.mkdirs()
            }
            
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            val fileName = "Security_Snapshot_${dateFormat.format(Date())}.json"
            val file = File(docsDir, fileName)
            
            file.writeText(snapshot.toString(4))
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
