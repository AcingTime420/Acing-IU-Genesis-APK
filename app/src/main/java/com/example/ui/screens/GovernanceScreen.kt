package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.BuildConfig
import com.example.ui.AcingViewModel
import com.example.ui.SecurityRole
import com.example.ui.components.AgentRosterGovernanceComponent
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
fun GovernanceScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val governanceAuditResult by viewModel.governanceAuditResult.collectAsState()
    val isAuditingGovernance by viewModel.isAuditingGovernance.collectAsState()

    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
    val apiKeyConfigured = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Governance & Access Control",
                subtitle = "Role-Based Access Control (RBAC) & secrets verification",
                icon = Icons.Default.AdminPanelSettings
            )
        }

        item {
            com.example.ui.components.ShopifyLicenseCard(viewModel = viewModel)
        }

        item {
            com.example.ui.components.KnoxVsGenesisSecurityMatrixComponent()
        }

        if (governanceAuditResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisSecureGreen),
                    modifier = Modifier.fillMaxWidth().testTag("ai_governance_audit_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = AegisSecureGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AEGIS AI GOVERNANCE FINDINGS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = AegisSecureGreen
                                )
                            }
                            androidx.compose.material3.OutlinedButton(
                                onClick = { viewModel.clearGovernanceAudit() },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 10.sp, color = AegisTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = governanceAuditResult ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rbac_role_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ROLE-BASED ACCESS CONTROL (RBAC)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                    Text(
                        text = "Select active operator authority profile for audit logging and policy execution.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SecurityRole.entries.forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(AegisDarkBg, RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (currentRole == role) AegisPrimaryCyan else AegisBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setSecurityRole(role) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentRole == role,
                                onClick = { viewModel.setSecurityRole(role) },
                                colors = RadioButtonDefaults.colors(selectedColor = AegisPrimaryCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = role.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AegisTextPrimary
                                )
                                Text(
                                    text = role.level,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Environment Secrets & Build Verification",
                subtitle = "Secrets Gradle Plugin & BuildConfig integrity",
                icon = Icons.Default.Key
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
                    val apiKeyConfigured = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GEMINI_API_KEY (Secrets Panel)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )
                        SeverityBadge(severity = if (apiKeyConfigured) "SECURE" else "WARNING")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (apiKeyConfigured) "API Key securely injected via BuildConfig.GEMINI_API_KEY" else "GEMINI_API_KEY is not configured in Secrets panel. Local AI simulation active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val configRows = listOf(
                        "Application ID" to "com.aistudio.acingiugenesis.sec",
                        "Target SDK" to "Android 15 (API 35)",
                        "ProGuard Obfuscation" to "Enabled (proguard-rules.pro)",
                        "Database Encryption" to "Room SQLite Local Storage"
                    )

                    configRows.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = key, fontSize = 12.sp, color = AegisTextSecondary)
                            Text(
                                text = value,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisSecureGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    androidx.compose.material3.Button(
                        onClick = { viewModel.auditGovernanceWithAi(apiKeyConfigured) },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = AegisPrimaryCyan,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        enabled = !isAuditingGovernance,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_governance_ai_audit_button")
                    ) {
                        if (isAuditingGovernance) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = androidx.compose.ui.graphics.Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auditing Governance...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audit Governance & Secrets with AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            val lastEvaluation by viewModel.lastAgentEvaluation.collectAsState()
            AgentRosterGovernanceComponent(
                onEvaluateRequest = { agent, op, targetType, targetId, reqLevel ->
                    viewModel.evaluateAgentActionRequest(agent, op, targetType, targetId, reqLevel)
                },
                lastEvaluation = lastEvaluation
            )
        }

        item {
            SectionHeader(
                title = "Aegis Security Engineering Manifesto",
                subtitle = "Acing IU: Genesis core principles",
                icon = Icons.Default.Shield
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "\"Secure by Design. Verified by Evidence. Documented for the Future. Engineered to Last.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Security before convenience.\n2. Least privilege & zero-trust defaults.\n3. Immutable cryptographic auditability.\n4. Evidence-driven engineering.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AegisTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
