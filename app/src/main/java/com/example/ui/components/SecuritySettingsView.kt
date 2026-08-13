package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.SecurityAuditCsvExportService
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisWarningGold
import kotlinx.coroutines.launch

@Composable
fun SecuritySettingsView(
    biometricLockoutProtection: Boolean,
    onBiometricLockoutChange: (Boolean) -> Unit,
    highSensitivityMode: Boolean,
    onHighSensitivityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportService = remember(context) { SecurityAuditCsvExportService(context) }

    var isExporting by remember { mutableStateOf(false) }
    var exportStatusMessage by remember { mutableStateOf<String?>(null) }
    var lastExportResult by remember { mutableStateOf<SecurityAuditCsvExportService.ExportResult?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisDarkBg),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security Settings",
                    tint = AegisPrimaryCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ADVANCED SECURITY SETTINGS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = AegisPrimaryCyan
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Biometric Lockout Protection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Biometric Lockout Protection",
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )
                    Text(
                        text = "Require pin/password after multiple failed biometric attempts.",
                        fontSize = 12.sp,
                        color = AegisTextSecondary,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = biometricLockoutProtection,
                    onCheckedChange = onBiometricLockoutChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AegisDarkBg,
                        checkedTrackColor = AegisPrimaryCyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = AegisPrimaryCyan.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // High-Sensitivity Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "High-Sensitivity Mode",
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )
                    Text(
                        text = "Increase strictness of device anomaly detection and trust scoring.",
                        fontSize = 12.sp,
                        color = AegisTextSecondary,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = highSensitivityMode,
                    onCheckedChange = onHighSensitivityChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AegisDarkBg,
                        checkedTrackColor = AegisPrimaryCyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = AegisPrimaryCyan.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // Forensic Security Audit Log Export Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FORENSIC RECORD-KEEPING",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AegisPrimaryCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Export the SecurityAuditDatabase as an immutable, cryptographically signed CSV file with SHA-256 HMAC integrity seals.",
                    fontSize = 12.sp,
                    color = AegisTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isExporting = true
                        exportStatusMessage = null
                        scope.launch {
                            val res = exportService.exportSignedCsv()
                            lastExportResult = res
                            isExporting = false
                            when (res) {
                                is SecurityAuditCsvExportService.ExportResult.Success -> {
                                    exportStatusMessage = "Exported ${res.recordCount} records. Signature: ${res.cryptographicSignature.take(12)}..."
                                    exportService.shareExportedFile(res.file)
                                }
                                is SecurityAuditCsvExportService.ExportResult.Error -> {
                                    exportStatusMessage = res.message
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AegisPrimaryCyan,
                        contentColor = AegisDarkBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isExporting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("export_logs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export Logs",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isExporting) "SIGNING & EXPORTING..." else "EXPORT LOGS (SIGNED CSV)",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                if (exportStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val isErr = lastExportResult is SecurityAuditCsvExportService.ExportResult.Error
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isErr) AegisDangerRed.copy(alpha = 0.1f) else AegisSecureGreen.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isErr) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isErr) AegisDangerRed else AegisSecureGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = exportStatusMessage ?: "",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isErr) AegisDangerRed else AegisSecureGreen
                        )
                    }
                }
            }
        }
    }
}
