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

@Composable
fun SecuritySettingsView(
    biometricLockoutProtection: Boolean,
    onBiometricLockoutChange: (Boolean) -> Unit,
    highSensitivityMode: Boolean,
    onHighSensitivityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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
        }
    }
}
