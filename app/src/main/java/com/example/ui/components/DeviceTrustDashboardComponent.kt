package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.DeviceTelemetryInput
import com.example.trust.DeviceTrustReport
import com.example.trust.SignalContribution
import com.example.trust.TrustTier
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@Composable
fun DeviceTrustDashboardComponent(
    report: DeviceTrustReport,
    currentTelemetryInput: DeviceTelemetryInput,
    onTelemetryChanged: (DeviceTelemetryInput) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_trust_dashboard_component")
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
                            .background(Color(report.tier.colorHex).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(report.tier.colorHex),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DEVICE TRUST MATRIX",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "Correlation ID: ${report.correlationId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextMuted
                        )
                    }
                }

                // Validation Status Indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (report.isValidated) AegisSecureGreen.copy(alpha = 0.15f)
                            else AegisDangerRed.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (report.isValidated) AegisSecureGreen else AegisDangerRed,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (report.isValidated) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (report.isValidated) AegisSecureGreen else AegisDangerRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (report.isValidated) "TELEMETRY VALIDATED" else "VALIDATION REJECTED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (report.isValidated) AegisSecureGreen else AegisDangerRed
                        )
                    }
                }
            }

            // Validation Error Callout if invalid
            if (!report.isValidated && report.validationErrors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisDangerRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisDangerRed),
                    modifier = Modifier.fillMaxWidth().testTag("telemetry_validation_error_box")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = AegisDangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (report.isTamperDetected) "TELEMETRY TAMPERING DETECTED" else "TELEMETRY INPUT VALIDATION ERRORS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisDangerRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        report.validationErrors.forEach { err ->
                            Text(
                                text = "• $err",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextPrimary
                            )
                        }
                    }
                }
            }

            // Recharts-Style Circular Gauge & Status Tier Breakdown Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Score Gauge Component
                TrustGaugeMeter(
                    score = report.score,
                    tierColor = Color(report.tier.colorHex),
                    isAuthorized = report.isAuthorized,
                    modifier = Modifier.size(120.dp)
                )

                // Status Tier Details Panel
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STATUS TIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(report.tier.colorHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = report.tier.label.uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(report.tier.colorHex)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.tier.description,
                        fontSize = 11.sp,
                        color = AegisTextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (report.isAuthorized) AegisSecureGreen.copy(alpha = 0.15f) else AegisWarningGold.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (report.isAuthorized) "ACCESS AUTHORIZED (SCORE >= 80)" else "ACCESS RESTRICTED (THRESHOLD: 80)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (report.isAuthorized) AegisSecureGreen else AegisWarningGold
                        )
                    }
                }
            }

            // Individual Trust Signals Breakdown
            Text(
                text = "INDIVIDUAL TRUST SIGNALS BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AegisTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                report.signals.forEach { signal ->
                    SignalProgressBarItem(signal = signal)
                }
            }

            // Interactive Telemetry Simulator Panel
            TelemetryInputSimulatorPanel(
                currentInput = currentTelemetryInput,
                onTelemetryChanged = onTelemetryChanged
            )
        }
    }
}

@Composable
fun TrustGaugeMeter(
    score: Int,
    tierColor: Color,
    isAuthorized: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label = "score_anim"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = size.minDimension - strokeWidth

            // Track Background Arc
            drawArc(
                color = AegisSurfaceVariant,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress Arc
            val sweepAngle = (animatedScore / 100f) * 270f
            drawArc(
                color = tierColor,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 80 Point Access Threshold Marker Arc Dot
            val thresholdAngle = 135f + (80f / 100f) * 270f
            val rad = Math.toRadians(thresholdAngle.toDouble())
            val radius = arcSize / 2f
            val cx = size.width / 2f + radius * Math.cos(rad).toFloat()
            val cy = size.height / 2f + radius * Math.sin(rad).toFloat()

            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(cx, cy)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${score}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = AegisTextPrimary
            )
            Text(
                text = "/ 100",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = AegisTextMuted
            )
        }
    }
}

@Composable
fun SignalProgressBarItem(signal: SignalContribution) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AegisSurfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = signal.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextPrimary
                )
                Text(
                    text = if (signal.isPenalty) {
                        if (signal.isActive) "PENALTY (-100)" else "CLEAN (+0)"
                    } else {
                        "+${signal.pointsAwarded} / ${signal.maxPoints} pts"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (signal.isPenalty && signal.isActive) AegisDangerRed else if (signal.isActive) AegisSecureGreen else AegisTextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(AegisBorder)
            ) {
                val progressFraction = if (signal.maxPoints > 0) signal.pointsAwarded.toFloat() / signal.maxPoints.toFloat() else if (signal.isActive) 1f else 0f
                if (progressFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(if (signal.isPenalty) AegisDangerRed else AegisPrimaryCyan)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = signal.description,
                fontSize = 9.sp,
                color = AegisTextMuted
            )
        }
    }
}

@Composable
fun TelemetryInputSimulatorPanel(
    currentInput: DeviceTelemetryInput,
    onTelemetryChanged: (DeviceTelemetryInput) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TELEMETRY INPUT & VALIDATION TESTER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryCyan
                    )
                }

                Button(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AegisSurface, contentColor = AegisPrimaryCyan),
                    modifier = Modifier.testTag("toggle_telemetry_tester_btn")
                ) {
                    Text(if (expanded) "Hide Tester" else "Simulate Telemetry", fontSize = 10.sp)
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Hardware ID text field
                OutlinedTextField(
                    value = currentInput.hardwareId,
                    onValueChange = { newId ->
                        onTelemetryChanged(currentInput.copy(hardwareId = newId))
                    },
                    label = { Text("Hardware Node ID", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisPrimaryCyan,
                        unfocusedBorderColor = AegisBorder,
                        focusedTextColor = AegisTextPrimary,
                        unfocusedTextColor = AegisTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("hardware_id_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Signal Toggle Switches
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SimulatorToggleRow(
                        label = "SELinux Enforcing Mode (+40 pts)",
                        checked = currentInput.selinuxEnforcing,
                        onCheckedChange = { onTelemetryChanged(currentInput.copy(selinuxEnforcing = it)) }
                    )
                    SimulatorToggleRow(
                        label = "Bootloader Locked (+30 pts)",
                        checked = currentInput.bootloaderLocked,
                        onCheckedChange = { onTelemetryChanged(currentInput.copy(bootloaderLocked = it)) }
                    )
                    SimulatorToggleRow(
                        label = "Partitions DM-Verity Unmodified (+20 pts)",
                        checked = currentInput.partitionsUnmodified,
                        onCheckedChange = { onTelemetryChanged(currentInput.copy(partitionsUnmodified = it)) }
                    )
                    SimulatorToggleRow(
                        label = "Knox Hardware Fuse Intact (+10 pts)",
                        checked = currentInput.knoxFuseIntact,
                        onCheckedChange = { onTelemetryChanged(currentInput.copy(knoxFuseIntact = it)) }
                    )
                    SimulatorToggleRow(
                        label = "Root Privilege Detected (Forces Score to 0)",
                        checked = currentInput.isRooted,
                        isWarning = true,
                        onCheckedChange = { onTelemetryChanged(currentInput.copy(isRooted = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Scenarios
                Text(
                    text = "PRESET TELEMETRY SCENARIOS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AegisTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            onTelemetryChanged(
                                DeviceTelemetryInput(
                                    hardwareId = "SM-S938U-VERIZON-01",
                                    selinuxEnforcing = true,
                                    bootloaderLocked = true,
                                    partitionsUnmodified = true,
                                    knoxFuseIntact = true,
                                    isRooted = false
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisSecureGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("preset_trusted_btn")
                    ) {
                        Text("Clean Node", fontSize = 9.sp)
                    }

                    Button(
                        onClick = {
                            onTelemetryChanged(
                                DeviceTelemetryInput(
                                    hardwareId = "ROOTED-NODE-DEV-09",
                                    selinuxEnforcing = false,
                                    bootloaderLocked = false,
                                    partitionsUnmodified = false,
                                    knoxFuseIntact = false,
                                    isRooted = true
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisDangerRed, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("preset_rooted_btn")
                    ) {
                        Text("Rooted Node", fontSize = 9.sp)
                    }

                    Button(
                        onClick = {
                            onTelemetryChanged(
                                DeviceTelemetryInput(
                                    hardwareId = "SPOOF_BYPASS_<script>alert('xss')</script>",
                                    selinuxEnforcing = true,
                                    bootloaderLocked = true,
                                    partitionsUnmodified = true,
                                    knoxFuseIntact = true,
                                    isRooted = true
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AegisWarningGold, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("preset_tampered_btn")
                    ) {
                        Text("Tampered Input", fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatorToggleRow(
    label: String,
    checked: Boolean,
    isWarning: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isWarning && checked) AegisDangerRed else AegisTextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (isWarning) AegisDangerRed else AegisPrimaryCyan
            )
        )
    }
}
