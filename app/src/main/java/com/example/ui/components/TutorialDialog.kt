package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
 * Data model representing each interactive onboarding step in the tutorial.
 */
data class TutorialStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val targetTab: AppTab,
    val highlights: List<String>,
    val capabilitiesSummary: String,
    val practicalTip: String
)

/**
 * Interactive Platform Tutorial & Walkthrough Modal:
 * Guides users step-by-step through every core subsystem of Acing IU Genesis IRP,
 * explaining each feature's purpose, underlying architecture, and real-time security actions.
 */
@Composable
fun TutorialDialog(
    viewModel: AcingViewModel,
    onDismiss: () -> Unit,
    onNavigateToTab: (AppTab) -> Unit
) {
    val tutorialSteps = remember {
        listOf(
            TutorialStep(
                stepNumber = 1,
                title = "Welcome to Acing IU Genesis IRP",
                subtitle = "Zero-Trust Incident Response & Firmware Hardening Platform",
                icon = Icons.Default.Shield,
                iconTint = AegisPrimaryCyan,
                targetTab = AppTab.DASHBOARD,
                highlights = listOf(
                    "Real-time Device Trust Index (DTI) scoring",
                    "Hardware KeyStore attestation & SELinux posture",
                    "Continuous kernel telemetry & live incident triage"
                ),
                capabilitiesSummary = "The platform unifies hardware-backed root of trust verification, cryptographic partition integrity, and autonomous threat remediation into a single mission-critical dashboard.",
                practicalTip = "Check the top-right status pill anytime to confirm the zero-trust state or trigger emergency containment."
            ),
            TutorialStep(
                stepNumber = 2,
                title = "10-Vector Security Matrix",
                subtitle = "Comprehensive Device & Boot Baseline Attestation",
                icon = Icons.Default.Security,
                iconTint = AegisSecureGreen,
                targetTab = AppTab.SECURITY_STATUS,
                highlights = listOf(
                    "AVB 2.0 Verified Boot Chain validation",
                    "SELinux Enforcing policy with strict MLS rules",
                    "Samsung Knox 0x0 Warranty Bit & Play Integrity status",
                    "Real-time one-click remediation proposals"
                ),
                capabilitiesSummary = "Continuously scans 10 cryptographic vectors to detect bootloader unlocks, root access, malicious system tampering, and unpinned TLS connections.",
                practicalTip = "Click 'Remediate' on any flagged vector to apply instant zero-trust policy corrections."
            ),
            TutorialStep(
                stepNumber = 3,
                title = "Firmware & Partition Analysis",
                subtitle = "Cryptographic Image Verification & Entropy Engine",
                icon = Icons.Default.FolderZip,
                iconTint = AegisPrimaryCyan,
                targetTab = AppTab.FIRMWARE,
                highlights = listOf(
                    "Multi-threaded inspection of boot.img, vbmeta.img, vendor_boot.img, dtbo.img",
                    "Shannon byte entropy calculation to spot obfuscated packers and payloads",
                    "Magic byte header inspection (ANDROID!, AVB0, DTBO)",
                    "Samsung Odin .tar.md5 archive signature & PIT partition table verification"
                ),
                capabilitiesSummary = "Ensures on-device and flashed firmware binaries match certified cryptographic digests and have not been modified by unauthorized third-party bootloaders.",
                practicalTip = "Run 'Start Analysis' to observe real-time MB/s partition throughput and entropy heatmaps."
            ),
            TutorialStep(
                stepNumber = 4,
                title = "Fallback Security Policy (Air-Gapped)",
                subtitle = "Local-Only Verification via Pre-Bundled TFLite Models",
                icon = Icons.Default.Lock,
                iconTint = AegisWarningGold,
                targetTab = AppTab.FIRMWARE,
                highlights = listOf(
                    "Automatic offline detection via NetworkCapabilities",
                    "Instant transition to Restricted Mode when internet drops",
                    "Local-only TFLite neural token embedding evaluation",
                    "Independent on-device SHA-256 and entropy validation without cloud latency"
                ),
                capabilitiesSummary = "Guarantees zero-trust operational readiness even in strictly air-gapped, offline, or compromised network environments.",
                practicalTip = "Use the 'Simulate Offline Disconnect' button on the Firmware screen to test air-gapped verification."
            ),
            TutorialStep(
                stepNumber = 5,
                title = "Network Telemetry & Rogue Defense",
                subtitle = "Interface Inspector, Cert Pinning & Rogue Port Kill Switch",
                icon = Icons.Default.Wifi,
                iconTint = AegisPrimaryCyan,
                targetTab = AppTab.DEVICES,
                highlights = listOf(
                    "Active network interface inspection (WiFi, Cellular, VPN)",
                    "Live network vulnerability scanner for rogue DHCP and open ports",
                    "Cryptographic TLS Certificate Pinning toggle",
                    "Emergency Rogue ADB & USB debug bridge termination"
                ),
                capabilitiesSummary = "Protects against Man-in-the-Middle (MitM) proxies, unauthorized ADB debugging bridges, and malicious network telemetry exfiltration.",
                practicalTip = "Enable 'Aegis Cert Pinning' to restrict all outbound telemetry exclusively to pinned public keys."
            ),
            TutorialStep(
                stepNumber = 6,
                title = "Forensic Ledger & Signed CSV Export",
                subtitle = "Tamper-Evident Incident Tracking & Audit Export",
                icon = Icons.Default.BugReport,
                iconTint = AegisDangerRed,
                targetTab = AppTab.FORENSICS,
                highlights = listOf(
                    "Cryptographically signed audit event timeline",
                    "SHA-256 HMAC integrity hashes for every log entry",
                    "Severity filtering: CRITICAL, ALERT, SECURE, INFO",
                    "Direct CSV file export via Android FileProvider share sheet"
                ),
                capabilitiesSummary = "Maintains an immutable local Room database ledger for post-incident analysis, compliance audits, and legal chain of custody verification.",
                practicalTip = "Tap 'Export Signed CSV' to immediately generate and share an authenticated forensic report."
            ),
            TutorialStep(
                stepNumber = 7,
                title = "Aegis AI Security Copilot",
                subtitle = "Gemini Pro / Flash Zero-Trust Autonomous Assistant",
                icon = Icons.Default.Psychology,
                iconTint = AegisPrimaryCyan,
                targetTab = AppTab.AEGIS_AI,
                highlights = listOf(
                    "Multi-step Thinking Mode for deep architectural rationale",
                    "Context-aware automated prompt suggestions",
                    "Zero-day exploit mitigation & CVE patch generation",
                    "Direct integration with live incident logs"
                ),
                capabilitiesSummary = "Leverages cutting-edge Gemini reasoning models to analyze complex threat patterns, interpret forensic artifacts, and craft tailored hardening scripts.",
                practicalTip = "Toggle 'AI Thinking Mode' to see comprehensive chain-of-thought analysis for critical threats."
            ),
            TutorialStep(
                stepNumber = 8,
                title = "Governance, RBAC & Biometrics",
                subtitle = "Multi-Role Access Control & Hardware Biometric Gatekeeper",
                icon = Icons.Default.AdminPanelSettings,
                iconTint = AegisBadgeIndigoText,
                targetTab = AppTab.GOVERNANCE,
                highlights = listOf(
                    "Three-tier Role Based Access Control (Principal Architect, Auditor, Engineer)",
                    "5-minute background session timeout for biometric re-authentication",
                    "Multi-fingerprint biometric action mapping (FRP Reset, Emergency Lockdown)",
                    "Predictive Matrix Auto-Typing engine & Shopify License verification"
                ),
                capabilitiesSummary = "Enforces strict organizational access boundaries, biometric physical presence verification, and rapid emergency rollback protocols.",
                practicalTip = "Switch roles in the Governance tab to test role-restricted permissions and view the audit logs."
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = tutorialSteps[currentStepIndex]
    val totalSteps = tutorialSteps.size

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .widthIn(max = 680.dp)
                .testTag("tutorial_dialog"),
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                // Top Bar: Header, Progress & Close Button
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
                                .background(AegisBadgeIndigoBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Tutorial",
                                tint = AegisBadgeIndigoText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Platform Interactive Tutorial",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AegisTextPrimary
                            )
                            Text(
                                text = "Step ${step.stepNumber} of $totalSteps",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_tutorial_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AegisTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentStepIndex + 1).toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AegisPrimaryCyan,
                    trackColor = AegisDarkBg
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step Content (Animated Transition)
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState.stepNumber > initialState.stepNumber) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    label = "TutorialStepAnimation"
                ) { currentStep ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Hero Banner with Subsystem Icon & Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AegisDarkBg)
                                .border(1.dp, currentStep.iconTint.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(currentStep.iconTint.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = currentStep.icon,
                                        contentDescription = currentStep.title,
                                        tint = currentStep.iconTint,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentStep.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AegisTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentStep.subtitle,
                                        fontSize = 11.sp,
                                        color = currentStep.iconTint,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Capabilities Summary
                        Text(
                            text = "FUNCTION PURPOSE & ARCHITECTURE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentStep.capabilitiesSummary,
                            fontSize = 12.sp,
                            color = AegisTextPrimary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Highlights & Capabilities
                        Text(
                            text = "CORE CAPABILITIES:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        currentStep.highlights.forEach { highlight ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Feature",
                                    tint = AegisSecureGreen,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = highlight,
                                    fontSize = 12.sp,
                                    color = AegisTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Practical Pro-Tip Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AegisBadgeIndigoBg.copy(alpha = 0.5f))
                                .border(1.dp, AegisBadgeIndigoBg, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Tip",
                                    tint = AegisBadgeIndigoText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "OPERATIONAL TIP",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = AegisBadgeIndigoText
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentStep.practicalTip,
                                        fontSize = 11.sp,
                                        color = AegisTextPrimary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Controls: Back, Deep-Link Jump, Next / Finish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStepIndex > 0) {
                        OutlinedButton(
                            onClick = { currentStepIndex-- },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("tutorial_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous",
                                modifier = Modifier.size(16.dp),
                                tint = AegisTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous", fontSize = 12.sp, color = AegisTextSecondary)
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("tutorial_skip_button")
                        ) {
                            Text("Skip Tour", fontSize = 12.sp, color = AegisTextMuted)
                        }
                    }

                    // Direct Jump to the Subsystem
                    Button(
                        onClick = {
                            onNavigateToTab(step.targetTab)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisBadgeIndigoBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("tutorial_try_tab_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Try Tab",
                            tint = AegisBadgeIndigoText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Open ${step.targetTab.name.replace("_", " ")}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisBadgeIndigoText
                        )
                    }

                    // Next or Finish Button
                    Button(
                        onClick = {
                            if (currentStepIndex < totalSteps - 1) {
                                currentStepIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("tutorial_next_button")
                    ) {
                        Text(
                            text = if (currentStepIndex < totalSteps - 1) "Next" else "Got It!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        if (currentStepIndex < totalSteps - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
