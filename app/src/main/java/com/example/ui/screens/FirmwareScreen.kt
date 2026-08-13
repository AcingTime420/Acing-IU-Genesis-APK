package com.example.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.data.FirmwareScanEntity
import com.example.ui.AcingViewModel
import com.example.ui.components.BuildPropFirmwareSecurityComponent
import com.example.ui.components.OdinFirmwareVerifierView
import com.example.ui.components.SectionHeader
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.*

private fun Context.findFragmentActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun FirmwareScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBiometricUnlocked by viewModel.isFirmwareBiometricUnlocked.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    val firmwareScans by viewModel.firmwareScans.collectAsState()
    val firmwareAnalysisResult by viewModel.firmwareAnalysisResult.collectAsState()
    val isAnalyzingFirmware by viewModel.isAnalyzingFirmware.collectAsState()

    val buildPropAudits by viewModel.buildPropAudits.collectAsState()
    val dmVerityStatuses by viewModel.dmVerityStatuses.collectAsState()
    val bootkitReport by viewModel.bootkitReport.collectAsState()
    val vbmetaResult by viewModel.vbmetaResult.collectAsState()
    val strongBoxCryptoResult by viewModel.strongBoxCryptoResult.collectAsState()
    val domainTransitions by viewModel.domainTransitions.collectAsState()

    val odinFirmwareResult by viewModel.odinFirmwareResult.collectAsState()
    val isVerifyingOdin by viewModel.isVerifyingOdin.collectAsState()

    val firmwareProgress by viewModel.firmwareAnalysisProgress.collectAsState()
    val isOfflinePolicyActive by viewModel.isOfflinePolicyActive.collectAsState()
    val lastOfflineVerification by viewModel.lastOfflineVerification.collectAsState()

    com.example.ui.components.SecurityGatekeeper(
        modifier = modifier,
        isUnlocked = isBiometricUnlocked,
        onUnlockChanged = { unlocked, auditReason ->
            viewModel.setFirmwareBiometricUnlocked(unlocked, auditReason)
        },
        title = "RESTRICTED FIRMWARE SUITE",
        subtitle = "Biometric Verification Required",
        description = "Sensitive cryptographic partition digests, bootkit telemetry, dm-verity tables, and Odin binary artifacts require zero-trust biometric authorization.",
        autoPrompt = true
    ) {
        // Unlocked Sensitive Firmware Analysis Suite
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Authenticated Session Header Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisBadgeIndigoBg),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBadgeIndigoText.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("firmware_authenticated_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = AegisSecureGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "BIOMETRIC AUTHENTICATED: TEE UNLOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisSecureGreen
                            )
                            Text(
                                text = "Authorized for ${currentRole.label} | Audit trail active",
                                fontSize = 10.sp,
                                color = AegisTextSecondary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.lockFirmwareBiometricSession() },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("lock_firmware_session_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AegisTextSecondary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock", fontSize = 10.sp, color = AegisTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Android Firmware & Partition Analysis",
                subtitle = "Inspect boot, recovery, system & vendor images for AVB 2.0 integrity",
                icon = Icons.Default.FolderZip
            )
        }

        // Linear Progress Bar Firmware Partition Analysis Routine
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                com.example.ui.components.FirmwareAnalysisProgressBar(
                    filesProcessed = firmwareProgress.filesProcessed,
                    totalFiles = firmwareProgress.totalFiles,
                    currentFileName = firmwareProgress.currentFileName,
                    currentPhase = firmwareProgress.currentPhase,
                    isComplete = firmwareProgress.isComplete,
                    throughputMbPerSec = firmwareProgress.throughputMbPerSec
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.runComprehensiveFirmwareSweep() },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = AegisDarkBg),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !firmwareProgress.isRunning,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("run_firmware_sweep_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (firmwareProgress.isRunning) "SWEEPING..." else "RUN FIRMWARE SWEEP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Local 'Fallback Security Policy' Module (Offline TFLite Model Verification)
        item {
            com.example.ui.components.FallbackSecurityPolicyCard(
                viewModel = viewModel
            )
        }

        if (firmwareAnalysisResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisSecureGreen),
                    modifier = Modifier.fillMaxWidth().testTag("ai_firmware_analysis_card")
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
                                    tint = AegisSecureGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AEGIS AI PARTITION FINDINGS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisSecureGreen
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.clearFirmwareAnalysis() },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 10.sp, color = AegisTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = firmwareAnalysisResult ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                    }
                }
            }
        }

        item {
            BuildPropFirmwareSecurityComponent(
                buildPropAudits = buildPropAudits,
                dmVerityStatuses = dmVerityStatuses,
                bootkitReport = bootkitReport,
                vbmetaResult = vbmetaResult,
                strongBoxCryptoResult = strongBoxCryptoResult,
                domainTransitions = domainTransitions,
                onScanBootkit = { viewModel.scanBootkitAndRamdisk() },
                onToggleVbmetaSpoof = { viewModel.toggleVbmetaSpoof(it) },
                onRunStrongBoxCrypto = { alias, text -> viewModel.runStrongBoxTeeCrypto(alias, text) },
                onUnblockDomainTransition = { source, target -> viewModel.unblockDomainTransition(source, target) }
            )
        }

        // Module 2: Odin Firmware Verification
        item {
            OdinFirmwareVerifierView(
                odinResult = odinFirmwareResult,
                isVerifying = isVerifyingOdin,
                onVerifyOdin = { viewModel.verifyOdinFirmwareArchive(it) },
                onDismiss = { viewModel.clearOdinFirmwareResult() }
            )
        }

        item {
            SectionHeader(
                title = "Partition Verification Matrix",
                subtitle = "Cryptographic SHA-256 hashes & digital signatures",
                icon = Icons.Default.Fingerprint
            )
        }

        val defaultPartitions = listOf(
            "boot.img" to "Kernel & Ramdisk",
            "init_boot.img" to "Generic Ramdisk (Android 13+)",
            "vendor_boot.img" to "Vendor Ramdisk & Modules",
            "system.img" to "Android OS Framework",
            "vendor.img" to "Hardware Abstraction Drivers",
            "vbmeta.img" to "Android Verified Boot Metadata"
        )

        items(defaultPartitions) { (partName, desc) ->
            val matchingScan = firmwareScans.firstOrNull { it.partitionName == partName }
            PartitionCard(
                partitionName = partName,
                description = desc,
                scanData = matchingScan,
                onScan = { viewModel.scanFirmwarePartition(partName) },
                onAiAudit = {
                    val hash = matchingScan?.sha256Hash ?: "a8f3b29c1e4d5f6a7b8c9d0e1f2a3b4c"
                    val sig = matchingScan?.signatureStatus ?: "AVB 2.0 RSA-4096 Pending"
                    viewModel.analyzeFirmwareWithAi(partName, hash, sig)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    }
}

@Composable
fun PartitionCard(
    partitionName: String,
    description: String,
    scanData: FirmwareScanEntity?,
    onScan: () -> Unit,
    onAiAudit: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = partitionName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisTextPrimary
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onAiAudit,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("ai_audit_${partitionName}_button")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AegisSecureGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("AI Audit", fontSize = 10.sp, color = AegisSecureGreen)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (scanData != null && scanData.isVerified) {
                        SeverityBadge(severity = "SECURE")
                    } else {
                        OutlinedButton(
                            onClick = onScan,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("scan_${partitionName}_button")
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Verify", fontSize = 10.sp, color = AegisPrimaryCyan)
                        }
                    }
                }
            }

            if (scanData != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AegisDarkBg, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "SHA-256: ${scanData.sha256Hash}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisPrimaryCyan,
                            maxLines = 1
                        )
                        Text(
                            text = "AVB 2.0: ${scanData.signatureStatus}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisSecureGreen
                        )
                    }
                }
            }
        }
    }
}

