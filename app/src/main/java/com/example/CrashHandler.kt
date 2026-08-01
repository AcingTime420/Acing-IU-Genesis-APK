package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stackTrace = intent.getStringExtra("stack_trace") ?: "Unknown error"
        setContent {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item { Text(text = "CRASH: \n$stackTrace") }
            }
        }
    }
}

object CrashHandler {
    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val logFile = java.io.File(context.getExternalFilesDir(null), "crash_log.txt")
                logFile.writeText(android.util.Log.getStackTraceString(throwable))
            } catch (e: Exception) {
            }
            val intent = Intent(context, CrashActivity::class.java).apply {
                putExtra("stack_trace", android.util.Log.getStackTraceString(throwable))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}
