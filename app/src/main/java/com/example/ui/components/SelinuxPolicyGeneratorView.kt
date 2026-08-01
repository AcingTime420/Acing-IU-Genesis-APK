package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.GeneratedPolicyResult
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun SelinuxPolicyGeneratorView(
    policyResult: GeneratedPolicyResult?,
    onGeneratePolicy: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var inputLogcat by remember {
        mutableStateOf("avc: denied { read open } for pid=1420 comm=\"app_process\" scontext=u:r:untrusted_app:s0 tcontext=u:r:system_server:s0 tclass=binder")
    }

    val presetDenials = listOf(
        "avc: denied { read open } for pid=1420 scontext=u:r:untrusted_app:s0 tcontext=u:r:system_server:s0 tclass=binder",
        "avc: denied { ioctl } for pid=3040 scontext=u:r:system_app:s0 tcontext=u:r:sysfs:s0 tclass=file",
        "avc: denied { execute } for pid=881 scontext=u:r:shell:s0 tcontext=u:r:vendor_file:s0 tclass=file"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("selinux_policy_generator_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Policy,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KNOX & SELINUX POLICY GENERATOR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AegisPrimaryCyan
                    )
                }
            }

            Text(
                text = "Generate raw .te policy rules & Knox container MDM directives from Logcat AVC denial entries.",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Presets
            Text(
                text = "PRESET AVC DENIAL LOGS:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            presetDenials.forEach { preset ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(AegisDarkBg, RoundedCornerShape(6.dp))
                        .border(1.dp, AegisBorder, RoundedCornerShape(6.dp))
                        .clickable { inputLogcat = preset }
                        .padding(8.dp)
                ) {
                    Text(
                        text = preset,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AegisTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputLogcat,
                onValueChange = { inputLogcat = it },
                label = { Text("Logcat AVC Denial Input", fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selinux_input_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (inputLogcat.isNotBlank()) {
                            onGeneratePolicy(inputLogcat)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("generate_policy_button")
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate .te Policy Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (policyResult != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AegisDarkBg, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (policyResult.isNeverallowViolation) AegisDangerRed else AegisSecureGreen,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (policyResult.isNeverallowViolation) Icons.Default.Warning else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (policyResult.isNeverallowViolation) AegisDangerRed else AegisSecureGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (policyResult.isNeverallowViolation) "NEVERALLOW VIOLATION DETECTED" else "VALID .TE POLICY GENERATED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (policyResult.isNeverallowViolation) AegisDangerRed else AegisSecureGreen
                                )
                            }

                            Row {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(policyResult.tePolicyRules))
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("copy_policy_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Rules", fontSize = 10.sp)
                                }
                            }
                        }

                        if (policyResult.violationWarning != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = policyResult.violationWarning,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AegisDangerRed
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = policyResult.tePolicyRules,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "KNOX MDM POLICY DIRECTIVE JSON:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisPrimaryCyan
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = policyResult.knoxMdmPolicyJson,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                    }
                }
            }
        }
    }
}
