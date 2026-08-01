package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiConfigStatus
import com.example.ai.GeminiConfigValidationResult
import com.example.ui.theme.*

@Composable
fun AegisAiStatusView(
    validationResult: GeminiConfigValidationResult,
    serviceErrorMessage: String? = null,
    onRefreshStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGuide by remember { mutableStateOf(false) }

    val isErrorState = serviceErrorMessage != null || validationResult.status == GeminiConfigStatus.SERVICE_UNAVAILABLE
    val isMissingConfig = validationResult.status == GeminiConfigStatus.MISSING_OR_PLACEHOLDER || validationResult.status == GeminiConfigStatus.INVALID_FORMAT

    val cardBg = when {
        isErrorState -> Color(0xFF3B1E1E)
        isMissingConfig -> Color(0xFF2E2211)
        else -> AegisSurface
    }

    val borderColor = when {
        isErrorState -> AegisDangerRed
        isMissingConfig -> AegisWarningGold
        else -> AegisSecureGreen
    }

    val statusTitle = when {
        isErrorState -> "AI SERVICE UNAVAILABLE"
        isMissingConfig -> "CONFIGURATION REQUIRED (FAIL-SAFE MODE)"
        else -> "LIVE GEMINI AI CONNECTED"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("aegis_ai_status_view")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isErrorState -> AegisDangerRed
                                    isMissingConfig -> AegisWarningGold
                                    else -> AegisSecureGreen
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = statusTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = when {
                                isErrorState -> Color(0xFFFF8A80)
                                isMissingConfig -> Color(0xFFFFD54F)
                                else -> AegisPrimaryCyan
                            }
                        )
                        Text(
                            text = serviceErrorMessage ?: validationResult.userMessage,
                            fontSize = 10.sp,
                            color = if (isErrorState || isMissingConfig) Color(0xFFE0E0E0) else AegisTextSecondary,
                            maxLines = 2
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isMissingConfig || isErrorState) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AegisTerminalBg)
                                .border(1.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                .clickable { showGuide = !showGuide }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("troubleshoot_toggle_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Troubleshoot",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showGuide) "Hide" else "Troubleshoot",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onRefreshStatus,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("refresh_ai_status_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Re-check AI Status",
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showGuide || isErrorState) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisTerminalBg)
                        .border(1.dp, AegisTerminalBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "GEMINI_API_KEY CONFIGURATION & DIAGNOSTICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTerminalPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Masked Key: ${validationResult.maskedKey}\n" +
                               "• Mode: ${if (validationResult.isFailSafeMode) "Fail-Safe (Local Synthetic Security Engine)" else "Live AI API"}\n\n" +
                               "Steps to resolve:\n" +
                               "1. Obtain a valid key from Google AI Studio: https://aistudio.google.com/app/apikey\n" +
                               "2. Open the 'Secrets' panel in the AI Studio left sidebar.\n" +
                               "3. Add key: GEMINI_API_KEY with your key value.\n" +
                               "4. Tap the Refresh button above to verify key injection into BuildConfig.",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTerminalTextPrimary
                    )
                }
            }
        }
    }
}
