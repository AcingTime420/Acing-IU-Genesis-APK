package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@Composable
fun DevicesScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val selinuxEnforced by viewModel.selinuxEnforced.collectAsState()
    val lockdownActive by viewModel.zeroTrustLockdown.collectAsState()
    val certPinningActive by viewModel.certPinningActive.collectAsState()
    val snapshots by viewModel.deviceSnapshots.collectAsState()
    val deviceDiagnosticResult by viewModel.deviceDiagnosticResult.collectAsState()
    val isAnalyzingDevice by viewModel.isAnalyzingDevice.collectAsState()

    val currentSnapshot = snapshots.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Device Diagnostics & Telemetry",
                subtitle = "Hardware-backed keymaster, battery & SELinux posture",
                icon = Icons.Default.PhoneAndroid
            )
        }

        if (deviceDiagnosticResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan),
                    modifier = Modifier.fillMaxWidth().testTag("ai_device_diagnostic_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AegisPrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AEGIS AI TELEMETRY DIAGNOSTIC",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisPrimaryCyan
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearDeviceDiagnostic() },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 10.sp, color = AegisTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = deviceDiagnosticResult ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_telemetry_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentSnapshot?.deviceName ?: "Pixel 9 Pro Security Target",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisTextPrimary
                        )
                        SeverityBadge(severity = if (selinuxEnforced) "SECURE" else "WARNING")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val telemetryList = listOf(
                        "Android Build" to (currentSnapshot?.androidVersion ?: "Android 15 (API 35)"),
                        "SELinux Mode" to (if (selinuxEnforced) "Enforcing (MAC Active)" else "Permissive"),
                        "AVB 2.0 Boot" to (currentSnapshot?.avbState ?: "Locked (Verified Boot)"),
                        "Hardware Keystore" to (currentSnapshot?.hardwareKeystore ?: "StrongBox TEE Keymaster"),
                        "CPU Thermal Zone" to "32.4°C (Normal)",
                        "USB Debugging (ADB)" to if (lockdownActive) "Blocked by Policy" else "Authorised"
                    )

                    telemetryList.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, fontSize = 12.sp, color = AegisTextSecondary)
                            Text(
                                text = value,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    androidx.compose.material3.Button(
                        onClick = { viewModel.analyzeDeviceWithAi() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = AegisPrimaryCyan,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        enabled = !isAnalyzingDevice,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_device_ai_diagnostic_button")
                    ) {
                        if (isAnalyzingDevice) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = androidx.compose.ui.graphics.Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyzing Telemetry...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run AI Telemetry Diagnostic", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Zero-Trust Policy Enforcement",
                subtitle = "Toggle hardware enforcement & lockdown policies",
                icon = Icons.Default.Policy
            )
        }

        item {
            PolicyToggleCard(
                title = "SELinux Mandatory Access Control",
                subtitle = "Enforces kernel security policies & blocks unauthorized process domain transitions",
                isChecked = selinuxEnforced,
                onCheckedChange = { viewModel.toggleSelinux() },
                testTag = "selinux_switch"
            )
        }

        item {
            PolicyToggleCard(
                title = "Zero-Trust Emergency Lockdown",
                subtitle = "Immediately terminates external ADB routes, USB debug bridges & non-pinned connections",
                isChecked = lockdownActive,
                onCheckedChange = { viewModel.toggleZeroTrustLockdown() },
                isDanger = true,
                testTag = "lockdown_switch"
            )
        }

        item {
            PolicyToggleCard(
                title = "Strict TLS Certificate Pinning",
                subtitle = "Enforces Aegis public key pinning for all outbound telemetry and AI services",
                isChecked = certPinningActive,
                onCheckedChange = { viewModel.toggleCertPinning() },
                testTag = "cert_pinning_switch"
            )
        }

        item {
            SectionHeader(
                title = "Application Sandbox Permission Matrix",
                subtitle = "System runtime permissions & privacy guard",
                icon = Icons.Default.Security
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val permissions = listOf(
                        "CAMERA" to "Restricted (Explicit Runtime Prompt)",
                        "RECORD_AUDIO" to "Restricted (Explicit Runtime Prompt)",
                        "ACCESS_FINE_LOCATION" to "Disabled (Zero-Trust Policy)",
                        "READ_PHONE_STATE" to "Isolated (Virtual Sandbox ID)",
                        "INTERNET" to "Protected (Pinned TLS Tunnel)"
                    )

                    permissions.forEach { (perm, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = perm,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisPrimaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = status,
                                fontSize = 11.sp,
                                color = AegisTextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PolicyToggleCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDanger: Boolean = false,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDanger && isChecked) AegisDangerRed else AegisBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDanger && isChecked) AegisDangerRed else AegisTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AegisTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AegisDarkBg,
                    checkedTrackColor = if (isDanger) AegisDangerRed else AegisPrimaryCyan,
                    uncheckedThumbColor = AegisTextSecondary,
                    uncheckedTrackColor = AegisSurfaceVariant
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
