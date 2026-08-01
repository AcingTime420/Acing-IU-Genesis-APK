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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
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
import com.example.security.AuditStatus
import com.example.security.SecurityAuditReport
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningYellow

@Composable
fun CustomSecurityAuditView(
    auditReport: SecurityAuditReport?,
    isRunning: Boolean,
    onRunAudit: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskNameInput by remember { mutableStateOf("Full Zero-Trust System Integrity Scan") }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("custom_audit_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CUSTOM SECURITY AUDIT ROUTINE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                }
            }

            Text(
                text = "Run isolated device intelligence workflows (memory integrity, TEE keymaster attestation, Knox fuse audit).",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = taskNameInput,
                onValueChange = { taskNameInput = it },
                label = { Text("Audit Suite / Task Name", fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_task_name_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (taskNameInput.isNotBlank()) {
                            onRunAudit(taskNameInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    enabled = !isRunning,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("run_audit_button")
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Running Audit Routine...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run Security Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (auditReport != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AegisDarkBg, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (auditReport.zeroTrustCompliant) AegisSecureGreen else AegisWarningYellow,
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
                                text = "AUDIT REPORT: ${auditReport.overallScore}/100 SCORE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (auditReport.zeroTrustCompliant) AegisSecureGreen else AegisWarningYellow
                            )

                            Text(
                                text = if (auditReport.zeroTrustCompliant) "ZERO TRUST COMPLIANT" else "ATTENTION REQUIRED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (auditReport.zeroTrustCompliant) AegisSecureGreen else AegisWarningYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        auditReport.stepResults.forEach { step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = when (step.status) {
                                            AuditStatus.PASSED -> Icons.Default.CheckCircle
                                            AuditStatus.WARNING -> Icons.Default.Warning
                                            AuditStatus.FAILED -> Icons.Default.Error
                                        },
                                        contentDescription = null,
                                        tint = when (step.status) {
                                            AuditStatus.PASSED -> AegisSecureGreen
                                            AuditStatus.WARNING -> AegisWarningYellow
                                            AuditStatus.FAILED -> AegisDangerRed
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = step.stepName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AegisTextPrimary
                                        )
                                        Text(
                                            text = step.details,
                                            fontSize = 9.sp,
                                            color = AegisTextSecondary,
                                            lineHeight = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
