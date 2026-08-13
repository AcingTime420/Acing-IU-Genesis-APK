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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.PredictiveKeyboardModelEngine
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

/**
 * Interactive Jetpack Compose component demonstrating Acing Matrix Predictive Auto-Keyboard Engine.
 * Features live next-word prediction, TFLite/N-gram candidate probabilities, and auto-typing simulation.
 */
@Composable
fun PredictiveKeyboardCard(
    modifier: Modifier = Modifier
) {
    val modelEngine = remember { PredictiveKeyboardModelEngine() }
    var inputText by remember { mutableStateOf("how are") }
    var autoAcceptEnabled by remember { mutableStateOf(true) }
    var lastAppendedWord by remember { mutableStateOf("") }

    val prediction = modelEngine.predictNextWord(inputText)

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(12.dp))
            .testTag("predictive_keyboard_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Acing Matrix Predictive Auto-Keyboard Engine",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AegisTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AegisBadgeIndigoBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "TFLite / N-GRAM",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "On-device AI model reads context and auto-predicts/types the next word in sequence",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-commit Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisDarkBg)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AegisSecureGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Auto-Append High-Confidence Predictions",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "Threshold: >30% model probability score",
                            fontSize = 9.sp,
                            color = AegisTextMuted
                        )
                    }
                }

                Switch(
                    checked = autoAcceptEnabled,
                    onCheckedChange = { autoAcceptEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AegisSecureGreen,
                        checkedTrackColor = AegisSecureGreen.copy(alpha = 0.3f),
                        uncheckedThumbColor = AegisTextMuted,
                        uncheckedTrackColor = AegisDarkBg
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Text Box
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Active Editor Input Text", fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary,
                    focusedContainerColor = AegisDarkBg,
                    unfocusedContainerColor = AegisDarkBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prediction Banner
            if (prediction.predictedWord.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisTerminalBg)
                        .border(1.dp, AegisPrimaryCyan, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AegisSecureGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Predicted Next Word: \"${prediction.predictedWord}\"",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AegisSecureGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Confidence: ${(prediction.confidence * 100).toInt()}% | Context: ${prediction.contextTokens.joinToString(" ")}",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                inputText = if (inputText.endsWith(" ")) {
                                    "$inputText${prediction.predictedWord} "
                                } else {
                                    "$inputText ${prediction.predictedWord} "
                                }
                                lastAppendedWord = prediction.predictedWord
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Append", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Top-K Candidate Probabilities
                Text(
                    text = "Top Model Candidates Probability Distribution:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    prediction.topCandidates.take(3).forEach { (candWord, prob) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AegisDarkBg)
                                .border(1.dp, AegisBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$candWord (${(prob * 100).toInt()}%)",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
