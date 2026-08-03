package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTerminalBg
import com.example.ui.theme.AegisTerminalGreen
import com.example.ui.theme.AegisTerminalTextPrimary
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

/**
 * Native Jetpack Compose Chart component visualizing real-time system metrics,
 * Acing Matrix consensus node health, and security event rate over time.
 */
@Composable
fun SystemHealthChartDashboard(
    nodeConsensusPercentage: Int = 100,
    activeNodesCount: Int = 3,
    securityEventCount: Int = 14,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AegisBorder, RoundedCornerShape(12.dp))
            .testTag("system_health_chart_dashboard")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AegisSecureGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "System Health & Matrix Consensus",
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
                        text = "REALTIME TELEMETRY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Summary Grid
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricTile(
                    title = "Node Consensus",
                    value = "$nodeConsensusPercentage%",
                    subtitle = "$activeNodesCount/3 Nodes Active",
                    color = AegisSecureGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Security Events",
                    value = "$securityEventCount",
                    subtitle = "Last 24 Hours",
                    color = AegisPrimaryCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "PST Partition",
                    value = "VERIFIED",
                    subtitle = "1MB Block Locked",
                    color = Color(0xFF673AB7),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Event Trend Graph (Canvas Chart)
            Text(
                text = "Consensus & Event Throughput Trend (24h Window)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AegisTerminalBg)
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw grid lines
                    val gridColor = Color(0xFF333333)
                    for (i in 1..3) {
                        val y = height * (i / 4f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Sample telemetry data points for event rate curve
                    val points = listOf(
                        Offset(0f, height * 0.7f),
                        Offset(width * 0.15f, height * 0.5f),
                        Offset(width * 0.3f, height * 0.65f),
                        Offset(width * 0.45f, height * 0.3f),
                        Offset(width * 0.6f, height * 0.45f),
                        Offset(width * 0.75f, height * 0.2f),
                        Offset(width * 0.9f, height * 0.25f),
                        Offset(width, height * 0.15f)
                    )

                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val p1 = points[i - 1]
                            val p2 = points[i]
                            val controlX = (p1.x + p2.x) / 2f
                            cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
                        }
                    }

                    // Draw line
                    drawPath(
                        path = path,
                        color = AegisPrimaryCyan,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw point markers
                    points.forEach { pt ->
                        drawCircle(
                            color = AegisSecureGreen,
                            radius = 3.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("00:00 UTC", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AegisTextMuted)
                Text("12:00 UTC", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AegisTextMuted)
                Text("24:00 UTC (LIVE)", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AegisSecureGreen)
            }
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AegisDarkBg)
            .padding(10.dp)
    ) {
        Column {
            Text(title, fontSize = 9.sp, color = AegisTextMuted, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 8.sp, color = AegisTextSecondary)
        }
    }
}
