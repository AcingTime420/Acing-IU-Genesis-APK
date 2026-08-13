package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * Granular Linear Progress Bar component that visually tracks the progress of long-running
 * firmware analysis tasks, partition integrity sweeps, and cryptographic verification routines.
 */
@Composable
fun FirmwareAnalysisProgressBar(
    filesProcessed: Int,
    totalFiles: Int,
    currentFileName: String = "",
    currentPhase: String = "Analyzing partition blocks...",
    isComplete: Boolean = false,
    throughputMbPerSec: Float = 42.8f,
    modifier: Modifier = Modifier
) {
    val rawProgress = if (totalFiles > 0) {
        (filesProcessed.toFloat() / totalFiles.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isComplete) 1f else rawProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "firmware_progress_anim"
    )

    val percentage = (animatedProgress * 100).toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("firmware_analysis_progress_bar")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isComplete) AegisSecureGreen.copy(alpha = 0.2f) else AegisPrimaryCyan.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.Memory,
                            contentDescription = null,
                            tint = if (isComplete) AegisSecureGreen else AegisPrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isComplete) "ANALYSIS COMPLETE" else "FIRMWARE SCAN IN PROGRESS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (isComplete) AegisSecureGreen else AegisPrimaryCyan
                    )
                }

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (isComplete) AegisSecureGreen else AegisPrimaryCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Linear Progress Indicator
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .testTag("firmware_linear_progress_indicator"),
                color = if (isComplete) AegisSecureGreen else AegisPrimaryCyan,
                trackColor = Color(0xFF1E293B),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Granular Feedback Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Files Processed: $filesProcessed / $totalFiles",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AegisTextPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Throughput",
                        tint = AegisTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${throughputMbPerSec} MB/s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisTextSecondary
                    )
                }
            }

            if (currentFileName.isNotBlank() || currentPhase.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisDarkBg)
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        if (currentFileName.isNotBlank()) {
                            Text(
                                text = "Active Target: $currentFileName",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = AegisWarningGold
                            )
                        }
                        if (currentPhase.isNotBlank()) {
                            Text(
                                text = "Status: $currentPhase",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = AegisTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
