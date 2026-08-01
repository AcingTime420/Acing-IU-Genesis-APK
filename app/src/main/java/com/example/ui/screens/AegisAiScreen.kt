package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.ai.ChatMessage
import com.example.ui.AcingViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryBlue
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisSurfaceVariant
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun AegisAiScreen(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chatMessages by viewModel.chatMessages.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    val useThinkingMode by viewModel.useThinkingMode.collectAsState()
    val remediationProposals by viewModel.remediationProposals.collectAsState()
    val isDiagnosisActive by viewModel.isSelfHealingActive.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showInspectorPanel by remember { mutableStateOf(chatMessages.isEmpty()) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var isListeningVoice by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Speech-to-text recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListeningVoice = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
            }
        }
    }

    fun startVoiceInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate prompt to Aegis AI...")
            }
            isListeningVoice = true
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            isListeningVoice = false
            // Fallback for emulators without speech activity
            Toast.makeText(context, "Voice input simulation: Speech recognizer initialized", Toast.LENGTH_SHORT).show()
            inputText = "Audit system SELinux and TEE keymaster attestation status"
        }
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Main Scrollable Thread containing Header Inspector + Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(
                        title = "Aegis AI Security Co-Pilot",
                        subtitle = "Gemini powered Android threat modeling & reasoning assistant",
                        icon = Icons.Default.SmartToy
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Collapsible Header Bar Toggle
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AegisSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showInspectorPanel = !showInspectorPanel }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = AegisPrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showInspectorPanel) "HIDE SYSTEM INSPECTOR" else "EXPAND SYSTEM INSPECTOR & SELF-HEALING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisPrimaryCyan
                                )
                            }
                            Icon(
                                imageVector = if (showInspectorPanel) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Inspector",
                                tint = AegisTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Collapsible Inspector Cards
                    AnimatedVisibility(
                        visible = showInspectorPanel,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Reusable AI Status & Diagnostic View
                            val configValidation = com.example.ai.GeminiConfigValidator.validateConfig()
                            com.example.ui.components.AegisAiStatusView(
                                validationResult = configValidation,
                                onRefreshStatus = { viewModel.refreshApiKeyStatus() }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Autonomous Self-Healing & Human-Authorized Remediation
                            com.example.ui.components.AegisSelfHealingComponent(
                                proposals = remediationProposals,
                                isDiagnosisActive = isDiagnosisActive,
                                onRunDiagnosis = { viewModel.runAegisSelfDiagnosis() },
                                onApprovePatch = { viewModel.approveAndExecuteRemediation(it) },
                                onDismissPatch = { viewModel.dismissRemediation(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Prompt Chips Row
                    val presetPrompts = listOf(
                        "Audit AVB 2.0 Bootloader Locks",
                        "Explain SELinux Policy Violations",
                        "Evaluate TEE Keymaster Attestation",
                        "Generate Zero-Trust Policy"
                    )

                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(presetPrompts.size) { index ->
                            val prompt = presetPrompts[index]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AegisSurface)
                                    .border(1.dp, AegisBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.sendAiPrompt(prompt) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisPrimaryCyan,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thinking Mode Toggle Bar
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AegisSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (useThinkingMode) AegisPrimaryCyan else AegisTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "HIGH THINKING MODE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (useThinkingMode) AegisPrimaryCyan else AegisTextPrimary
                                    )
                                    Text(
                                        text = if (useThinkingMode) "Deep reasoning (gemini-3.1-pro)" else "Fast mode (gemini-3.5-flash)",
                                        fontSize = 9.sp,
                                        color = AegisTextSecondary,
                                        lineHeight = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = useThinkingMode,
                                onCheckedChange = { viewModel.toggleThinkingMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AegisDarkBg,
                                    checkedTrackColor = AegisPrimaryCyan,
                                    uncheckedThumbColor = AegisTextSecondary,
                                    uncheckedTrackColor = AegisSurfaceVariant
                                ),
                                modifier = Modifier.testTag("thinking_mode_switch")
                            )
                        }
                    }
                }
            }

            // Chat Messages
            items(chatMessages) { msg ->
                ChatMessageBubble(msg = msg)
            }

            if (aiLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AegisPrimaryCyan, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (useThinkingMode) "Aegis AI is reasoning deeply..." else "Aegis AI is preparing security analysis...",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisPrimaryCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Active Voice Listening Indicator Banner
        if (isListeningVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisPrimaryCyan.copy(alpha = 0.2f))
                    .border(1.dp, AegisPrimaryCyan, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Listening",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LISTENING FOR VOICE PROMPT...",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisPrimaryCyan
                    )
                }
                Text(
                    text = "TAP MIC TO STOP",
                    fontSize = 9.sp,
                    color = AegisTextSecondary,
                    modifier = Modifier.clickable { isListeningVoice = false }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Row with (+) Attach Function, Voice Input (Mic), Text Field, and Send Arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // (+) Function attachment menu trigger
            Box {
                IconButton(
                    onClick = { showAttachMenu = !showAttachMenu },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AegisSurface)
                        .border(1.dp, AegisBorder, CircleShape)
                        .testTag("attach_function_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Input Functions",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(
                    expanded = showAttachMenu,
                    onDismissRequest = { showAttachMenu = false },
                    modifier = Modifier.background(AegisDarkBg)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attach Logcat Snippet Context", fontSize = 11.sp, color = AegisTextPrimary)
                            }
                        },
                        onClick = {
                            showAttachMenu = false
                            inputText = "Audit Logcat Snippet: [SELinux DENIED comm=\"app_process\" scontext=u:r:untrusted_app:s0 tcontext=u:r:system_server:s0 tclass=binder]"
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attach Telemetry & Keystore Context", fontSize = 11.sp, color = AegisTextPrimary)
                            }
                        },
                        onClick = {
                            showAttachMenu = false
                            inputText = "Analyze Device Telemetry: SELinux Enforcing, Keystore StrongBox RSA-4096 Attestation Verified, AVB 2.0 Boot Chain Intact."
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = AegisPrimaryCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attach Firmware Partition Digest", fontSize = 11.sp, color = AegisTextPrimary)
                            }
                        },
                        onClick = {
                            showAttachMenu = false
                            inputText = "Audit Firmware Image Hash: Partition 'boot' SHA-256 e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = AegisSecureGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attach Zero-Trust Policy Query", fontSize = 11.sp, color = AegisTextPrimary)
                            }
                        },
                        onClick = {
                            showAttachMenu = false
                            inputText = "Generate Zero-Trust RBAC security policy directives for Acing IU: Genesis kernel modules."
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BugReport, contentDescription = null, tint = AegisPrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Trigger Aegis Self-Healing Diagnosis", fontSize = 11.sp, color = AegisPrimaryBlue)
                            }
                        },
                        onClick = {
                            showAttachMenu = false
                            viewModel.runAegisSelfDiagnosis()
                            inputText = "Review self-healing diagnosis proposals and explain remediation path."
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Voice Input Microphone Button
            IconButton(
                onClick = { startVoiceInput() },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isListeningVoice) AegisPrimaryCyan else AegisSurface)
                    .border(1.dp, if (isListeningVoice) AegisPrimaryCyan else AegisBorder, CircleShape)
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = if (isListeningVoice) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListeningVoice) AegisDarkBg else AegisPrimaryCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Text Input Box
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Aegis AI or input security query...", fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send Arrow Button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendAiPrompt(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !aiLoading,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) AegisPrimaryCyan else AegisSurfaceVariant)
                    .testTag("send_prompt_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Prompt",
                    tint = if (inputText.isNotBlank()) Color.White else AegisTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessage) {
    val isUser = msg.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AegisPrimaryCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = if (isUser) "ARCHITECT" else if (msg.isThinkingModel) "AEGIS AI (THINKING MODE)" else "AEGIS AI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isUser) AegisTextSecondary else AegisPrimaryCyan
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(if (isUser) AegisPrimaryBlue.copy(alpha = 0.3f) else AegisSurface)
                .border(
                    1.dp,
                    if (isUser) AegisPrimaryBlue else AegisBorder,
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyMedium,
                color = AegisTextPrimary
            )
        }
    }
}
