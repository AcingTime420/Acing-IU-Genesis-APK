package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLogEntity
import com.example.ui.AcingViewModel
import com.example.ui.components.AuditLogRowItem
import com.example.ui.components.SectionHeader
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun ForensicsScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val auditLogs by viewModel.auditLogs.collectAsState()
    val isAnalyzingLog by viewModel.isAnalyzingLog.collectAsState()
    val logAnalysisResult by viewModel.logAnalysisResult.collectAsState()

    var customLogInput by remember { mutableStateOf("") }

    val presetLogcatEntries = listOf(
        "[CRITICAL] Buffer overflow attempt in libbinder.so process ID 1420 (ioctl 0xc0046201)",
        "[WARNING] Unsanitized Intent broadcast received from untrusted package com.unknown.app",
        "[SECURE] Keymaster hardware attestation token generated for RSA key alias 'aegis_master_key'"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Digital Forensics & Artifact Analysis",
                subtitle = "Logcat security analyzer & AI threat triage co-pilot",
                icon = Icons.Default.BugReport
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("logcat_analyzer_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOGCAT THREAT TRIAGE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                    Text(
                        text = "Select a log entry or paste raw Logcat output to run Gemini AI threat analysis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    presetLogcatEntries.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(AegisDarkBg, RoundedCornerShape(6.dp))
                                .border(1.dp, AegisBorder, RoundedCornerShape(6.dp))
                                .clickable { customLogInput = preset }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customLogInput,
                        onValueChange = { customLogInput = it },
                        label = { Text("Raw Logcat Snippet / Security Trace") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("log_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AegisPrimaryCyan,
                            unfocusedBorderColor = AegisBorder,
                            focusedTextColor = AegisTextPrimary,
                            unfocusedTextColor = AegisTextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (customLogInput.isNotBlank()) {
                                    viewModel.analyzeLogSnippet(customLogInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = androidx.compose.ui.graphics.Color.White),
                            enabled = customLogInput.isNotBlank() && !isAnalyzingLog,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("analyze_log_button")
                        ) {
                            if (isAnalyzingLog) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyzing...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze with Aegis AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (logAnalysisResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisSecureGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_analysis_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = AegisSecureGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AEGIS AI FORENSIC FINDINGS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisSecureGreen
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearLogAnalysis() },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 10.sp, color = AegisTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = logAnalysisResult ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Cryptographic Audit Ledger",
                    subtitle = "Immutable event database (${auditLogs.size} records)",
                    icon = Icons.Default.Description
                )

                OutlinedButton(
                    onClick = { viewModel.clearAuditLogs() },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = AegisTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", fontSize = 10.sp, color = AegisTextSecondary)
                }
            }
        }

        if (auditLogs.isEmpty()) {
            item {
                Text(
                    text = "Audit log ledger is currently empty.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AegisTextSecondary
                )
            }
        } else {
            items(auditLogs) { log ->
                AuditLogRowItem(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
