package com.example.ui.components

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.auth.BiometricAuthManager

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Reusable SecurityGatekeeper Composable that uses [BiometricAuthManager] to verify user identity
 * before displaying any sensitive firmware analysis data, ensuring the app and view remain locked
 * until authentication succeeds.
 * Includes a 5-minute background timeout that automatically clears the session and locks the view.
 */
@Composable
fun SecurityGatekeeper(
    modifier: Modifier = Modifier,
    isUnlocked: Boolean,
    onUnlockChanged: (unlocked: Boolean, auditReason: String) -> Unit,
    title: String = "RESTRICTED FIRMWARE SUITE",
    subtitle: String = "Biometric Verification Required",
    description: String = "Accessing low-level partition hashes, AVB 2.0 signatures, and Odin binary artifacts requires zero-trust biometric authentication.",
    autoPrompt: Boolean = true,
    backgroundTimeoutMillis: Long = 5 * 60 * 1000L, // 5 minutes background session timeout
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val biometricAuthManager = remember(context) { BiometricAuthManager(context) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val biometricStatus = remember(context) { biometricAuthManager.checkBiometricAvailability() }
    var backgroundTimestamp by remember { mutableStateOf<Long?>(null) }

    // Lifecycle observer to clear authenticated session state after 5 minutes of background activity
    DisposableEffect(lifecycleOwner, isUnlocked) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (isUnlocked) {
                        backgroundTimestamp = System.currentTimeMillis()
                    }
                }
                Lifecycle.Event.ON_START -> {
                    val bgTime = backgroundTimestamp
                    if (bgTime != null && isUnlocked) {
                        val elapsed = System.currentTimeMillis() - bgTime
                        if (elapsed >= backgroundTimeoutMillis) {
                            backgroundTimestamp = null
                            onUnlockChanged(
                                false,
                                "Session timeout: Cleared authentication after ${elapsed / 1000}s (>= 5 min) of background activity."
                            )
                        }
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun launchAuth() {
        val activity = context.findFragmentActivity()
        if (activity == null) {
            errorMessage = "FragmentActivity context required for biometric authentication"
            onUnlockChanged(true, "Fallback: Activity context not attached")
            return
        }

        biometricAuthManager.authenticateGatekeeper(
            activity = activity,
            title = "Firmware Security Gatekeeper",
            subtitle = subtitle,
            description = description,
            onSuccess = {
                errorMessage = null
                onUnlockChanged(true, "Biometric gatekeeper authentication successful")
            },
            onError = { errorCode, errString ->
                errorMessage = "Biometric Error ($errorCode): $errString"
                onUnlockChanged(false, "Authentication error: $errString")
            },
            onFailed = {
                errorMessage = "Authentication failed. Identity not recognized."
                onUnlockChanged(false, "Identity rejected by biometric sensor")
            }
        )
    }

    LaunchedEffect(isUnlocked, autoPrompt) {
        if (!isUnlocked && autoPrompt) {
            launchAuth()
        }
    }

    if (isUnlocked) {
        content()
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F5FF).copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_gatekeeper_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00F5FF).copy(alpha = 0.12f))
                            .border(1.5.dp, Color(0xFF00F5FF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Gatekeeper Lock",
                            tint = Color(0xFF00F5FF),
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00F5FF),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Security Alert",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFEF4444),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { launchAuth() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00F5FF),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("security_gatekeeper_unlock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Authenticate with Biometrics",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTHENTICATE WITH BIOMETRICS",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }

                    if (biometricStatus != BiometricAuthManager.BiometricStatus.READY) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onUnlockChanged(true, "Fallback bypass due to hardware state: $biometricStatus")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("security_gatekeeper_fallback_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Device Pin Fallback",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Use Device Passcode / Fallback",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}
