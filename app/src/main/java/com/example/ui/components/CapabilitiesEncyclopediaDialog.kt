package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.AcingViewModel
import com.example.ui.AppTab
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * Data structure detailing an individual function's operational purpose, technical architecture,
 * security role level, and output artifact.
 */
data class FunctionCapabilityItem(
    val id: String,
    val name: String,
    val category: FunctionCategory,
    val icon: ImageVector,
    val purpose: String,
    val capabilities: List<String>,
    val technicalArchitecture: String,
    val requiredRole: String,
    val producedArtifact: String,
    val targetTab: AppTab
)

enum class FunctionCategory(val label: String) {
    ALL("All Subsystems"),
    CORE_SECURITY("Core & Matrix"),
    FIRMWARE_BOOT("Firmware & Boot"),
    NETWORK_DEFENSE("Network Defense"),
    FORENSICS_AUDIT("Forensics & Audit"),
    AI_COPILOT("Aegis AI Copilot"),
    GOVERNANCE_RBAC("Governance & RBAC"),
    BIOMETRICS_HARDWARE("Biometrics & Hardware")
}

val allCapabilityItems: List<FunctionCapabilityItem> = listOf(
    FunctionCapabilityItem(
        id = "device_trust_index",
        name = "Device Trust Index (DTI) Scoring Engine",
        category = FunctionCategory.CORE_SECURITY,
        icon = Icons.Default.Speed,
        purpose = "Evaluates multi-factor hardware security signals to produce a normalized 0-100 real-time security posture score.",
        capabilities = listOf(
            "Aggregates 10 zero-trust vectors into a single trusted baseline",
            "Calculates dynamic penalty weights for unlocked bootloaders and unpinned TLS",
            "Provides instantaneous executive posture indicators"
        ),
        technicalArchitecture = "Calculated via DeviceTrustService by sampling hardware keystore attestation, SELinux kernel states, and AVB 2.0 tree validation.",
        requiredRole = "Available to all roles (Principal, Auditor, Engineer)",
        producedArtifact = "Normalized DTI Score (0-100) & Real-time Trust Posture Banner",
        targetTab = AppTab.DASHBOARD
    ),
    FunctionCapabilityItem(
        id = "avb_boot_chain",
        name = "AVB 2.0 Android Verified Boot Validator",
        category = FunctionCategory.CORE_SECURITY,
        icon = Icons.Default.Shield,
        purpose = "Cryptographically verifies the authenticity and integrity of boot partitions from bootloader to system image.",
        capabilities = listOf(
            "Validates vbmeta.img RSA-4096 signature against Genesis OEM root key",
            "Verifies dm-verity Merkle tree root hashes for partition tamper-proofing",
            "Flags yellow/orange/red boot states caused by custom ROMs or modified boot images"
        ),
        technicalArchitecture = "AVB 2.0 specification parser with SHA-256 digest calculation across partition descriptors.",
        requiredRole = "Security Auditor & Principal Architect",
        producedArtifact = "AVB 2.0 Boot Chain Verification State & Tamper Alert Logs",
        targetTab = AppTab.SECURITY_STATUS
    ),
    FunctionCapabilityItem(
        id = "selinux_enforcement",
        name = "SELinux Policy Generator & Enforcer",
        category = FunctionCategory.CORE_SECURITY,
        icon = Icons.Default.Security,
        purpose = "Enforces Mandatory Access Control (MAC) domain boundaries and generates Type Enforcement (.te) security rules.",
        capabilities = listOf(
            "Detects Permissive or Disabled SELinux states",
            "Generates custom Knox & AOSP Type Enforcement (.te) macro policies",
            "Isolates rogue system services from accessing raw device block nodes"
        ),
        technicalArchitecture = "SelinuxPolicyGenerator engine generating audit2allow-compatible policy rules and checking /sys/fs/selinux/enforce.",
        requiredRole = "Principal Architect",
        producedArtifact = "SELinux Type Enforcement (.te) Policy File & Enforcing Mode Switch",
        targetTab = AppTab.SECURITY_STATUS
    ),
    FunctionCapabilityItem(
        id = "firmware_partition_analyzer",
        name = "Multi-Threaded Firmware Partition Analyzer",
        category = FunctionCategory.FIRMWARE_BOOT,
        icon = Icons.Default.FolderZip,
        purpose = "Performs deep multi-stage binary inspection on flashed partition images (boot, vbmeta, vendor_boot, dtbo).",
        capabilities = listOf(
            "Calculates real-time throughput in Megabytes/sec (MB/s)",
            "Parses Android Boot Header v2/v3/v4 magic signatures",
            "Computes cryptographic SHA-256 and SHA-1 checksums",
            "Simulates background firmware partition extraction and integrity checks"
        ),
        technicalArchitecture = "BuildPropAndFirmwareSecurityEngine leveraging Kotlin Coroutines for asynchronous streaming file parsing.",
        requiredRole = "Systems Engineer & Principal Architect",
        producedArtifact = "Firmware Partition Analysis Report & Integrity Verification Summary",
        targetTab = AppTab.FIRMWARE
    ),
    FunctionCapabilityItem(
        id = "shannon_entropy_analyzer",
        name = "Shannon Byte Entropy Anomaly Engine",
        category = FunctionCategory.FIRMWARE_BOOT,
        icon = Icons.Default.AutoFixHigh,
        purpose = "Analyzes raw binary byte distributions to detect encrypted payloads, obfuscated shellcode, and hidden packers.",
        capabilities = listOf(
            "Measures byte randomness on a 0.0 to 8.0 Shannon scale",
            "Identifies high-entropy regions (>7.90) indicative of compressed or encrypted malware",
            "Visualizes entropy curves across individual partition blocks"
        ),
        technicalArchitecture = "Mathematical Shannon entropy calculation: -Σ p(x) * log2(p(x)) evaluated over 512-byte sliding buffer windows.",
        requiredRole = "Security Auditor & Principal Architect",
        producedArtifact = "Entropy Score Gauge & Packer Anomaly Classification",
        targetTab = AppTab.FIRMWARE
    ),
    FunctionCapabilityItem(
        id = "fallback_security_policy",
        name = "Fallback Security Policy Module (Air-Gapped)",
        category = FunctionCategory.FIRMWARE_BOOT,
        icon = Icons.Default.Lock,
        purpose = "Automatically engages Restricted Mode on network disconnect and executes local-only TFLite signature verification.",
        capabilities = listOf(
            "Detects network disconnects via ConnectivityManager NetworkCallback",
            "Switches platform to Restricted Air-Gapped Mode instantly",
            "Runs local-only pre-bundled TFLite models for binary classification",
            "Allows zero-trust simulated air-gapped laboratory testing"
        ),
        technicalArchitecture = "FallbackSecurityPolicyModule with pre-bundled TensorFlow Lite neural models, token vocabularies, and local Room audit storage.",
        requiredRole = "All Roles",
        producedArtifact = "Restricted Mode Policy State & Local TFLite Verification Results",
        targetTab = AppTab.FIRMWARE
    ),
    FunctionCapabilityItem(
        id = "odin_verifier",
        name = "Samsung Odin .tar.md5 Archive Verifier",
        category = FunctionCategory.FIRMWARE_BOOT,
        icon = Icons.Default.PhoneAndroid,
        purpose = "Validates Samsung official factory flash packages and PIT (Partition Information Table) binary integrity.",
        capabilities = listOf(
            "Verifies trailer MD5 checksums on concatenated TAR archives",
            "Parses embedded PIT tables for partition sizing and flash layout",
            "Validates OEM digital signatures on CSC, AP, CP, and BL binaries"
        ),
        technicalArchitecture = "OdinFirmwareVerifier inspecting tar file directory blocks and calculating trailing 16-byte MD5 signatures.",
        requiredRole = "Systems Engineer",
        producedArtifact = "Odin Flash Package Verification Certificate",
        targetTab = AppTab.FIRMWARE
    ),
    FunctionCapabilityItem(
        id = "cert_pinning_engine",
        name = "TLS Public Key Certificate Pinning Engine",
        category = FunctionCategory.NETWORK_DEFENSE,
        icon = Icons.Default.Shield,
        purpose = "Restricts outbound telemetry and management connections strictly to pre-configured Genesis cryptographic public keys.",
        capabilities = listOf(
            "Prevents Man-in-the-Middle (MitM) TLS inspection by rogue root certificates",
            "Enforces SHA-256 SPKI key pinning across all API endpoints",
            "Provides instant UI switch to enable/disable strict pinning"
        ),
        technicalArchitecture = "NetworkSecurityConfig enforcement with OkHttp CertificatePinner integration.",
        requiredRole = "Principal Architect",
        producedArtifact = "Active TLS Pinning State & MitM Prevention Audit Log",
        targetTab = AppTab.DEVICES
    ),
    FunctionCapabilityItem(
        id = "network_vulnerability_scanner",
        name = "Live Network & Interface Vulnerability Scanner",
        category = FunctionCategory.NETWORK_DEFENSE,
        icon = Icons.Default.Wifi,
        purpose = "Scans active network interfaces (WiFi, Cellular, VPN) to detect rogue open ports, DHCP anomalies, and DNS spoofing.",
        capabilities = listOf(
            "Enumerates open inbound network ports and listening sockets",
            "Detects rogue DHCP servers offering unvalidated gateway routes",
            "Checks DNS resolvers against DNS cache poisoning signatures"
        ),
        technicalArchitecture = "NetworkVulnerabilityScanner probing local socket endpoints and inspecting Android NetworkCapabilities.",
        requiredRole = "Systems Engineer & Security Auditor",
        producedArtifact = "Network Vulnerability Assessment Report & Port Status",
        targetTab = AppTab.DEVICES
    ),
    FunctionCapabilityItem(
        id = "rogue_port_isolation",
        name = "Rogue Port & ADB Emergency Isolation Kill Switch",
        category = FunctionCategory.NETWORK_DEFENSE,
        icon = Icons.Default.Close,
        purpose = "Instantly shuts down unauthorized USB debugging bridges, external ADB routes, and non-pinned connections.",
        capabilities = listOf(
            "Terminates rogue TCP debugging ports (5555, 5037)",
            "Disconnects unpinned telemetry bridges immediately",
            "Locks down hardware communication channels in containment mode"
        ),
        technicalArchitecture = "Emergency socket shutdown and Android Settings secure ADB state toggle.",
        requiredRole = "Principal Architect",
        producedArtifact = "Port Isolation Status & Emergency Containment Log",
        targetTab = AppTab.DEVICES
    ),
    FunctionCapabilityItem(
        id = "forensic_audit_ledger",
        name = "Tamper-Evident Forensic Incident Ledger",
        category = FunctionCategory.FORENSICS_AUDIT,
        icon = Icons.Default.BugReport,
        purpose = "Records every security event, configuration change, and remediation action into an immutable Room database ledger.",
        capabilities = listOf(
            "Computes SHA-256 HMAC cryptographic signatures for each audit record",
            "Provides chronological timeline with severity filters (CRITICAL, ALERT, SECURE, INFO)",
            "Stores actor role, timestamp, subsystem category, and forensic metadata"
        ),
        technicalArchitecture = "SecurityAuditDatabase backed by Android Room and SQLite with cryptographic hash chaining.",
        requiredRole = "All Roles (Read) / SecOps Admin (Write)",
        producedArtifact = "Cryptographic Audit Log Stream & Incident Ledger View",
        targetTab = AppTab.FORENSICS
    ),
    FunctionCapabilityItem(
        id = "signed_csv_exporter",
        name = "Forensic CSV Export Service with FileProvider",
        category = FunctionCategory.FORENSICS_AUDIT,
        icon = Icons.Default.Description,
        purpose = "Generates tamper-evident, cryptographically signed CSV audit logs and shares them securely via Android FileProvider.",
        capabilities = listOf(
            "Exports full forensic event history to RFC-4180 compliant CSV files",
            "Attaches cryptographic SHA-256 HMAC manifest headers",
            "Dispatches secure content:// URI via system share sheets without file exposure"
        ),
        technicalArchitecture = "SecurityAuditCsvExportService backed by Android FileProvider (res/xml/file_paths.xml).",
        requiredRole = "Security Auditor & Principal Architect",
        producedArtifact = "Signed .csv File Shared via System Intent",
        targetTab = AppTab.FORENSICS
    ),
    FunctionCapabilityItem(
        id = "aegis_ai_copilot",
        name = "Aegis AI Security Copilot (Gemini Pro/Flash)",
        category = FunctionCategory.AI_COPILOT,
        icon = Icons.Default.Psychology,
        purpose = "Provides autonomous, context-aware AI threat analysis, zero-day CVE correlation, and remediation script generation.",
        capabilities = listOf(
            "Multi-step Thinking Mode for transparent chain-of-thought security rationale",
            "Automated zero-trust prompt suggestions based on current device telemetry",
            "Generates actionable remediation code (SELinux rules, shell fixes, config updates)",
            "Evaluates proposal safety before executing platform remediations"
        ),
        technicalArchitecture = "AegisAiService integrating Gemini Pro / Flash models via REST API and generative AI endpoints.",
        requiredRole = "All Roles",
        producedArtifact = "AI Threat Intelligence Briefings, Remediation Plans, and Thinking Transcripts",
        targetTab = AppTab.AEGIS_AI
    ),
    FunctionCapabilityItem(
        id = "rbac_access_control",
        name = "Role-Based Access Control (RBAC) Module",
        category = FunctionCategory.GOVERNANCE_RBAC,
        icon = Icons.Default.AdminPanelSettings,
        purpose = "Enforces strict authorization boundaries across three operational tiers to prevent unauthorized modifications.",
        capabilities = listOf(
            "Principal Architect (Level 5): Full policy generation, lockdown, and firmware flashing",
            "Security Auditor (Level 4): Read-only forensic review, CSV export, and attestation audits",
            "Systems Engineer (Level 3): Diagnostics, partition analysis, and network scanning"
        ),
        technicalArchitecture = "AgentGovernanceService enforcing role capabilities across view model dispatchers.",
        requiredRole = "Principal Architect (to switch or elevate roles)",
        producedArtifact = "Current Role Authority Badge & Scoped Permission Enforcement",
        targetTab = AppTab.GOVERNANCE
    ),
    FunctionCapabilityItem(
        id = "biometric_gatekeeper",
        name = "Biometric Security Gatekeeper & Auto-Timeout",
        category = FunctionCategory.GOVERNANCE_RBAC,
        icon = Icons.Default.Fingerprint,
        purpose = "Protects sensitive security operations with hardware biometric authentication and a 5-minute background auto-lock.",
        capabilities = listOf(
            "Requires Class 3 Biometric (Fingerprint/Face) or Device Credential",
            "Automatically locks application session after 5 minutes in background",
            "Prevents shoulder-surfing and unauthorized physical access"
        ),
        technicalArchitecture = "BiometricSecurityManager and SecurityGatekeeper observing ProcessLifecycleOwner.",
        requiredRole = "All Roles",
        producedArtifact = "Biometric Verification Gate & Authenticated Session Token",
        targetTab = AppTab.GOVERNANCE
    ),
    FunctionCapabilityItem(
        id = "fingerprint_action_mapping",
        name = "Multi-Fingerprint Biometric Action Mapping",
        category = FunctionCategory.BIOMETRICS_HARDWARE,
        icon = Icons.Default.Fingerprint,
        purpose = "Maps specific enrolled fingers (Right Index, Left Ring, Left Pinky) to discrete mission-critical security actions.",
        capabilities = listOf(
            "Right Index: Authorized FRP Reset & Knox Attestation Reset",
            "Left Ring: Emergency Master Zero-Trust Lockdown Trigger",
            "Left Pinky: Forensic Cache & Network Telemetry Purge"
        ),
        technicalArchitecture = "BiometricSecurityManager mapping biometric prompt IDs to discrete SecurityActionType handlers.",
        requiredRole = "Principal Architect & Security Auditor",
        producedArtifact = "Fingerprint Action Bindings & Executed Trigger Audit Records",
        targetTab = AppTab.DEVICE_SECURITY
    ),
    FunctionCapabilityItem(
        id = "predictive_keyboard_engine",
        name = "Acing Matrix Predictive Auto-Typing Engine",
        category = FunctionCategory.BIOMETRICS_HARDWARE,
        icon = Icons.Default.PhoneAndroid,
        purpose = "On-device next-word predictive model engine for automated security pass-phrases and auto-typing simulation.",
        capabilities = listOf(
            "Local N-gram frequency model & TFLite candidate probability scoring",
            "Accessibility Service integration for auto-injecting verified security credentials",
            "Real-time keystroke latency and confidence visualization"
        ),
        technicalArchitecture = "AcingPredictiveKeyboardManager loading index_word.json and word_index.json assets.",
        requiredRole = "Systems Engineer & Principal Architect",
        producedArtifact = "Predicted Word Candidates & Keystroke Probability Matrices",
        targetTab = AppTab.DEVICE_SECURITY
    ),
    FunctionCapabilityItem(
        id = "shopify_license_validator",
        name = "Shopify Enterprise License Validator",
        category = FunctionCategory.BIOMETRICS_HARDWARE,
        icon = Icons.Default.CheckCircle,
        purpose = "Verifies cryptographic software entitlement licenses and binds authorized deployments to hardware identifiers.",
        capabilities = listOf(
            "Validates digital license signatures against Shopify partner public keys",
            "Computes SHA-256 hardware device fingerprint",
            "Enforces seat limits and tier features (Enterprise, Genesis Pro)"
        ),
        technicalArchitecture = "ShopifyLicenseValidator checking cryptographic HMAC license keys and expiration timestamps.",
        requiredRole = "Security Auditor & Principal Architect",
        producedArtifact = "Shopify License Verification Certificate & Entitlement Token",
        targetTab = AppTab.DEVICE_SECURITY
    ),
    FunctionCapabilityItem(
        id = "mitre_threat_intel",
        name = "MITRE ATT&CK Threat Intelligence Matrix",
        category = FunctionCategory.CORE_SECURITY,
        icon = Icons.Default.Shield,
        purpose = "Correlates active Android device vulnerabilities against industry standard MITRE ATT&CK mobile tactics.",
        capabilities = listOf(
            "Categorizes threats into Initial Access, Privilege Escalation, Credential Access, Defense Evasion",
            "Visualizes threat vector distributions via dynamic Donut Chart",
            "Matches active CVE identifiers with known exploitation vectors"
        ),
        technicalArchitecture = "Threat intelligence scoring engine computing weighted vector severity percentages.",
        requiredRole = "Security Auditor & Analyst",
        producedArtifact = "Threat Vector Distribution Chart & MITRE Technique Mapping",
        targetTab = AppTab.THREAT_INTEL
    )
)

/**
 * Comprehensive Functions & Capabilities Encyclopedia Dialog:
 * Details every single capability, its operational purpose, technical foundation,
 * and allows instant jumping to the active screen.
 */
@Composable
fun CapabilitiesEncyclopediaDialog(
    viewModel: AcingViewModel,
    onDismiss: () -> Unit,
    onNavigateToTab: (AppTab) -> Unit
) {
    val functionDirectory = allCapabilityItems

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FunctionCategory.ALL) }

    val filteredItems = remember(searchQuery, selectedCategory) {
        functionDirectory.filter { item ->
            val matchesCategory = selectedCategory == FunctionCategory.ALL || item.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.purpose.contains(searchQuery, ignoreCase = true) ||
                    item.technicalArchitecture.contains(searchQuery, ignoreCase = true) ||
                    item.capabilities.any { it.contains(searchQuery, ignoreCase = true) } ||
                    item.producedArtifact.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .widthIn(max = 760.dp)
                .testTag("capabilities_encyclopedia_dialog"),
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(18.dp)
            ) {
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AegisPrimaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Encyclopedia",
                                tint = AegisPrimaryCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Functions & Capabilities Encyclopedia",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AegisTextPrimary
                            )
                            Text(
                                text = "Detailed Architecture & Operational Purpose Directory (${filteredItems.size} Functions)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_encyclopedia_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AegisTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by function name, keyword, or architecture (e.g. TFLite, SELinux, CSV)...", fontSize = 12.sp, color = AegisTextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AegisTextSecondary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = AegisTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AegisDarkBg,
                        unfocusedContainerColor = AegisDarkBg,
                        focusedBorderColor = AegisPrimaryCyan,
                        unfocusedBorderColor = AegisBorder,
                        focusedTextColor = AegisTextPrimary,
                        unfocusedTextColor = AegisTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("encyclopedia_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FunctionCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else AegisTextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AegisPrimaryCyan,
                                containerColor = AegisDarkBg
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AegisPrimaryCyan else AegisBorder
                            ),
                            modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Items List
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No Results",
                                tint = AegisTextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No matching capabilities found", fontSize = 14.sp, color = AegisTextSecondary)
                            Text("Try adjusting your search terms or selecting a different category.", fontSize = 11.sp, color = AegisTextMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("encyclopedia_items_list"),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredItems, key = { it.id }) { item ->
                            FunctionCapabilityCard(
                                item = item,
                                onTryFunction = {
                                    onNavigateToTab(item.targetTab)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FunctionCapabilityCard(
    item: FunctionCapabilityItem,
    onTryFunction: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisDarkBg),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Title, Category Pill, Jump Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AegisBadgeIndigoBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = AegisBadgeIndigoText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                        Text(
                            text = item.category.label,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisBadgeIndigoText
                        )
                    }
                }

                // Quick Launch / Try Button
                Button(
                    onClick = onTryFunction,
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("try_function_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Try",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Launch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AegisPrimaryCyan)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Purpose & Operational Summary
            Text(
                text = item.purpose,
                fontSize = 11.sp,
                color = AegisTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Capabilities Checklist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisSurface)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SPECIFIC CAPABILITIES:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTextMuted
                )
                item.capabilities.forEach { capability ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", fontSize = 11.sp, color = AegisSecureGreen, fontWeight = FontWeight.Bold)
                        Text(
                            text = capability,
                            fontSize = 11.sp,
                            color = AegisTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical Architecture & Security Requirements
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Technical Architecture:",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextSecondary
                    )
                    Text(
                        text = item.technicalArchitecture,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryCyan,
                        modifier = Modifier.widthIn(max = 240.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Required Security Role:",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextSecondary
                    )
                    Text(
                        text = item.requiredRole,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisWarningGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Produced Output / Artifact:",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextSecondary
                    )
                    Text(
                        text = item.producedArtifact,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisSecureGreen,
                        modifier = Modifier.widthIn(max = 240.dp)
                    )
                }
            }
        }
    }
}
