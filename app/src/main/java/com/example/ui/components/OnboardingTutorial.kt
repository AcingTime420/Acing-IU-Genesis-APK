package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

data class OnboardingTutorialStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String,
    val description: String,
    val technicalHighlights: List<String>,
    val targetArea: String
)

val defaultTutorialSteps = listOf(
    OnboardingTutorialStep(
        stepNumber = 1,
        title = "Security Dashboard & Posture",
        subtitle = "Central Command & Telemetry Aggregation",
        icon = Icons.Default.Dashboard,
        badge = "OVERVIEW",
        description = "Provides real-time system integrity indexing, Knox warranty bit telemetry, multi-layer risk scoring, and hardware security posture audits.",
        technicalHighlights = listOf(
            "Live Hardware Trust Scoring (0-100%)",
            "Root & SELinux Heuristic Detection",
            "Background Security Snapshot Workers"
        ),
        targetArea = "Dashboard Tab"
    ),
    OnboardingTutorialStep(
        stepNumber = 2,
        title = "Firmware Analysis & Biometric Gatekeeper",
        subtitle = "Zero-Trust Cryptographic Partition Auditing",
        icon = Icons.Default.FolderZip,
        badge = "FIRMWARE",
        description = "Inspect bootloader signatures, dm-verity hash trees, Odin binary packages, and build.prop configurations protected by strict Biometric Gatekeepers.",
        technicalHighlights = listOf(
            "Biometric Hardware Enclave Auth",
            "Odin Binary Signature Verification",
            "Automated SHA-256 Partition Verifier"
        ),
        targetArea = "Firmware Tab"
    ),
    OnboardingTutorialStep(
        stepNumber = 3,
        title = "Aegis AI Threat Intelligence",
        subtitle = "Automated Security Remediation Powered by Gemini",
        icon = Icons.Default.SmartToy,
        badge = "AI ENGINE",
        description = "Analyzes high-frequency audit logs, proposes automated remediation plans, and executes policy rollouts securely with client-side validation.",
        technicalHighlights = listOf(
            "Gemini 2.5 Multi-Turn Reasoning",
            "Structured JSON Remediation Proposals",
            "Zero-Trust Policy Validation Pipeline"
        ),
        targetArea = "Aegis AI Tab"
    ),
    OnboardingTutorialStep(
        stepNumber = 4,
        title = "Full Device Control & Settings",
        subtitle = "Hardware Lockdowns & Autonomous Policies",
        icon = Icons.Default.Security,
        badge = "DEVICE CONTROLS",
        description = "Take full operational control of the device with USB lockdown, 2G cellular isolation, auto-reboot timers, and the full Capabilities Encyclopedia.",
        technicalHighlights = listOf(
            "USB-C BadUSB Data Blocking",
            "IMSI Catcher Defense Protocol",
            "Complete Capabilities Guide Explorer"
        ),
        targetArea = "Settings Tab"
    )
)

/**
 * OnboardingTutorial Composable that displays interactive tooltip overlays
 * explaining core features triggered on first app launch.
 */
@Composable
fun OnboardingTutorial(
    isFirstTime: Boolean,
    onComplete: () -> Unit,
    steps: List<OnboardingTutorialStep> = defaultTutorialSteps
) {
    if (!isFirstTime) return

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps[currentStepIndex]

    Dialog(
        onDismissRequest = { /* Require explicit user action */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .testTag("onboarding_tutorial_dialog"),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AegisPrimaryCyan)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
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
                                    imageVector = currentStep.icon,
                                    contentDescription = null,
                                    tint = AegisPrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PLATFORM ONBOARDING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisPrimaryCyan,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Step ${currentStep.stepNumber} of ${steps.size}",
                                    fontSize = 12.sp,
                                    color = AegisTextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onComplete,
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Tutorial",
                                tint = AegisTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step Content with Animation
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TutorialStepContent"
                    ) { step ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AegisSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = step.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisWarningGold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = step.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AegisTextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = step.subtitle,
                                fontSize = 12.sp,
                                color = AegisPrimaryCyan,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                            )

                            Text(
                                text = step.description,
                                fontSize = 13.sp,
                                color = AegisTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Technical highlights card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = AegisDarkBg),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "CORE CAPABILITIES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AegisTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    step.technicalHighlights.forEach { highlight ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = AegisSecureGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = highlight,
                                                fontSize = 11.sp,
                                                color = AegisTextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Step Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        steps.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (index == currentStepIndex) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentStepIndex) AegisPrimaryCyan else AegisBorder
                                    )
                                    .clickable { currentStepIndex = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStepIndex > 0) {
                            OutlinedButton(
                                onClick = { currentStepIndex-- },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("onboarding_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Previous Step",
                                    tint = AegisTextPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back", color = AegisTextPrimary, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "Genesis IRP v2.5",
                                fontSize = 11.sp,
                                color = AegisTextMuted,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (currentStepIndex < steps.size - 1) {
                                    currentStepIndex++
                                } else {
                                    onComplete()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AegisPrimaryCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("onboarding_next_button")
                        ) {
                            Text(
                                text = if (currentStepIndex < steps.size - 1) "Next Feature" else "Get Started",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (currentStepIndex < steps.size - 1) Icons.Default.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
