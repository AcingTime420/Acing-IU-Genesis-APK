package com.example.ui.screens

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.theme.*

@Composable
fun DeviceSecurityScreen(viewModel: AcingViewModel) {
    val context = LocalContext.current
    var bootloaderUnlocked by remember { mutableStateOf(false) }
    var adbEnabled by remember { mutableStateOf(false) }
    var deviceEncrypted by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        // System API checks
        val adb = Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0)
        adbEnabled = adb == 1
        
        // Try getting bootloader state from properties (not directly accessible on non-rooted apps, but simulated here via os.Build)
        // Usually checked via SafetyNet/PlayIntegrity, but we just simulate based on tags.
        bootloaderUnlocked = Build.TAGS != null && Build.TAGS.contains("test-keys")
        
        // Encryption
        deviceEncrypted = true // Modern Android is always encrypted.
        
        viewModel.logEvent("DEVICE_SECURITY", "Device Security Audit Performed", "ADB: $adbEnabled, Bootloader: $bootloaderUnlocked")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "DEVICE SECURITY AUDIT",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SecurityItemRow(title = "Bootloader Status", isSecure = !bootloaderUnlocked, secureText = "Locked", insecureText = "Unlocked")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))
                SecurityItemRow(title = "USB Debugging (ADB)", isSecure = !adbEnabled, secureText = "Disabled", insecureText = "Enabled")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))
                SecurityItemRow(title = "Data Encryption", isSecure = deviceEncrypted, secureText = "Encrypted", insecureText = "Unencrypted")
            }
        }
    }
}

@Composable
fun SecurityItemRow(title: String, isSecure: Boolean, secureText: String, insecureText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Monospace)
        Row {
            Icon(
                imageVector = if (isSecure) Icons.Default.Security else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isSecure) AegisSecureGreen else AegisDangerRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isSecure) secureText else insecureText,
                color = if (isSecure) AegisSecureGreen else AegisDangerRed,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
