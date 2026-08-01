package com.example.forensics

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class LogFinding(
    val line: Int,
    val content: String,
    val patternMatched: String,
    val severity: String
)

class LogParserService {
    
    // Common malicious patterns in logs
    private val patterns = mapOf(
        "Su Binary Detected" to Regex(".*su\\s+-c.*|.*\\/system\\/xbin\\/su.*", RegexOption.IGNORE_CASE),
        "Unauthorized Shell Access" to Regex(".*sh\\s+-i.*|.*bash\\s+-i.*"),
        "Privilege Escalation Attempt" to Regex(".*avc:\\s+denied.*scontext=u:r:untrusted_app.*tcontext=u:r:su.*"),
        "Suspicious Mount" to Regex(".*mount\\s+-o\\s+remount,rw\\s+\\/system.*"),
        "SELinux Disabled" to Regex(".*selinux=0.*|.*enforcing=0.*")
    )

    suspend fun parseLogFile(context: Context, uri: Uri): List<LogFinding> = withContext(Dispatchers.IO) {
        val findings = mutableListOf<LogFinding>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var lineNumber = 1
                    var line = reader.readLine()
                    while (line != null) {
                        for ((threat, regex) in patterns) {
                            if (regex.containsMatchIn(line)) {
                                findings.add(
                                    LogFinding(
                                        line = lineNumber,
                                        content = line,
                                        patternMatched = threat,
                                        severity = if (threat == "SELinux Disabled" || threat == "Su Binary Detected") "CRITICAL" else "HIGH"
                                    )
                                )
                                break // one finding per line is enough
                            }
                        }
                        lineNumber++
                        line = reader.readLine()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        findings
    }
}
