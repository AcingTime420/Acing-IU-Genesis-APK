package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

/**
 * Hardware Security Protection Toggles Card state.
 */
data class HardwareSecurityState(
    val usbDataLockdown: Boolean = true,
    val radioCellularLockdown2G3G: Boolean = true,
    val autoRebootTimerEnabled: Boolean = true,
    val duressErasurePinConfigured: Boolean = true,
    val acingMatrixSyncEnabled: Boolean = true
)

/**
 * Card-based settings interface in Jetpack Compose to allow toggling hardware-level
 * protections like USB-C Data Lockdown, 2G/3G cellular radio controls, and Auto-Reboot timer.
 */
@Composable
fun HardwareSecuritySettingsCard(
    state: HardwareSecurityState,
    onStateChange: (HardwareSecurityState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(12.dp))
            .testTag("hardware_security_settings_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Acing Matrix Developer Protection Controls",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AegisTextPrimary
            )
            Text(
                text = "Hardware-level toggles for Knox / Acing IU baseline enforcement",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle 1: USB-C Data Lockdown
            SettingToggleItem(
                title = "USB-C Data Lockdown",
                subtitle = "Blocks data transmission through USB port; restricts to charging only to prevent exfiltration.",
                icon = Icons.Default.Usb,
                isChecked = state.usbDataLockdown,
                onCheckedChange = { onStateChange(state.copy(usbDataLockdown = it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 2: 2G/3G Cellular Radio Lockdown
            SettingToggleItem(
                title = "2G / 3G Cellular Radio Lockdown",
                subtitle = "Disables legacy cellular protocols to prevent IMSI catcher / Stingray interception attacks.",
                icon = Icons.Default.CellTower,
                isChecked = state.radioCellularLockdown2G3G,
                onCheckedChange = { onStateChange(state.copy(radioCellularLockdown2G3G = it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 3: Auto-Reboot Timer
            SettingToggleItem(
                title = "Auto-Reboot Timer (Data-at-Rest)",
                subtitle = "Reboots device after 12h of inactivity to return RAM keys to encrypted storage at rest.",
                icon = Icons.Default.LockClock,
                isChecked = state.autoRebootTimerEnabled,
                onCheckedChange = { onStateChange(state.copy(autoRebootTimerEnabled = it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 4: Acing Matrix Sync
            SettingToggleItem(
                title = "Acing Matrix Blockchain Sync",
                subtitle = "Synchronizes dApp-style credential shares across local trust node consensus.",
                icon = Icons.Default.Sync,
                isChecked = state.acingMatrixSyncEnabled,
                onCheckedChange = { onStateChange(state.copy(acingMatrixSyncEnabled = it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 5: Duress Erasure PIN
            SettingToggleItem(
                title = "Duress Erasure (Panic PIN)",
                subtitle = "Alternate PIN triggers background wipe of persistent partition and sensitive keys.",
                icon = Icons.Default.LockReset,
                isChecked = state.duressErasurePinConfigured,
                onCheckedChange = { onStateChange(state.copy(duressErasurePinConfigured = it)) }
            )
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(AegisDarkBg, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) AegisPrimaryCyan else AegisTextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = AegisTextSecondary,
                    lineHeight = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AegisSecureGreen,
                checkedTrackColor = AegisSecureGreen.copy(alpha = 0.3f),
                uncheckedThumbColor = AegisTextMuted,
                uncheckedTrackColor = AegisDarkBg
            )
        )
    }
}
