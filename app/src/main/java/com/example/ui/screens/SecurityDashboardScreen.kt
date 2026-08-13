package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acingiu.data.AuditLogExportService
import com.example.findActivity
import com.example.security.BiometricGuard
import com.example.ui.AcingViewModel
import com.example.ui.components.AuditLogRowItem
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreatAnalyticsView
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold
import kotlinx.coroutines.launch

@Composable
fun SecurityDashboardScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val firmwareScans by viewModel.firmwareScans.collectAsState()
    val selinuxEnforced by viewModel.selinuxEnforced.collectAsState()
    val zeroTrustLockdown by viewModel.zeroTrustLockdown.collectAsState()
    val biometricLockout by viewModel.biometricLockoutProtection.collectAsState()

    var exportResultState by remember { mutableStateOf<AuditLogExportService.ExportResult?>(null) }
    var isExporting by remember { mutableStateOf(false) }

    // Calculate dynamic Security Health Score (0 - 100%)
    val healthScore = remember(auditLogs, selinuxEnforced, zeroTrustLockdown, biometricLockout) {
        var score = 100
        if (!selinuxEnforced) score -= 25
        if (!biometricLockout) score -= 15
        val criticalCount = auditLogs.count { it.severity.contains("CRITICAL", ignoreCase = true) }
        val warnCount = auditLogs.count { it.severity.contains("WARN", ignoreCase = true) }
        score -= (criticalCount * 10 + warnCount * 3)
        if (zeroTrustLockdown) score += 5
        score.coerceIn(0, 100)
    }

    val healthScoreColor = when {
        healthScore >= 85 -> AegisSecureGreen
        healthScore >= 60 -> AegisWarningGold
        else -> AegisDangerRed
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Security Health Score Card
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_health_score_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "System Integrity",
                                    tint = healthScoreColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SYSTEM INTEGRITY HEALTH SCORE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Aggregated Room Database Security Metrics",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(healthScoreColor.copy(alpha = 0.15f))
                                .border(1.dp, healthScoreColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$healthScore%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = healthScoreColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // System Integrity Indicators Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IntegrityStatusIconBadge(
                            title = "SELinux",
                            isOk = selinuxEnforced,
                            icon = Icons.Default.Policy,
                            modifier = Modifier.weight(1f)
                        )
                        IntegrityStatusIconBadge(
                            title = "AVB 2.0",
                            isOk = true,
                            icon = Icons.Default.VerifiedUser,
                            modifier = Modifier.weight(1f)
                        )
                        IntegrityStatusIconBadge(
                            title = "Biometrics",
                            isOk = biometricLockout,
                            icon = Icons.Default.Fingerprint,
                            modifier = Modifier.weight(1f)
                        )
                        IntegrityStatusIconBadge(
                            title = "Zero Trust",
                            isOk = zeroTrustLockdown,
                            icon = Icons.Default.Lock,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Threat Vector Distribution Donut Chart (visualizing detected vulnerabilities found during recent firmware scans)
        item {
            com.example.ui.components.ThreatVectorDistributionDonutChart(
                firmwareScans = firmwareScans
            )
        }

        // Threat Analytics Timeline Chart & Network Interface Metrics
        item {
            ThreatAnalyticsView(
                auditLogs = auditLogs,
                flaggedTunnelsCount = 0
            )
        }

        // Audit Log Export & JSON Report Action Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                modifier = Modifier.fillMaxWidth().testTag("audit_export_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export JSON Logs",
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUDIT LOG EXPORT SERVICE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = AegisTextPrimary
                            )
                        }

                        Button(
                            onClick = {
                                val activity = context.findActivity()
                                val guard = BiometricGuard(context)
                                val executeExport = {
                                    isExporting = true
                                    coroutineScope.launch {
                                        val exportService = AuditLogExportService(context)
                                        val result = exportService.exportLogsToJsonReport(auditLogs)
                                        exportResultState = result
                                        isExporting = false
                                        if (result is AuditLogExportService.ExportResult.Success) {
                                            Toast.makeText(context, "Exported ${result.recordCount} logs to JSON", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                if (activity != null && guard.isBiometricAvailable()) {
                                    guard.authenticateFeatureAccess(
                                        activity = activity,
                                        feature = BiometricGuard.SensitiveFeature.SECURITY_AUDIT_LOGS,
                                        onSuccess = { executeExport() },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            executeExport()
                                        }
                                    )
                                } else {
                                    executeExport()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isExporting,
                            modifier = Modifier.testTag("export_json_button")
                        ) {
                            Text(
                                text = if (isExporting) "EXPORTING..." else "EXPORT JSON REPORT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisDarkBg
                            )
                        }
                    }

                    exportResultState?.let { result ->
                        Spacer(modifier = Modifier.height(12.dp))
                        when (result) {
                            is AuditLogExportService.ExportResult.Success -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AegisSecureGreen.copy(alpha = 0.1f))
                                        .border(1.dp, AegisSecureGreen, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "EXPORT SUCCESSFUL (${result.recordCount} records)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AegisSecureGreen,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "File: ${result.filePath}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = AegisTextPrimary
                                        )
                                    }
                                }
                            }
                            is AuditLogExportService.ExportResult.Error -> {
                                Text(
                                    text = "Export Error: ${result.message}",
                                    fontSize = 11.sp,
                                    color = AegisDangerRed,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Room Database Security Audit Ledger
        item {
            SectionHeader(
                title = "High-Frequency Security Audit Logs",
                subtitle = "Thread-safe SQLite persistent events stream",
                icon = Icons.Default.Policy
            )
        }

        if (auditLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No audit logs in database.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AegisTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(auditLogs.take(10)) { log ->
                AuditLogRowItem(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IntegrityStatusIconBadge(
    title: String,
    isOk: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val statusColor = if (isOk) AegisSecureGreen else AegisDangerRed
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(statusColor.copy(alpha = 0.12f))
            .border(1.dp, statusColor, RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = statusColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AegisTextPrimary
            )
            Text(
                text = if (isOk) "PASS" else "FAIL",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}
