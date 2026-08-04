package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuditLogEntity
import com.example.ui.AcingViewModel
import com.example.ui.AppTab
import com.example.ui.components.AuditLogRowItem
import com.example.ui.components.DeviceTrustDashboardComponent
import com.example.ui.components.SectionHeader
import com.example.ui.components.SeverityBadge
import com.example.ui.components.StatusCard
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgePurpleBg
import com.example.ui.theme.AegisBadgePurpleText
import com.example.ui.theme.AegisBadgeRedBg
import com.example.ui.theme.AegisBadgeRedText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTerminalBorder
import com.example.ui.theme.AegisTerminalGreen
import com.example.ui.theme.AegisTerminalPurple
import com.example.ui.theme.AegisTerminalRed
import com.example.ui.theme.AegisTerminalTextPrimary
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@Composable
fun DashboardScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val auditLogs by viewModel.auditLogs.collectAsState()
    val snapshots by viewModel.deviceSnapshots.collectAsState()
    val selinuxEnforced by viewModel.selinuxEnforced.collectAsState()
    val lockdownActive by viewModel.zeroTrustLockdown.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val securityBriefing by viewModel.securityBriefing.collectAsState()
    val isGeneratingBriefing by viewModel.isGeneratingBriefing.collectAsState()
    val trustReport by viewModel.deviceTrustReport.collectAsState()
    val telemetryInput by viewModel.currentTelemetryInput.collectAsState()

    val latestSnapshot = snapshots.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HeroCommandBanner(
                roleLabel = currentRole.label,
                lockdownActive = lockdownActive,
                onRunAudit = { viewModel.runFullSecurityAudit() }
            )
        }

        if (trustReport != null) {
            item {
                DeviceTrustDashboardComponent(
                    report = trustReport!!,
                    currentTelemetryInput = telemetryInput,
                    onTelemetryChanged = { updated ->
                        viewModel.updateTelemetryInput(updated)
                    }
                )
            }
        }

        if (securityBriefing != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan),
                    modifier = Modifier.fillMaxWidth().testTag("ai_security_briefing_card")
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
                                    text = "AEGIS AI EXECUTIVE BRIEFING",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisPrimaryCyan
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearSecurityBriefing() },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 10.sp, color = AegisTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = securityBriefing ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                    }
                }
            }
        }

        item {
            com.example.ui.components.MatrixHealthDashboard(
                onTriggerAcingAudit = { viewModel.runFullSecurityAudit() }
            )
        }

        item {
            CentralNavigationHub(viewModel)
        }
        item {
            MissionObjectiveCard()
        }

        item {
            ReconcileTerminalCard()
        }

        item {
            HighDensityStatGrid()
        }

        item {
            SectionHeader(
                title = "Zero-Trust System Integrity",
                subtitle = "Hardware-backed verification & kernel security posture",
                icon = Icons.Default.Shield
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    title = "SELinux Policy",
                    value = if (selinuxEnforced) "ENFORCING" else "PERMISSIVE",
                    statusText = if (selinuxEnforced) "MAC Active" else "MAC Bypassed",
                    isSecure = selinuxEnforced,
                    icon = Icons.Default.Policy,
                    testTag = "selinux_status_card",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleSelinux() }
                )
                StatusCard(
                    title = "AVB 2.0 Boot",
                    value = "VERIFIED",
                    statusText = "RSA-4096 Locked",
                    isSecure = true,
                    icon = Icons.Default.VerifiedUser,
                    testTag = "avb_status_card",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    title = "Keystore Attest",
                    value = "STRONGBOX",
                    statusText = "TEE Hardware-Backed",
                    isSecure = true,
                    icon = Icons.Default.Memory,
                    testTag = "keystore_status_card",
                    modifier = Modifier.weight(1f)
                )
                StatusCard(
                    title = "Health Index",
                    value = "${latestSnapshot?.healthScore ?: 98}%",
                    statusText = if (lockdownActive) "Lockdown Mode" else "Zero-Trust Clean",
                    isSecure = (latestSnapshot?.healthScore ?: 98) >= 80,
                    icon = Icons.Default.Security,
                    testTag = "health_index_card",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(
                title = "Security Quick Operations",
                subtitle = "Execute automated zero-trust tasks & analysis",
                icon = Icons.Default.Terminal
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.runFullSecurityAudit() },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("run_audit_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sweep", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.generateSecurityBriefing() },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisSecureGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isGeneratingBriefing,
                    modifier = Modifier.weight(1f).testTag("generate_briefing_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Briefing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.selectTab(AppTab.AEGIS_AI) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("aegis_ai_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Chat", fontSize = 11.sp, color = AegisPrimaryCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            SectionHeader(
                title = "Real-Time Immutable Audit Ledger",
                subtitle = "Live security events stored in local Room database",
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
                        text = "No recorded audit logs yet. Run an Audit Sweep to generate ledger entries.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AegisTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(auditLogs.take(8)) { log ->
                AuditLogRowItem(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MissionObjectiveCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(AegisPrimaryCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mission Objective",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AegisTextPrimary
                    )
                    Text(
                        text = "Inspect, diagnose, repair, and validate.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisPrimaryCyan
                    )
                    Text(
                        text = "Commit: 5331f42",
                        fontSize = 10.sp,
                        color = AegisTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "PHASE 1" to 1.0f,
                    "PHASE 2" to 1.0f,
                    "PHASE 3" to 0.5f,
                    "PHASE 4" to 0.0f
                ).forEach { (phase, progress) ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(AegisBadgeIndigoBg)
                        ) {
                            if (progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(AegisPrimaryCyan)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phase,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (progress > 0f) AegisPrimaryCyan else AegisTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReconcileTerminalCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AegisTerminalBg),
        border = androidx.compose.foundation.BorderStroke(3.dp, AegisTerminalBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECONCILE TERMINAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTerminalTextPrimary
                )
                Text(
                    text = "genesis-irp.log",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTerminalPurple
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AegisTerminalBorder)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val logs = listOf(
                "[OK] Found 23 tracked, 4 untracked changes." to AegisTerminalGreen,
                "[ERR] rootmaster/page.tsx: JSX lint failed." to AegisTerminalRed,
                "[INFO] Fixing react/no-unescaped-entities..." to AegisTerminalPurple,
                "[INFO] Repairing encoding: Ãƒ -> UTF-8." to AegisTerminalPurple,
                "[OK] Branding updated to Acing IU: Genesis." to AegisTerminalGreen,
                "Running npm ci in /frontend..." to AegisTerminalTextPrimary,
                "Waiting for validation gates..." to AegisTextMuted
            )

            logs.forEach { (text, color) ->
                Text(
                    text = text,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp,
                    color = color
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun HighDensityStatGrid(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(AegisBadgePurpleBg)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "SECURITY AUDIT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisBadgePurpleText
                )
                Text(
                    text = "16 High",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisBadgePurpleText
                )
                Text(
                    text = "Omit-dev active",
                    fontSize = 10.sp,
                    color = AegisBadgePurpleText.copy(alpha = 0.7f)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(AegisBadgeRedBg)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "GATE STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisBadgeRedText
                )
                Text(
                    text = "8/12 PASS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisBadgeRedText
                )
                Text(
                    text = "4 Gates Pending",
                    fontSize = 10.sp,
                    color = AegisBadgeRedText.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HeroCommandBanner(
    roleLabel: String,
    lockdownActive: Boolean,
    onRunAudit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_command_banner")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Draw Hero Banner background image
            Image(
                painter = painterResource(id = R.drawable.hero_security_banner_1785324265324),
                contentDescription = "Acing IU Genesis Command Center",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.2f)
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AegisSurface.copy(alpha = 0.85f),
                                AegisSurface
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AegisPrimaryCyan.copy(alpha = 0.15f))
                            .border(1.dp, AegisPrimaryCyan, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ROLE: $roleLabel",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisPrimaryCyan
                        )
                    }

                    SeverityBadge(severity = if (lockdownActive) "CRITICAL" else "SECURE")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ACING IU: GENESIS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = AegisTextPrimary
                )

                Text(
                    text = "Android Research, Firmware Analysis & Zero-Trust Platform",
                    style = MaterialTheme.typography.bodySmall,
                    color = AegisTextSecondary
                )
            }
        }
    }
}


@Composable
fun CentralNavigationHub(viewModel: AcingViewModel) {
    SectionHeader(
        title = "Platform Navigation",
        subtitle = "Centralized hubs for analysis and security",
        icon = Icons.Default.Dashboard
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationCard(
            title = "Firmware Analysis",
            icon = Icons.Default.FolderZip,
            onClick = { viewModel.selectTab(com.example.ui.AppTab.FIRMWARE) },
            modifier = Modifier.weight(1f)
        )
        NavigationCard(
            title = "Threat Intel",
            icon = Icons.Default.BugReport,
            onClick = { viewModel.selectTab(com.example.ui.AppTab.THREAT_INTEL) },
            modifier = Modifier.weight(1f)
        )
        NavigationCard(
            title = "Device Security",
            icon = Icons.Default.Security,
            onClick = { viewModel.selectTab(com.example.ui.AppTab.DEVICE_SECURITY) },
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NavigationCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AegisTextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 12.sp)
        }
    }
}
