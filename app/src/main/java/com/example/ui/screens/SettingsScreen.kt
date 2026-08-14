package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.components.HardwareSecuritySettingsCard
import com.example.ui.components.HardwareSecurityState
import com.example.ui.components.InfoTooltip
import com.example.ui.components.SectionHeader
import com.example.ui.components.SecuritySettingsView
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * SettingsScreen provides the user full control of their device security posture,
 * hardware protection locks, system-level enforcing toggles, and direct entry
 * to the complete Capabilities Guide.
 */
@Composable
fun SettingsScreen(
    viewModel: AcingViewModel,
    onNavigateToCapabilities: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hardwareState by remember { mutableStateOf(HardwareSecurityState()) }
    val biometricLockout by viewModel.biometricLockoutProtection.collectAsState()
    val highSensitivity by viewModel.highSensitivityMode.collectAsState()
    val restrictedState by viewModel.restrictedModeState.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AegisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Capabilities Guide Navigation Entry
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capabilities_guide_entry_card"),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AegisPrimaryCyan.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Platform Capabilities Guide",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary
                            )
                        }

                        Surface(
                            color = AegisBadgeIndigoBg,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ENCYCLOPEDIA",
                                color = AegisBadgeIndigoText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Explore the complete breakdown of Firmware Analysis, Security Audit DB, Biometric Gatekeeper, Network Scanners, and SELinux generation architecture.",
                        fontSize = 12.sp,
                        color = AegisTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToCapabilities,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AegisPrimaryCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_capabilities_guide_btn")
                    ) {
                        Text(
                            text = "Open Capabilities Guide",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Full Device Hardware Control Section
        item {
            SectionHeader(
                title = "Hardware Security Controls",
                icon = Icons.Default.Tune
            )
        }

        item {
            HardwareSecuritySettingsCard(
                state = hardwareState,
                onStateChange = { newState ->
                    hardwareState = newState
                    viewModel.recordAuditAction(
                        category = "Device Control",
                        title = "Hardware Security Settings Updated",
                        details = "Updated USB lockdown=${newState.usbDataLockdown}, cellular 2G lockdown=${newState.radioCellularLockdown2G3G}, auto-reboot=${newState.autoRebootTimerEnabled}, acing matrix sync=${newState.acingMatrixSyncEnabled}",
                        severity = "INFO"
                    )
                }
            )
        }

        // 3. System & Biometric Policy Controls
        item {
            SectionHeader(
                title = "Authentication & Detection Strictness",
                icon = Icons.Default.Shield
            )
        }

        item {
            SecuritySettingsView(
                biometricLockoutProtection = biometricLockout,
                onBiometricLockoutChange = { viewModel.toggleBiometricLockoutProtection(it) },
                highSensitivityMode = highSensitivity,
                onHighSensitivityChange = { viewModel.toggleHighSensitivityMode(it) }
            )
        }

        // 4. Autonomous Security & Offline Fallback Toggles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("autonomous_policy_card"),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AegisBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Autonomous Policy Engine",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        InfoTooltip(
                            summary = "Governs whether offline TFLite models can enforce network isolation without waiting for cloud validation."
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Offline TFLite Fallback Verification",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary
                            )
                            Text(
                                text = "Enforces zero-trust local verification when internet connectivity is severed.",
                                fontSize = 11.sp,
                                color = AegisTextSecondary
                            )
                        }

                        Switch(
                            checked = restrictedState.isRestricted,
                            onCheckedChange = { viewModel.toggleOfflineFallbackPolicy(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = AegisPrimaryCyan,
                                uncheckedThumbColor = AegisTextMuted,
                                uncheckedTrackColor = AegisSurfaceVariant
                            ),
                            modifier = Modifier.testTag("offline_fallback_switch")
                        )
                    }
                }
            }
        }

        // 5. Onboarding & Tour Replay Tools
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tour_management_card"),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AegisBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Tutorials & Discovery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetOnboardingAndDiscovery()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("replay_onboarding_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Replay Tour",
                                fontSize = 11.sp,
                                color = AegisTextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToCapabilities,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_encyclopedia_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = AegisWarningGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Read Guide",
                                fontSize = 11.sp,
                                color = AegisTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
