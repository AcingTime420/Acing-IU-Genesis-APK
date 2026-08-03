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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricSecurityManager
import com.example.security.FingerprintMapping
import com.example.security.SecurityActionType
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

/**
 * UI component for mapping specific fingerprints (Right Index, Left Ring, Left Pinky)
 * to Acing IU security actions using androidx.biometric integration.
 */
@Composable
fun FingerprintMappingView(
    biometricManager: BiometricSecurityManager,
    onTriggerExecuted: (FingerprintMapping, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mappingsList by remember { mutableStateOf(biometricManager.getMappings()) }
    var statusFeedback by remember { mutableStateOf<String?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(12.dp))
            .testTag("fingerprint_mapping_view")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Biometric Fingerprint Action Mapping",
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
                        text = "ROUTINES+ BRIDGE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Assign unique Acing IU security triggers to individual registered finger profiles",
                style = MaterialTheme.typography.bodySmall,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            mappingsList.forEach { mapping ->
                FingerprintItemCard(
                    mapping = mapping,
                    onActionSelected = { newAction ->
                        biometricManager.updateMapping(mapping.fingerName, newAction)
                        mappingsList = biometricManager.getMappings()
                    },
                    onTestTrigger = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            biometricManager.authenticateFingerprintAction(
                                activity = activity,
                                targetFinger = mapping.fingerName,
                                onSuccess = { authenticatedMapping ->
                                    val feedback = "✓ Verified ${authenticatedMapping.fingerName}: Triggered ${authenticatedMapping.assignedAction.label}"
                                    statusFeedback = feedback
                                    onTriggerExecuted(authenticatedMapping, feedback)
                                },
                                onError = { err ->
                                    val feedback = "✗ Biometric Authentication Note: $err"
                                    statusFeedback = feedback
                                    onTriggerExecuted(mapping, feedback)
                                }
                            )
                        } else {
                            // Non-FragmentActivity context fallback (for standard Jetpack Compose Activity)
                            val feedback = "✓ Simulated Trigger: ${mapping.fingerName} -> ${mapping.assignedAction.label}"
                            statusFeedback = feedback
                            onTriggerExecuted(mapping, feedback)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            statusFeedback?.let { feedback ->
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AegisDarkBg)
                        .border(1.dp, AegisPrimaryCyan, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = feedback,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AegisPrimaryCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun FingerprintItemCard(
    mapping: FingerprintMapping,
    onActionSelected: (SecurityActionType) -> Unit,
    onTestTrigger: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AegisDarkBg)
            .border(1.dp, AegisBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = mapping.fingerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AegisTextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(mapping.assignedAction.badgeColorHex))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mapping.assignedAction.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = mapping.description,
                fontSize = 9.sp,
                color = AegisTextSecondary,
                lineHeight = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box {
            OutlinedButton(
                onClick = { dropdownExpanded = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AegisPrimaryCyan),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Change Action", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.background(AegisSurface)
            ) {
                SecurityActionType.values().forEach { action ->
                    DropdownMenuItem(
                        text = { Text(action.label, fontSize = 11.sp, color = AegisTextPrimary) },
                        onClick = {
                            onActionSelected(action)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Button(
            onClick = onTestTrigger,
            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
            modifier = Modifier.height(32.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Test", modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Test", fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
