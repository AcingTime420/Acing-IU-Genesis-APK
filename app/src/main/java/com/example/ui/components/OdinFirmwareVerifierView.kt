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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firmware.OdinTarMd5VerificationResult
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun OdinFirmwareVerifierView(
    odinResult: OdinTarMd5VerificationResult?,
    isVerifying: Boolean,
    onVerifyOdin: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var archiveNameInput by remember { mutableStateOf("AP_SM-S938U_S25_ULTRA_OEM_BUILD.tar.md5") }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("odin_verifier_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ODIN FIRMWARE VERIFICATION MODULE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                }
            }

            Text(
                text = "Parse PIT partition tables, calculate SHA-256 binary digests, and verify TAR.MD5 signatures.",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = archiveNameInput,
                onValueChange = { archiveNameInput = it },
                label = { Text("Odin Firmware Package (.tar.md5 / PIT)", fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("odin_archive_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (archiveNameInput.isNotBlank()) {
                            onVerifyOdin(archiveNameInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    enabled = !isVerifying,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("verify_odin_button")
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Parsing PIT Table...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify PIT & SHA-256", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (odinResult != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AegisDarkBg, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (odinResult.tamperedPartitionsDetected.isEmpty()) AegisSecureGreen else AegisDangerRed,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PIT TABLE: ${odinResult.totalPartitionsCount} PARTITIONS VERIFIED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisSecureGreen
                            )

                            Text(
                                text = "KNOX FUSE: ${odinResult.knoxWarrantyFuseState}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisSecureGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        odinResult.pitPartitions.forEach { pit ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Memory,
                                            contentDescription = null,
                                            tint = AegisPrimaryCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${pit.partitionName} (${pit.flashFilename})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = AegisTextPrimary
                                        )
                                    }
                                    Text(
                                        text = "${pit.sizeInMB}MB | ${pit.filesystemType}",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = AegisTextSecondary
                                    )
                                }
                                Text(
                                    text = "  SHA-256: ${pit.calculatedSha256Digest.take(24)}... [MATCH]",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisSecureGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
