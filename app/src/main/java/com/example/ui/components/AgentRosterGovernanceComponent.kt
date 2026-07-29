package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.AgentActionEvaluation
import com.example.agent.AgentAuthorityLevel
import com.example.agent.AgentIdentity
import com.example.agent.GenesisAgentRoster
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisPrimaryBlue
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentRosterGovernanceComponent(
    onEvaluateRequest: (
        agent: AgentIdentity,
        operation: String,
        targetType: String,
        targetId: String,
        requestedAuthorityLevel: AgentAuthorityLevel
    ) -> Unit,
    lastEvaluation: AgentActionEvaluation?,
    modifier: Modifier = Modifier
) {
    var selectedAgent by remember { mutableStateOf(GenesisAgentRoster.ALL_AGENTS.first()) }
    var operationInput by remember { mutableStateOf("Inspect boot.img partition signature") }
    var targetTypeInput by remember { mutableStateOf("firmware-image") }
    var targetIdInput by remember { mutableStateOf("acing-iu-genesis-v1.3.1.img") }
    var requestedLevel by remember { mutableStateOf(AgentAuthorityLevel.LEVEL_1) }

    var authorityDropdownExpanded by remember { mutableStateOf(false) }
    var agentDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("agent_roster_governance_component")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AegisPrimaryCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SPECIALIZED AGENT AIS ROSTER & GOVERNANCE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "14 Policy-Governed Digital Operators | Code of Conduct Enforced",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisSecureGreen.copy(alpha = 0.15f))
                        .border(1.dp, AegisSecureGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LEAST PRIVILEGE ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisSecureGreen
                    )
                }
            }

            // Interactive Agent Request Simulator Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AGENT ACTION & POLICY EVALUATION SIMULATOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisPrimaryCyan
                            )
                        }
                    }

                    // Agent Selector & Authority Level Dropdowns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Agent Selector Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedAgent.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Agent AI", fontSize = 10.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { agentDropdownExpanded = true }
                                    .testTag("agent_selector_dropdown"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AegisPrimaryCyan,
                                    unfocusedBorderColor = AegisBorder,
                                    focusedTextColor = AegisTextPrimary,
                                    unfocusedTextColor = AegisTextPrimary
                                ),
                                singleLine = true
                            )
                            DropdownMenu(
                                expanded = agentDropdownExpanded,
                                onDismissRequest = { agentDropdownExpanded = false }
                            ) {
                                GenesisAgentRoster.ALL_AGENTS.forEach { agent ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(agent.displayName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Role: ${agent.assignedRole} | Max Cap: ${agent.maxAuthorityLevel.title}", fontSize = 10.sp, color = AegisTextMuted)
                                            }
                                        },
                                        onClick = {
                                            selectedAgent = agent
                                            agentDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Authority Level Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = "L${requestedLevel.levelNumber}: ${requestedLevel.title}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Requested Authority Level", fontSize = 10.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { authorityDropdownExpanded = true }
                                    .testTag("authority_level_dropdown"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AegisPrimaryCyan,
                                    unfocusedBorderColor = AegisBorder,
                                    focusedTextColor = AegisTextPrimary,
                                    unfocusedTextColor = AegisTextPrimary
                                ),
                                singleLine = true
                            )
                            DropdownMenu(
                                expanded = authorityDropdownExpanded,
                                onDismissRequest = { authorityDropdownExpanded = false }
                            ) {
                                AgentAuthorityLevel.entries.forEach { lvl ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("Level ${lvl.levelNumber}: ${lvl.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(lvl.description, fontSize = 10.sp, color = AegisTextMuted)
                                            }
                                        },
                                        onClick = {
                                            requestedLevel = lvl
                                            authorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Operation Text Input
                    OutlinedTextField(
                        value = operationInput,
                        onValueChange = { operationInput = it },
                        label = { Text("Action Operation Name", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("agent_operation_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AegisPrimaryCyan,
                            unfocusedBorderColor = AegisBorder,
                            focusedTextColor = AegisTextPrimary,
                            unfocusedTextColor = AegisTextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = targetTypeInput,
                            onValueChange = { targetTypeInput = it },
                            label = { Text("Target Resource Type", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AegisPrimaryCyan,
                                unfocusedBorderColor = AegisBorder,
                                focusedTextColor = AegisTextPrimary,
                                unfocusedTextColor = AegisTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = targetIdInput,
                            onValueChange = { targetIdInput = it },
                            label = { Text("Target Resource ID / Path", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AegisPrimaryCyan,
                                unfocusedBorderColor = AegisBorder,
                                focusedTextColor = AegisTextPrimary,
                                unfocusedTextColor = AegisTextPrimary
                            )
                        )
                    }

                    // Evaluate Button
                    Button(
                        onClick = {
                            onEvaluateRequest(
                                selectedAgent,
                                operationInput,
                                targetTypeInput,
                                targetIdInput,
                                requestedLevel
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("evaluate_agent_action_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EVALUATE CODE OF CONDUCT & REQUEST ACTION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Display Policy Evaluation Result Box
                    if (lastEvaluation != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        when (lastEvaluation) {
                            is AgentActionEvaluation.AutoApproved -> {
                                EvaluationResultCard(
                                    title = "POLICY RESULT: AUTO-APPROVED (LEVEL ${lastEvaluation.approvalRecord.authorityLevel})",
                                    color = AegisSecureGreen,
                                    icon = Icons.Default.CheckCircle,
                                    details = lastEvaluation.policyReason,
                                    recordJson = formatApprovalRecordJson(lastEvaluation.approvalRecord)
                                )
                            }
                            is AgentActionEvaluation.RequiresHumanApproval -> {
                                EvaluationResultCard(
                                    title = "POLICY RESULT: HUMAN APPROVAL REQUIRED (LEVEL ${lastEvaluation.approvalRecord.authorityLevel})",
                                    color = AegisWarningGold,
                                    icon = Icons.Default.Warning,
                                    details = lastEvaluation.warningDetails,
                                    recordJson = formatApprovalRecordJson(lastEvaluation.approvalRecord)
                                )
                            }
                            is AgentActionEvaluation.Denied -> {
                                EvaluationResultCard(
                                    title = "POLICY RESULT: DENIED BY GOVERNANCE POLICY",
                                    color = AegisDangerRed,
                                    icon = Icons.Default.Lock,
                                    details = "${lastEvaluation.denialReason}\nViolated: ${lastEvaluation.codeOfConductArticle}",
                                    recordJson = null
                                )
                            }
                        }
                    }
                }
            }

            // Roster of All 14 Agents
            Text(
                text = "APPROVED GENESIS AGENT ROSTER (${GenesisAgentRoster.ALL_AGENTS.size} AGENTS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AegisTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GenesisAgentRoster.ALL_AGENTS.forEach { agent ->
                    AgentRosterCardItem(
                        agent = agent,
                        isSelected = agent.id == selectedAgent.id,
                        onSelect = { selectedAgent = agent }
                    )
                }
            }
        }
    }
}

@Composable
fun EvaluationResultCard(
    title: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    details: String,
    recordJson: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier.fillMaxWidth().testTag("evaluation_result_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = details, fontSize = 11.sp, color = AegisTextPrimary)

            if (recordJson != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AGENT APPROVAL RECORD JSON:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisSurface)
                        .border(1.dp, AegisBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = recordJson,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryCyan
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentRosterCardItem(
    agent: AgentIdentity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AegisPrimaryCyan.copy(alpha = 0.15f) else AegisSurfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AegisPrimaryCyan else AegisBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("agent_item_${agent.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AegisPrimaryCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = agent.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AegisPrimaryBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "MAX CAP: LEVEL ${agent.maxAuthorityLevel.levelNumber}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = agent.purpose,
                fontSize = 11.sp,
                color = AegisTextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AegisSurface)
                        .border(1.dp, AegisBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ROLE: ${agent.assignedRole}",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextMuted
                    )
                }

                agent.approvedTools.take(3).forEach { tool ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AegisSurface)
                            .border(1.dp, AegisBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TOOL: $tool",
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

private fun formatApprovalRecordJson(record: com.example.agent.AgentApprovalRecord): String {
    return """
{
  "requestId": "${record.requestId}",
  "agentId": "${record.agentId}",
  "agentVersion": "${record.agentVersion}",
  "requestedBy": "${record.requestedBy}",
  "targetType": "${record.targetType}",
  "targetId": "${record.targetId}",
  "operation": "${record.operation}",
  "authorityLevel": ${record.authorityLevel},
  "authorizationReference": "${record.authorizationReference}",
  "riskClassification": "${record.riskClassification}",
  "dataLossPossible": ${record.dataLossPossible},
  "backupRequired": ${record.backupRequired},
  "approvalRequired": ${record.approvalRequired},
  "approvalStatus": "${record.approvalStatus}",
  "approvedBy": "${record.approvedBy}",
  "approvedAtUtc": "${record.approvedAtUtc}",
  "expiresAtUtc": "${record.expiresAtUtc}",
  "policyVersion": "${record.policyVersion}",
  "correlationId": "${record.correlationId}"
}
    """.trimIndent()
}
