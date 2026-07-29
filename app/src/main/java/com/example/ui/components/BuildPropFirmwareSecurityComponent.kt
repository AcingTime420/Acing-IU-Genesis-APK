package com.example.ui.components

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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.firmware.BootkitDetectionReport
import com.example.firmware.BuildPropItem
import com.example.firmware.DmVerityStatus
import com.example.firmware.DomainTransitionPermission
import com.example.firmware.StrongBoxTeeOperationResult
import com.example.firmware.VbmetaValidationAndSpoofResult
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryBlue
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@Composable
fun BuildPropFirmwareSecurityComponent(
    buildPropAudits: List<BuildPropItem>,
    dmVerityStatuses: List<DmVerityStatus>,
    bootkitReport: BootkitDetectionReport,
    vbmetaResult: VbmetaValidationAndSpoofResult,
    strongBoxCryptoResult: StrongBoxTeeOperationResult?,
    domainTransitions: List<DomainTransitionPermission>,
    onScanBootkit: () -> Unit,
    onToggleVbmetaSpoof: (Boolean) -> Unit,
    onRunStrongBoxCrypto: (alias: String, text: String) -> Unit,
    onUnblockDomainTransition: (source: String, target: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var teeAliasInput by remember { mutableStateOf("AcingStrongBoxKey_v2") }
    var teeTextInput by remember { mutableStateOf("GENESIS-HARDWARE-PAYLOAD-PROTECTED-AES256") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Build.prop Audit Card
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("full_build_prop_audit_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AegisPrimaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BUILD.PROP & KERNEL FLAG AUDIT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = AegisTextPrimary
                            )
                            Text(
                                text = "ro.debuggable, sys.oem_unlock_allowed, SELinux, Strongbox TEE, Root of Trust",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = AegisTextMuted
                            )
                        }
                    }
                    SeverityBadge(severity = "SECURE")
                }

                Spacer(modifier = Modifier.height(12.dp))

                buildPropAudits.forEach { prop ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prop.propName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisPrimaryCyan
                            )
                            Text(
                                text = prop.description,
                                fontSize = 9.sp,
                                color = AegisTextMuted,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = prop.currentValue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (prop.isSecure) AegisSecureGreen else AegisWarningGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AegisSecureGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Automatic dm-verity Detection
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("dm_verity_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AegisSecureGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTOMATIC DM-VERITY DETECTION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisSecureGreen
                        )
                    }
                    SeverityBadge(severity = "SECURE")
                }

                Spacer(modifier = Modifier.height(10.dp))

                dmVerityStatuses.forEach { ver ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AegisDarkBg)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Partition: ${ver.partitionName} [DM-VERITY ACTIVE]",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisTextPrimary
                                )
                                Text(
                                    text = "Hash Tree Root: ${ver.hashTreeRoot.take(28)}...",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisPrimaryCyan
                                )
                            }
                            Text(
                                text = ver.integrityState,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisSecureGreen
                            )
                        }
                    }
                }
            }
        }

        // 3. Automatic Bootkit Injection & Ramdisk Modification Scanner
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("bootkit_detection_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BOOTKIT INJECTION & RAMDISK DETECTION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisPrimaryCyan
                        )
                    }
                    Button(
                        onClick = onScanBootkit,
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("scan_bootkit_btn")
                    ) {
                        Text("SCAN NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Scanned Target: ${bootkitReport.scannedTarget} | Status: ${bootkitReport.threatLevel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisSecureGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                bootkitReport.injectionVectors.forEach { vector ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AegisSecureGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = vector, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AegisTextSecondary)
                    }
                }
            }
        }

        // 4. Automatic vbmeta.img Image Unblocker & Mocker Validator and Spoofer
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("vbmeta_mocker_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VBMETA.IMG UNBLOCKER & SPOOFER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisPrimaryCyan
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (vbmetaResult.unblocked) "SPOOF ACTIVE" else "HARDWARE LOCK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (vbmetaResult.unblocked) AegisWarningGold else AegisSecureGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = vbmetaResult.unblocked,
                            onCheckedChange = { onToggleVbmetaSpoof(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AegisWarningGold, checkedTrackColor = AegisWarningGold.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("vbmeta_spoof_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Header Magic: ${vbmetaResult.vbmetaHeaderMagic} | Rollback Index: ${vbmetaResult.rollbackIndex} | State: ${vbmetaResult.spoofedState}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vbmetaResult.details,
                    fontSize = 10.sp,
                    color = AegisTextSecondary
                )
            }
        }

        // 5. Automatic StrongBox Keymaster TEE Encryption/Decryption
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("strongbox_crypto_card")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STRONGBOX KEYMASTER TEE CRYPTO ENGINE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = teeAliasInput,
                        onValueChange = { teeAliasInput = it },
                        label = { Text("StrongBox Key Alias", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AegisPrimaryCyan, unfocusedBorderColor = AegisBorder)
                    )
                    OutlinedTextField(
                        value = teeTextInput,
                        onValueChange = { teeTextInput = it },
                        label = { Text("Plaintext Payload", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AegisPrimaryCyan, unfocusedBorderColor = AegisBorder)
                    )
                }

                Button(
                    onClick = { onRunStrongBoxCrypto(teeAliasInput, teeTextInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("run_strongbox_crypto_btn")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EXECUTE STRONGBOX AES-256-GCM TEE ENCRYPTION", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                if (strongBoxCryptoResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AegisDarkBg)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Algorithm: ${strongBoxCryptoResult.algorithm}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AegisSecureGreen)
                            Text(text = "Ciphertext: ${strongBoxCryptoResult.cipherTextBase64}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AegisPrimaryCyan)
                            Text(text = "Decrypted: ${strongBoxCryptoResult.decryptedText}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AegisTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            strongBoxCryptoResult.attestationCertificateChain.forEach { line ->
                                Text(text = line, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = AegisTextMuted)
                            }
                        }
                    }
                }
            }
        }

        // 6. SELinux Unauthorized Domain Transition Permissions
        Card(
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
            modifier = Modifier.fillMaxWidth().testTag("domain_transitions_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SELINUX DOMAIN TRANSITION PERMISSIONS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisPrimaryCyan
                        )
                    }
                    SeverityBadge(severity = "SECURE")
                }

                Spacer(modifier = Modifier.height(10.dp))

                domainTransitions.forEach { trans ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AegisDarkBg)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Transition: ${trans.sourceDomain} -> ${trans.targetDomain}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisTextPrimary
                                )
                                Text(
                                    text = trans.unblockStatus,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (trans.isBlockedBySELinux) AegisSecureGreen else AegisWarningGold
                                )
                            }

                            if (trans.isBlockedBySELinux) {
                                OutlinedButton(
                                    onClick = { onUnblockDomainTransition(trans.sourceDomain, trans.targetDomain) },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("unblock_${trans.sourceDomain}_${trans.targetDomain}_btn")
                                ) {
                                    Text("Unblock Lab", fontSize = 9.sp, color = AegisWarningGold)
                                }
                            } else {
                                Text(text = "ALLOWED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AegisSecureGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}
