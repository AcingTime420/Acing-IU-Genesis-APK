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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RemediationProposal
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBadgePurpleBg
import com.example.ui.theme.AegisBadgePurpleText
import com.example.ui.theme.AegisBadgeRedBg
import com.example.ui.theme.AegisBadgeRedText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryBlue
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun AegisSelfHealingComponent(
    proposals: List<RemediationProposal>,
    isDiagnosisActive: Boolean,
    onRunDiagnosis: () -> Unit,
    onApprovePatch: (String) -> Unit,
    onDismissPatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "Aegis Autonomous Healing",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUTONOMOUS REMEDIATION & SELF-HEALING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryCyan
                    )
                }

                Button(
                    onClick = onRunDiagnosis,
                    enabled = !isDiagnosisActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AegisPrimaryCyan,
                        contentColor = AegisDarkBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("run_diagnosis_button")
                ) {
                    if (isDiagnosisActive) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = AegisDarkBg,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Diagnosing...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Diagnosis", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Aegis continuously audits system state. Autonomous remediation requires human approval before modifying security policies or key states.",
                fontSize = 10.sp,
                color = AegisTextSecondary,
                lineHeight = 13.sp
            )

            if (proposals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    proposals.forEach { proposal ->
                        RemediationProposalCard(
                            proposal = proposal,
                            onApprove = { onApprovePatch(proposal.id) },
                            onDismiss = { onDismissPatch(proposal.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemediationProposalCard(
    proposal: RemediationProposal,
    onApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    val severityBadgeColors = when (proposal.severity.uppercase()) {
        "CRITICAL" -> Pair(AegisBadgeRedBg, AegisBadgeRedText)
        "HIGH" -> Pair(AegisBadgePurpleBg, AegisBadgePurpleText)
        else -> Pair(AegisBadgeIndigoBg, AegisBadgeIndigoText)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AegisDarkBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(severityBadgeColors.first)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = proposal.severity.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = severityBadgeColors.second
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = proposal.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )
                }

                if (!proposal.isExecuted) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = AegisTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: ${proposal.impactedComponent}",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = AegisPrimaryCyan
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = proposal.description,
                fontSize = 10.sp,
                color = AegisTextSecondary,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Proposed Fix: ${proposal.proposedFix}",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AegisSecureGreen,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (proposal.isExecuted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AegisSecureGreen.copy(alpha = 0.15f))
                        .border(1.dp, AegisSecureGreen, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Patched",
                        tint = AegisSecureGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HUMAN AUTHORIZED & PATCHED IN REAL TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisSecureGreen
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AegisSecureGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("approve_patch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Approve & Apply Patch",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
