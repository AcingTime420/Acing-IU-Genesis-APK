package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.FallbackSecurityPolicyModule
import com.example.ui.AcingViewModel
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * Fallback Security Policy Card:
 * Displays active Restricted Mode status, network auto-detection, local-only TFLite file signature
 * verification triggers (boot.img, vbmeta.img, vendor_boot.img, recovery.img), and entropy/confidence metrics.
 */
@Composable
fun FallbackSecurityPolicyCard(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val isOfflinePolicyActive by viewModel.isOfflinePolicyActive.collectAsState()
    val restrictedState by viewModel.restrictedModeState.collectAsState()
    val lastVerification by viewModel.lastOfflineVerification.collectAsState()
    val history by viewModel.fallbackVerificationHistory.collectAsState()

    var showDetails by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fallback_security_policy_card"),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOfflinePolicyActive) AegisWarningGold else AegisBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title & Policy Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isOfflinePolicyActive) AegisWarningGold.copy(alpha = 0.15f) else AegisPrimaryCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOfflinePolicyActive) Icons.Default.Security else Icons.Default.Shield,
                            contentDescription = "Fallback Policy",
                            tint = if (isOfflinePolicyActive) AegisWarningGold else AegisPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Fallback Security Policy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                        Text(
                            text = if (isOfflinePolicyActive) "Restricted Mode Active (Air-Gapped)" else "Standard Connected Mode",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isOfflinePolicyActive) AegisWarningGold else AegisSecureGreen
                        )
                    }
                }

                // Mode status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOfflinePolicyActive) AegisWarningGold.copy(alpha = 0.15f) else AegisSecureGreen.copy(alpha = 0.15f))
                        .border(1.dp, if (isOfflinePolicyActive) AegisWarningGold else AegisSecureGreen, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isOfflinePolicyActive) "RESTRICTED" else "ONLINE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isOfflinePolicyActive) AegisWarningGold else AegisSecureGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation / Subtitle
            Text(
                text = restrictedState.reason,
                fontSize = 11.sp,
                color = AegisTextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Policy State Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisDarkBg)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PolicyDetailRow(
                    label = "AI Engine Status:",
                    value = restrictedState.cloudAiStatus,
                    color = if (isOfflinePolicyActive) AegisWarningGold else AegisSecureGreen
                )
                PolicyDetailRow(
                    label = "Verification Mode:",
                    value = restrictedState.signatureVerificationEngine,
                    color = AegisPrimaryCyan
                )
                PolicyDetailRow(
                    label = "Telemetry Sync:",
                    value = restrictedState.telemetrySyncStatus,
                    color = if (isOfflinePolicyActive) AegisTextMuted else AegisSecureGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Local TFLite Partition Signature Verifiers
            Text(
                text = "Local Pre-Bundled TFLite File Signature Verification:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AegisTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.verifyFileWithFallbackSecurityPolicy("boot.img") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("verify_boot_tflite_btn")
                ) {
                    Text("boot.img", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AegisPrimaryCyan)
                }
                OutlinedButton(
                    onClick = { viewModel.verifyFileWithFallbackSecurityPolicy("vbmeta.img") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("verify_vbmeta_tflite_btn")
                ) {
                    Text("vbmeta.img", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AegisPrimaryCyan)
                }
                OutlinedButton(
                    onClick = { viewModel.verifyFileWithFallbackSecurityPolicy("vendor_boot.img") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("verify_vendor_boot_btn")
                ) {
                    Text("vendor.img", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AegisPrimaryCyan)
                }
            }

            // Simulated Offline Toggle / Hardware Auto-detection buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.toggleSimulatedOfflinePolicy(!isOfflinePolicyActive)
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOfflinePolicyActive) AegisPrimaryCyan.copy(alpha = 0.2f) else AegisWarningGold.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.weight(1f).testTag("toggle_offline_policy_btn")
                ) {
                    Text(
                        text = if (isOfflinePolicyActive) "Switch to Online Mode" else "Simulate Offline Disconnect",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isOfflinePolicyActive) AegisPrimaryCyan else AegisWarningGold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.resetOfflinePolicyToAutoDetection() },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("reset_auto_detect_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Auto Detect",
                        tint = AegisTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto", fontSize = 10.sp, color = AegisTextSecondary)
                }
            }

            // Latest Verification Result Card
            lastVerification?.let { result ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (result.isAuthentic) AegisSecureGreen.copy(alpha = 0.08f) else AegisDangerRed.copy(alpha = 0.08f))
                        .border(1.dp, if (result.isAuthentic) AegisSecureGreen else AegisDangerRed, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                        .testTag("tflite_verification_result_box")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (result.isAuthentic) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = "Status",
                                    tint = if (result.isAuthentic) AegisSecureGreen else AegisDangerRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TFLite Model Output: ${result.fileName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (result.isAuthentic) AegisSecureGreen else AegisDangerRed
                                )
                            }
                            Text(
                                text = "${(result.confidenceScore * 100).toInt()}% Conf.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Confidence Progress Bar
                        LinearProgressIndicator(
                            progress = { result.confidenceScore },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (result.isAuthentic) AegisSecureGreen else AegisDangerRed,
                            trackColor = AegisDarkBg
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = result.details,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Entropy: ${"%.2f".format(result.byteEntropy)} | Magic: ${result.partitionMagic}",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextMuted
                            )
                            Text(
                                text = "Engine: TFLite Offline",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisPrimaryCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicyDetailRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = AegisTextSecondary
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            color = color
        )
    }
}
