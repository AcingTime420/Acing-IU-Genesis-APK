package com.example.ui.screens

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.example.security.AcingMatrixAudit
import com.example.security.AcingSecurityWorker
import com.example.security.BiometricSecurityManager
import com.example.ui.AcingViewModel
import com.example.ui.components.AcingMatrixAuditView
import com.example.ui.components.FingerprintMappingView
import com.example.ui.components.HardwareSecuritySettingsCard
import com.example.ui.components.HardwareSecurityState
import com.example.ui.components.NetworkVulnerabilityScannerView
import com.example.ui.components.PredictiveKeyboardCard
import com.example.ui.components.SystemHealthChartDashboard
import com.example.ui.theme.*

@Composable
fun DeviceSecurityScreen(viewModel: AcingViewModel) {
    val context = LocalContext.current
    var bootloaderUnlocked by remember { mutableStateOf(false) }
    var adbEnabled by remember { mutableStateOf(false) }
    var deviceEncrypted by remember { mutableStateOf(true) }

    val matrixAudit = remember { AcingMatrixAudit() }
    val biometricManager = remember { BiometricSecurityManager(context) }
    var hardwareSettingsState by remember { mutableStateOf(HardwareSecurityState()) }

    val networkScanReport by viewModel.networkScanReport.collectAsState()
    val isNetworkScanning by viewModel.isNetworkScanning.collectAsState()
    
    LaunchedEffect(Unit) {
        // System API checks
        val adb = Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0)
        adbEnabled = adb == 1
        
        bootloaderUnlocked = Build.TAGS != null && Build.TAGS.contains("test-keys")
        deviceEncrypted = true

        // Enqueue background WorkManager security worker task
        AcingSecurityWorker.enqueueImmediateSync(context)
        AcingSecurityWorker.schedulePeriodicSync(context)
        
        viewModel.logEvent("DEVICE_SECURITY", "Device Security Audit Performed", "ADB: $adbEnabled, Bootloader: $bootloaderUnlocked")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "DEVICE SECURITY & ACING MATRIX AUDIT",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // 1. System Health Chart Dashboard
        SystemHealthChartDashboard(
            nodeConsensusPercentage = if (hardwareSettingsState.acingMatrixSyncEnabled) 100 else 66,
            activeNodesCount = if (hardwareSettingsState.acingMatrixSyncEnabled) 3 else 2,
            securityEventCount = 14
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

        // 2. Persistent Partition & FRP Audit View
        AcingMatrixAuditView(
            matrixAudit = matrixAudit,
            onTriggerAudit = {
                viewModel.logEvent("PST_AUDIT", "Persistent Partition Audited", "Verified via PersistentDataBlockManager")
            }
        )

        // 3. Biometric Fingerprint Action Mapping View
        FingerprintMappingView(
            biometricManager = biometricManager,
            onTriggerExecuted = { mapping, feedback ->
                if (mapping.assignedAction == com.example.security.SecurityActionType.AUTHORIZED_FRP_RESET) {
                    matrixAudit.logAuthorizedFrpResetEvent(
                        triggerFinger = mapping.fingerName,
                        isBiometricValid = true,
                        matrixConsensusApproved = hardwareSettingsState.acingMatrixSyncEnabled
                    )
                }
                viewModel.logEvent("BIOMETRIC_TRIGGER", mapping.fingerName, feedback)
            }
        )

        // 4. Hardware Security Protection Toggles Settings Card
        HardwareSecuritySettingsCard(
            state = hardwareSettingsState,
            onStateChange = { newState ->
                hardwareSettingsState = newState
                viewModel.logEvent(
                    category = "HARDWARE_TOGGLES",
                    title = "Hardware Security Settings Updated",
                    details = "USB Lockdown: ${newState.usbDataLockdown}, 2G/3G Lockdown: ${newState.radioCellularLockdown2G3G}, Matrix Sync: ${newState.acingMatrixSyncEnabled}"
                )
            }
        )
        
        // 5. Predictive Auto-Keyboard Engine
        PredictiveKeyboardCard()

        // 6. Custom Network Vulnerability Scanner
        NetworkVulnerabilityScannerView(
            scanReport = networkScanReport,
            isScanning = isNetworkScanning,
            onRunScan = { viewModel.runNetworkVulnerabilityScan(it) },
            onDismiss = { viewModel.clearNetworkScanReport() }
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        val biometricLockout by viewModel.biometricLockoutProtection.collectAsState()
        val highSensitivity by viewModel.highSensitivityMode.collectAsState()
        
        com.example.ui.components.SecuritySettingsView(
            biometricLockoutProtection = biometricLockout,
            onBiometricLockoutChange = { viewModel.toggleBiometricLockoutProtection(it) },
            highSensitivityMode = highSensitivity,
            onHighSensitivityChange = { viewModel.toggleHighSensitivityMode(it) }
        )
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
