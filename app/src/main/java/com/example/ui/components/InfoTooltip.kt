package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary

/**
 * InfoTooltip component that displays a short summary of a function's purpose
 * when tapped or long-pressed, providing inline help for security-specific controls.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoTooltip(
    summary: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    badgeText: String = "HELP"
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .testTag("info_tooltip_trigger")
                .combinedClickable(
                    onClick = { isExpanded = !isExpanded },
                    onLongClick = { isExpanded = true }
                )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Help & Security Information",
                tint = AegisPrimaryCyan.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(AegisDarkBg)
                .padding(8.dp)
        ) {
            Surface(
                color = AegisSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                modifier = Modifier.padding(4.dp)
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    androidx.compose.foundation.layout.Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title ?: "Security Insight",
                                color = AegisPrimaryCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AegisPrimaryCyan.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = AegisPrimaryCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = summary,
                            color = AegisTextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.width(220.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Tap outside to dismiss",
                            color = AegisTextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
