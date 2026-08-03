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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AcingMatrixAudit
import com.example.security.FrpAuditRecord
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTerminalGreen
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * UI component displaying PersistentDataBlockManager verification state
 * and FRP audit logs for authorized resets.
 */
@Composable
fun AcingMatrixAuditView(
    matrixAudit: AcingMatrixAudit,
    onTriggerAudit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var auditLogs by remember { mutableStateOf(matrixAudit.getAuditTrail()) }
    var latestRecord by remember { mutableStateOf(auditLogs.firstOrNull()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(12.dp))
            .testTag("acing_matrix_audit_view")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AegisSecureGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Persistent Partition & FRP Audit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AegisTextPrimary
                    )
                }

                Button(
                    onClick = {
                        val record = matrixAudit.performPersistentPartitionAudit(context)
                        auditLogs = matrixAudit.getAuditTrail()
                        latestRecord = record
                        onTriggerAudit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Audit Partition", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Partition Status Card
            latestRecord?.let { record ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisDarkBg)
                        .border(1.dp, AegisBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Target: ${record.partitionPath}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AegisBadgeIndigoBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = record.partitionState,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisBadgeIndigoText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "PST Block Size: ${record.persistentDataSizeBytes / 1024} KB | Consensus: ${record.nodeConsensusHash}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = record.executionOutcome,
                            fontSize = 10.sp,
                            color = AegisTerminalGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FRP Audit Trail Logs Header
            Text(
                text = "FRP Bypass Audit Log Trail (${auditLogs.size} Events)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisTerminalBg)
                    .padding(8.dp)
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(auditLogs) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "[${item.getFormattedTime()}] ${item.actionType}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisPrimaryCyan
                                )
                                Text(
                                    text = item.partitionState,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (item.isBiometricVerified) AegisTerminalGreen else AegisWarningGold
                                )
                            }
                            Text(
                                text = item.executionOutcome,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
