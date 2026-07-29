package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirmwareScanEntity
import com.example.ui.AcingViewModel
import com.example.ui.components.BuildPropFirmwareSecurityComponent
import com.example.ui.components.SectionHeader
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun FirmwareScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val firmwareScans by viewModel.firmwareScans.collectAsState()
    val firmwareAnalysisResult by viewModel.firmwareAnalysisResult.collectAsState()
    val isAnalyzingFirmware by viewModel.isAnalyzingFirmware.collectAsState()

    val buildPropAudits by viewModel.buildPropAudits.collectAsState()
    val dmVerityStatuses by viewModel.dmVerityStatuses.collectAsState()
    val bootkitReport by viewModel.bootkitReport.collectAsState()
    val vbmetaResult by viewModel.vbmetaResult.collectAsState()
    val strongBoxCryptoResult by viewModel.strongBoxCryptoResult.collectAsState()
    val domainTransitions by viewModel.domainTransitions.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Android Firmware & Partition Analysis",
                subtitle = "Inspect boot, recovery, system & vendor images for AVB 2.0 integrity",
                icon = Icons.Default.FolderZip
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
