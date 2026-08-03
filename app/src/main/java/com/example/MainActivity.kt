package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.worker.SecuritySnapshotWorker
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.ui.components.AuthenticationErrorView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.AppTab
import com.example.ui.screens.AegisAiScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.FirmwareScreen
import com.example.ui.screens.ForensicsScreen
import com.example.ui.screens.GovernanceScreen
import com.example.ui.screens.DeviceSecurityScreen
import com.example.ui.screens.SecurityStatusScreen
import com.example.ui.screens.ThreatIntelligenceScreen
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.components.AuthenticationErrorView
import androidx.compose.runtime.setValue

class MainActivity : androidx.fragment.app.FragmentActivity() {

    private val viewModel: AcingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        CrashHandler.install(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workRequest = PeriodicWorkRequestBuilder<SecuritySnapshotWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DailySecuritySnapshot", ExistingPeriodicWorkPolicy.KEEP, workRequest)
        setContent {
            MyApplicationTheme {
                AcingGenesisApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcingGenesisApp(viewModel: AcingViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    if (!isAuthenticated) {
        AuthScreen(viewModel = viewModel)
        return
    }
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val lockdownActive by viewModel.zeroTrustLockdown.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Acing IU",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AegisTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AegisBadgeIndigoBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v1.3.1",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisBadgeIndigoText
                                )
                            }
                        }
                        Text(
                            text = "Genesis IRP | Zero-Trust Platform",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (lockdownActive) com.example.ui.theme.AegisDangerRed.copy(alpha = 0.15f) else AegisBadgeIndigoBg)
                            .border(1.dp, if (lockdownActive) com.example.ui.theme.AegisDangerRed else AegisBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (lockdownActive) "LOCKDOWN" else "ZERO-TRUST OK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (lockdownActive) com.example.ui.theme.AegisDangerRed else AegisBadgeIndigoText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisDarkBg,
                    titleContentColor = AegisTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AegisDarkBg,
                contentColor = AegisTextSecondary,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(androidx.compose.foundation.BorderStroke(1.dp, AegisBorder))
                    .testTag("bottom_navigation_bar")
            ) {
                val navItems = listOf(
                    AppTab.DASHBOARD to ("Status" to Icons.Default.Dashboard),
                    AppTab.SECURITY_STATUS to ("Matrix" to Icons.Default.Shield),
                    AppTab.FIRMWARE to ("Firmware" to Icons.Default.FolderZip),
                    AppTab.DEVICES to ("Devices" to Icons.Default.PhoneAndroid),
                    AppTab.FORENSICS to ("Forensics" to Icons.Default.BugReport),
                    AppTab.AEGIS_AI to ("Aegis AI" to Icons.Default.SmartToy),
                    AppTab.GOVERNANCE to ("Security" to Icons.Default.AdminPanelSettings)
                )

                navItems.forEach { (tab, pair) ->
                    val (label, icon) = pair
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) AegisBadgeIndigoText else AegisTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AegisBadgeIndigoText else AegisTextSecondary,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = AegisBadgeIndigoBg
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = AegisDarkBg,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppTab.SECURITY_STATUS -> SecurityStatusScreen(viewModel = viewModel)
                AppTab.FIRMWARE -> FirmwareScreen(viewModel = viewModel)
                AppTab.DEVICES -> DevicesScreen(viewModel = viewModel)
                AppTab.FORENSICS -> ForensicsScreen(viewModel = viewModel)
                AppTab.AEGIS_AI -> AegisAiScreen(viewModel = viewModel)
                AppTab.GOVERNANCE -> GovernanceScreen(viewModel = viewModel)
                AppTab.THREAT_INTEL -> ThreatIntelligenceScreen(viewModel = viewModel)
                AppTab.DEVICE_SECURITY -> DeviceSecurityScreen(viewModel = viewModel)
            }
        }
    }
}

fun android.content.Context.findActivity(): androidx.fragment.app.FragmentActivity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is androidx.fragment.app.FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

object AuthSecurityLogger {
    fun logEvent(event: String, details: String) {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US).format(java.util.Date())
        android.util.Log.i("AegisSecurityAuth", "[$timestamp] EVENT: $event | DETAILS: $details")
    }

    fun logFailure(errorCode: Int, reason: String) {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US).format(java.util.Date())
        android.util.Log.e("AegisSecurityAuth", "[$timestamp] AUTH_FAILURE | CODE: $errorCode | REASON: $reason")
    }
}

@Composable
fun AuthScreen(viewModel: AcingViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context.findActivity()
    
    var authError by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    
    Box(
        modifier = Modifier.fillMaxSize().background(AegisDarkBg).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (authError != null) {
            AuthenticationErrorView(
                errorMessage = authError!!,
                onTryAgain = { authError = null }
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Security, contentDescription = "Security", tint = AegisPrimaryCyan, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("RESTRICTED ACCESS", style = MaterialTheme.typography.titleLarge, color = AegisDangerRed, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Biometric Authentication Required", color = AegisTextSecondary)
                Spacer(modifier = Modifier.height(32.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        if (activity != null) {
                            val biometricManager = androidx.biometric.BiometricManager.from(context)
                            val authenticators = androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
                            val canAuthenticate = biometricManager.canAuthenticate(authenticators)
                            
                            when (canAuthenticate) {
                                androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                                    AuthSecurityLogger.logEvent("BIOMETRIC_CHECK", "Biometric hardware available and enrolled.")
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "No biometric hardware available on device.")
                                    authError = "No biometric hardware available on device."
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "Biometric hardware is currently unavailable.")
                                    authError = "Biometric hardware is currently unavailable."
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "No biometrics enrolled on device. Falling back to device credential if possible.")
                                    authError = "No fingerprints or device credentials enrolled."
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "Security update required for biometric authentication.")
                                    authError = "Security update required."
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "Biometric authentication is unsupported.")
                                    authError = "Biometric authentication unsupported."
                                }
                                androidx.biometric.BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "Biometric status unknown.")
                                    authError = "Biometric status unknown."
                                }
                                else -> {
                                    AuthSecurityLogger.logFailure(canAuthenticate, "Unknown biometric authentication error code.")
                                    authError = "Unknown biometric error."
                                }
                            }
                            
                            if (canAuthenticate != androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
                                // Do not bypass anymore, show the error view instead
                                return@Button
                            }
    
                            val executor = androidx.core.content.ContextCompat.getMainExecutor(context)
                            val biometricPrompt = androidx.biometric.BiometricPrompt(activity, executor,
                                object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                                    override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                                        super.onAuthenticationSucceeded(result)
                                        AuthSecurityLogger.logEvent("AUTH_SUCCESS", "Biometric authentication succeeded. Type: ${result.authenticationType}")
                                        viewModel.setAuthenticated(true)
                                    }
                                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                        super.onAuthenticationError(errorCode, errString)
                                        AuthSecurityLogger.logFailure(errorCode, "Auth Error: $errString")
                                        authError = "Authentication Error: $errString"
                                    }
                                    override fun onAuthenticationFailed() {
                                        super.onAuthenticationFailed()
                                        AuthSecurityLogger.logFailure(-1, "Authentication failed (e.g. fingerprint not recognized).")
                                        authError = "Authentication failed. Fingerprint not recognized."
                                    }
                                })
                            val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Acing IU Authentication")
                                .setSubtitle("Verify identity to access intelligence dashboard")
                                .setAllowedAuthenticators(authenticators)
                                .build()
                            
                            AuthSecurityLogger.logEvent("AUTH_PROMPT", "Launching biometric prompt...")
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            android.widget.Toast.makeText(context, "Activity context not found", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan)
                ) {
                    Text("AUTHENTICATE", color = AegisDarkBg, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
