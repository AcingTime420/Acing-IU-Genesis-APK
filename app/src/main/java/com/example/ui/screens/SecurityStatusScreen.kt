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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.security.CapabilityItem
import com.example.security.GenesisCapabilityRegistry
import com.example.security.GenesisLayer
import com.example.security.LiveAuditReport
import com.example.security.MaturityLevel
import com.example.security.SecurityAuditService
import com.example.security.SecurityTestFramework
import com.example.security.TestExecutionReport
import com.example.ui.AcingViewModel
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTerminalGreen
import com.example.ui.theme.AegisTerminalRed
import com.example.ui.theme.AegisTerminalTextPrimary
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityStatusScreen(
    viewModel: AcingViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auditService = remember { SecurityAuditService() }
    val testFramework = remember { SecurityTestFramework() }

    var selectedLayerFilter by remember { mutableStateOf<GenesisLayer?>(null) }
    var selectedMaturityFilter by remember { mutableStateOf<MaturityLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var capabilitiesList by remember { mutableStateOf(GenesisCapabilityRegistry.getAllCapabilities()) }
    var matrixStats by remember { mutableStateOf(GenesisCapabilityRegistry.getStatistics()) }

    var liveAuditReport by remember { mutableStateOf<LiveAuditReport?>(null) }
    var testExecutionReport by remember { mutableStateOf<TestExecutionReport?>(null) }

    var showAuditDialog by remember { mutableStateOf(false) }
    var showTestReportDialog by remember { mutableStateOf(false) }
    var expandedCapabilityId by remember { mutableStateOf<String?>(null) }

    fun refreshMatrixData() {
        capabilitiesList = GenesisCapabilityRegistry.getAllCapabilities()
        matrixStats = GenesisCapabilityRegistry.getStatistics()
    }

    val filteredCapabilities = capabilitiesList.filter { item ->
        val matchesLayer = selectedLayerFilter == null || item.layer == selectedLayerFilter
        val matchesMaturity = selectedMaturityFilter == null || item.maturityLevel == selectedMaturityFilter
        val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.genesisTargetDescription.contains(searchQuery, ignoreCase = true) ||
                item.knoxComparisonBaseline.contains(searchQuery, ignoreCase = true) ||
                item.primaryClass.contains(searchQuery, ignoreCase = true)
        matchesLayer && matchesMaturity && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AegisDarkBg)
            .padding(16.dp)
            .testTag("security_status_screen")
    ) {
        // 1. Screen Header Title & Version Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Genesis 4-Layer Security Matrix",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = AegisTextPrimary
                )
                Text(
                    text = "Engineering Capability Verification & Runtime Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = AegisTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisBadgeIndigoBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Verified",
                        tint = AegisBadgeIndigoText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${matrixStats.implementationPercentage}% EXECUTABLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Overview Stats Cards Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatMetricCard(
                title = "Total Capabilities",
                value = matrixStats.totalCount.toString(),
                subtitle = "4 Architecture Layers",
                badgeColor = AegisSurface,
                textColor = AegisTextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Verified Executable",
                value = matrixStats.verifiedCount.toString(),
                subtitle = "Passed Unit Tests",
                badgeColor = Color(0xFFE8F5E9),
                textColor = AegisSecureGreen,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Limitations / Sim",
                value = (matrixStats.limitationsCount + matrixStats.simulatedCount).toString(),
                subtitle = "OS / App Scope",
                badgeColor = Color(0xFFFFF8E1),
                textColor = AegisWarningGold,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Specs / HW Targets",
                value = (matrixStats.specificationCount + matrixStats.hardwareDependentCount).toString(),
                subtitle = "AOSP & Silicon",
                badgeColor = Color(0xFFEDE7F6),
                textColor = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Action Buttons Bar (Run Live System Audit & Execute Security Unit Tests)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    liveAuditReport = auditService.performLiveSecurityAudit(context)
                    refreshMatrixData()
                    showAuditDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("run_live_audit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Run Live Audit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    testExecutionReport = testFramework.runFullSecurityTestSuite()
                    refreshMatrixData()
                    showTestReportDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("execute_tests_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Run Unit Tests", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Layer Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedLayerFilter?.layerNumber ?: 0,
            containerColor = AegisSurface,
            contentColor = AegisTextPrimary,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                val index = selectedLayerFilter?.layerNumber ?: 0
                if (index < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = AegisPrimaryCyan,
                        height = 3.dp
                    )
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, AegisBorder, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedLayerFilter == null,
                onClick = { selectedLayerFilter = null },
                text = { Text("ALL LAYERS (${capabilitiesList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            GenesisLayer.values().forEach { layer ->
                val count = capabilitiesList.count { it.layer == layer }
                Tab(
                    selected = selectedLayerFilter == layer,
                    onClick = { selectedLayerFilter = layer },
                    text = {
                        Text(
                            text = "L${layer.layerNumber}: ${layer.name.substringBefore('—').trim()} ($count)",
                            fontSize = 11.sp,
                            fontWeight = if (selectedLayerFilter == layer) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Maturity Filter Chips & Search Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedMaturityFilter == null,
                    onClick = { selectedMaturityFilter = null },
                    label = { Text("All Statuses", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AegisBadgeIndigoBg,
                        selectedLabelColor = AegisBadgeIndigoText
                    )
                )
            }
            items(MaturityLevel.values()) { level ->
                FilterChip(
                    selected = selectedMaturityFilter == level,
                    onClick = {
                        selectedMaturityFilter = if (selectedMaturityFilter == level) null else level
                    },
                    label = { Text(level.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(level.badgeColorHex),
                        selectedLabelColor = Color(level.textColorHex)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. Capability Items List
        Text(
            text = "Showing ${filteredCapabilities.size} Capabilities",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = AegisTextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredCapabilities, key = { it.id }) { item ->
                CapabilityCard(
                    item = item,
                    isExpanded = expandedCapabilityId == item.id,
                    onToggleExpand = {
                        expandedCapabilityId = if (expandedCapabilityId == item.id) null else item.id
                    }
                )
            }
        }
    }

    // Live Audit Result Dialog
    liveAuditReport?.let { report ->
        if (showAuditDialog) {
            AlertDialog(
                onDismissRequest = { showAuditDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AegisSecureGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Live System Audit Results", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .background(AegisTerminalBg, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = report.auditSummaryText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTerminalTextPrimary
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showAuditDialog = false }) {
                        Text("Close Audit Report")
                    }
                }
            )
        }
    }

    // Test Execution Report Dialog
    testExecutionReport?.let { report ->
        if (showTestReportDialog) {
            AlertDialog(
                onDismissRequest = { showTestReportDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (report.failCount == 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (report.failCount == 0) AegisSecureGreen else AegisDangerRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Runner: ${report.passCount}/${report.totalTestsRun} Passed",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .height(260.dp)
                            .fillMaxWidth()
                            .background(AegisTerminalBg, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        item {
                            Text(
                                text = report.summary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTerminalGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(
                                color = AegisTextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                        items(report.caseResults) { case ->
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (case.status == com.example.security.TestResultStatus.PASSED) "✓ [PASS]" else "✗ [FAIL]",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (case.status == com.example.security.TestResultStatus.PASSED) AegisTerminalGreen else AegisTerminalRed
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = case.testName,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = AegisTerminalTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "  Module: ${case.targetModule} (${case.executionTimeMs} ms)",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisTextMuted
                                )
                                Text(
                                    text = "  ${case.details}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisTerminalTextPrimary
                                )
                                case.failureCause?.let { cause ->
                                    Text(
                                        text = "  ERROR: $cause",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = AegisTerminalRed
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showTestReportDialog = false }) {
                        Text("Acknowledge Results")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    badgeColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = badgeColor),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = AegisTextSecondary,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 8.sp,
                color = AegisTextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CapabilityCard(
    item: CapabilityItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(8.dp))
            .clickable { onToggleExpand() }
            .testTag("capability_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Layer & Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AegisBadgeIndigoBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "L${item.layer.layerNumber} • ${item.layer.title.substringBefore('—').trim()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }

                MaturityStatusBadge(level = item.maturityLevel)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Capability Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = AegisTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Knox vs Genesis Comparison Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AegisDarkBg, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Knox Baseline: ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisTextSecondary
                    )
                    Text(
                        text = item.knoxComparisonBaseline,
                        fontSize = 10.sp,
                        color = AegisTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Genesis Target: ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AegisPrimaryCyan
                    )
                    Text(
                        text = item.genesisTargetDescription,
                        fontSize = 10.sp,
                        color = AegisTextPrimary
                    )
                }
            }

            // Expanded Details Section
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = AegisBorder)
                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(label = "Source Ref:", value = item.sourceFileRef, isMonospace = true)
                DetailRow(label = "Primary Class:", value = item.primaryClass, isMonospace = true)
                DetailRow(
                    label = "Required Perms:",
                    value = if (item.requiredPermissions.isEmpty()) "None (Application Scope)" else item.requiredPermissions.joinToString(", ")
                )
                DetailRow(label = "Live Verification Evidence:", value = item.runtimeVerificationEvidence)
                DetailRow(label = "Known Limitations:", value = item.knownLimitations)
                DetailRow(label = "Last Verified:", value = item.getFormattedTimestamp(), isMonospace = true)
            }
        }
    }
}

@Composable
private fun MaturityStatusBadge(level: MaturityLevel) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(level.badgeColorHex))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = level.label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color(level.textColorHex)
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AegisTextMuted
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            color = AegisTextPrimary
        )
    }
}
