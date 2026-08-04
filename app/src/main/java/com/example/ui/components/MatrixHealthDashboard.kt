package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.TestModeManager
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTerminalBorder
import com.example.ui.theme.AegisTerminalGreen
import com.example.ui.theme.AegisTerminalTextPrimary
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

data class MatrixPolicyItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val isEnabled: Boolean,
    val statusLabel: String
)

/**
 * Matrix Health Dashboard visualizing security policy states (USB control, 2G/3G lock, Auto-reboot, FRP policy, Matrix Sync)
 * with interactive toggles, Simulation Mode override, and Acing Audit log stream.
 */
@Composable
fun MatrixHealthDashboard(
    onTriggerAcingAudit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSimulationMode by TestModeManager.isSimulationModeFlow.collectAsState()
    val simulationLogs by TestModeManager.simulationLogs.collectAsState()

    var showAuditLogs by remember { mutableStateOf(false) }

    var usbControlActive by remember { mutableStateOf(true) }
    var radioLockdownActive by remember { mutableStateOf(true) }
    var autoRebootActive by remember { mutableStateOf(true) }
    var matrixSyncActive by remember { mutableStateOf(true) }
    var frpProtectionActive by remember { mutableStateOf(true) }

    val policies = remember(usbControlActive, radioLockdownActive, autoRebootActive, matrixSyncActive, frpProtectionActive) {
        listOf(
            MatrixPolicyItem(
                id = "usb_control",
                title = "USB-C Port Control",
                icon = Icons.Default.Usb,
                description = "Blocks USB data exfiltration; restricts physical connection to charging mode.",
                isEnabled = usbControlActive,
                statusLabel = if (usbControlActive) "CHARGE ONLY (SECURE)" else "DATA+CHARGE (UNRESTRICTED)"
            ),
            MatrixPolicyItem(
                id = "radio_lock",
                title = "2G/3G Cellular Lockdown",
                icon = Icons.Default.CellTower,
                description = "Disables legacy cellular protocols to prevent Stingray IMSI-catcher interception.",
                isEnabled = radioLockdownActive,
                statusLabel = if (radioLockdownActive) "4G/5G/6G ONLY" else "LEGACY RADIOS ALLOWED"
            ),
            MatrixPolicyItem(
                id = "auto_reboot",
                title = "Data-at-Rest Auto-Reboot",
                icon = Icons.Default.Security,
                description = "Automatically reboots device after 12 hours of inactivity to purge BFU keys.",
                isEnabled = autoRebootActive,
                statusLabel = if (autoRebootActive) "12H TIMER ACTIVE" else "DISABLED"
            ),
            MatrixPolicyItem(
                id = "matrix_sync",
                title = "Acing Matrix Consensus Sync",
                icon = Icons.Default.Sync,
                description = "Synchronizes security credentials across trusted dApp nodes on local mesh.",
                isEnabled = matrixSyncActive,
                statusLabel = if (matrixSyncActive) "3/3 NODES VERIFIED" else "OFFLINE"
            ),
            MatrixPolicyItem(
                id = "frp_policy",
                title = "Owner-Authorized FRP Reset",
                icon = Icons.Default.Lock,
                description = "Persistent data block wipe enabled via authenticated biometric fingerprint trigger.",
                isEnabled = frpProtectionActive,
                statusLabel = if (frpProtectionActive) "PERSISTENT BLOCK READY" else "LOCKED"
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("matrix_health_dashboard"),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Matrix Health",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Acing Matrix Health Dashboard",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "Genesis Security Policy Matrix • S25 Ultra Baseline",
                            fontSize = 12.sp,
                            color = AegisTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulation Mode Switch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AegisDarkBg),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Test Mode",
                            tint = if (isSimulationMode) AegisWarningGold else AegisPrimaryCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Simulation Mode (Dry Run)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AegisTextPrimary
                            )
                            Text(
                                text = if (isSimulationMode)
                                    "Safely logs partition & hardware writes without risking data loss."
                                else
                                    "LIVE MODE: Executes real system partition calls.",
                                fontSize = 11.sp,
                                color = AegisTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isSimulationMode,
                        onCheckedChange = { TestModeManager.isSimulationMode = it },
                        modifier = Modifier.testTag("simulation_mode_toggle"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AegisWarningGold,
                            checkedTrackColor = AegisWarningGold.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Active Security Policy Toggles",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AegisTextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // List of Policy Toggles
            policies.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(AegisSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (item.isEnabled) AegisSecureGreen.copy(alpha = 0.15f) else AegisDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (item.isEnabled) AegisSecureGreen else AegisTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AegisTextPrimary
                            )
                            Text(
                                text = item.statusLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isEnabled) AegisSecureGreen else AegisWarningGold
                            )
                            Text(
                                text = item.description,
                                fontSize = 10.sp,
                                color = AegisTextMuted,
                                maxLines = 2
                            )
                        }
                    }

                    Switch(
                        checked = item.isEnabled,
                        onCheckedChange = { enabled ->
                            when (item.id) {
                                "usb_control" -> usbControlActive = enabled
                                "radio_lock" -> radioLockdownActive = enabled
                                "auto_reboot" -> autoRebootActive = enabled
                                "matrix_sync" -> matrixSyncActive = enabled
                                "frp_policy" -> frpProtectionActive = enabled
                            }
                            TestModeManager.logAction(
                                "MatrixPolicy",
                                "${item.title} state updated to: $enabled",
                                isLiveExecuted = !isSimulationMode
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AegisSecureGreen,
                            checkedTrackColor = AegisSecureGreen.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        showAuditLogs = !showAuditLogs
                        if (showAuditLogs) {
                            onTriggerAcingAudit()
                            TestModeManager.logAction(
                                "AcingAudit",
                                "Acing Matrix Audit log stream initiated",
                                isLiveExecuted = false
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trigger_acing_audit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "Audit",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showAuditLogs) "Hide Audit Logs" else "Trigger Acing Audit",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expandable Audit Terminal Log View
            AnimatedVisibility(visible = showAuditLogs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(AegisTerminalBg, RoundedCornerShape(12.dp))
                        .border(1.dp, AegisTerminalBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Acing Matrix Real-Time Audit Log Stream",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTerminalTextPrimary
                        )
                        OutlinedButton(
                            onClick = { TestModeManager.clearLogs() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear", fontSize = 10.sp, color = AegisTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (simulationLogs.isEmpty()) {
                        Text(
                            text = "[Audit Stream Active] No recent audit events recorded. Interact with policy toggles above to generate telemetry.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = AegisTextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            items(simulationLogs.reversed()) { log ->
                                Text(
                                    text = log,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (log.contains("[SIMULATION MODE]")) AegisWarningGold else AegisTerminalGreen,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
