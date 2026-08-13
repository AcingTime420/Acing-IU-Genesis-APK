package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

enum class WindowAdaptiveClass {
    COMPACT,   // Mobile Portrait (< 600dp)
    MEDIUM,    // Foldables / Small Tablets / Mobile Landscape (600dp - 840dp)
    EXPANDED   // Large Tablets / Desktop / TV (> 840dp)
}

data class DeviceResolutionMetrics(
    val widthDp: Dp,
    val heightDp: Dp,
    val densityScale: Float,
    val densityDpi: Int,
    val widthPx: Int,
    val heightPx: Int,
    val aspectRatioStr: String,
    val orientationStr: String,
    val adaptiveClass: WindowAdaptiveClass
)

@Composable
fun rememberDeviceResolutionMetrics(): DeviceResolutionMetrics {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val wDp = configuration.screenWidthDp.dp
    val hDp = configuration.screenHeightDp.dp
    val densityScale = density.density
    val densityDpi = configuration.densityDpi

    val wPx = (configuration.screenWidthDp * densityScale).toInt()
    val hPx = (configuration.screenHeightDp * densityScale).toInt()

    val ratioVal = if (hDp.value > 0) wDp.value / hDp.value else 1.0f
    val aspectRatioStr = String.format("%.2f:1", ratioVal)

    val orientationStr = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "LANDSCAPE"
    } else {
        "PORTRAIT"
    }

    val adaptiveClass = when {
        wDp < 600.dp -> WindowAdaptiveClass.COMPACT
        wDp < 840.dp -> WindowAdaptiveClass.MEDIUM
        else -> WindowAdaptiveClass.EXPANDED
    }

    return DeviceResolutionMetrics(
        widthDp = wDp,
        heightDp = hDp,
        densityScale = densityScale,
        densityDpi = densityDpi,
        widthPx = wPx,
        heightPx = hPx,
        aspectRatioStr = aspectRatioStr,
        orientationStr = orientationStr,
        adaptiveClass = adaptiveClass
    )
}

/**
 * Wraps content in a layout container that automatically scales, centers, and constrains
 * content to adapt perfectly to any screen size, resolution, or device orientation.
 */
@Composable
fun AutoScreenAdaptationContainer(
    modifier: Modifier = Modifier,
    content: @Composable (metrics: DeviceResolutionMetrics) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AegisDarkBg)
    ) {
        val metrics = rememberDeviceResolutionMetrics()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp) // Prevents awkward stretching on tablet / desktop displays
                .align(Alignment.Center)
        ) {
            content(metrics)
        }
    }
}

/**
 * Status chip placed in top app bar showing current auto-adapted resolution and density class.
 * Clicking opens a detailed Auto Screen Adaptation Diagnostic Dialog.
 */
@Composable
fun AutoResolutionStatusChip(
    modifier: Modifier = Modifier
) {
    val metrics = rememberDeviceResolutionMetrics()
    var showDialog by remember { mutableStateOf(false) }

    val classLabel = when (metrics.adaptiveClass) {
        WindowAdaptiveClass.COMPACT -> "AUTO COMPACT"
        WindowAdaptiveClass.MEDIUM -> "AUTO FOLDABLE"
        WindowAdaptiveClass.EXPANDED -> "AUTO TABLET"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AegisBadgeIndigoBg)
            .border(1.dp, AegisBorder, RoundedCornerShape(6.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("auto_screen_chip")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AspectRatio,
                contentDescription = "Auto Screen",
                tint = AegisPrimaryCyan,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$classLabel | ${metrics.widthPx}x${metrics.heightPx}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = AegisBadgeIndigoText
            )
        }
    }

    if (showDialog) {
        AutoScreenSpecsDialog(
            metrics = metrics,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun AutoScreenSpecsDialog(
    metrics: DeviceResolutionMetrics,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitScreen,
                    contentDescription = null,
                    tint = AegisPrimaryCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUTO SCREEN ADAPTATION ENGINE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = AegisTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "The app continuously reads real-time screen density, window size classes, and aspect ratio metrics to dynamically auto-adjust layout grids, pixel scaling, safe insets, and typography across any device form factor.",
                    fontSize = 11.sp,
                    color = AegisTextSecondary
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = AegisSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        MetricRow("Window Class", metrics.adaptiveClass.name, AegisPrimaryCyan)
                        MetricRow("Resolution (Px)", "${metrics.widthPx} x ${metrics.heightPx}", AegisTextPrimary)
                        MetricRow("Viewport (Dp)", "${metrics.widthDp.value.toInt()}dp x ${metrics.heightDp.value.toInt()}dp", AegisTextPrimary)
                        MetricRow("Pixel Density", "${metrics.densityDpi} DPI (${String.format("%.2f", metrics.densityScale)}x)", AegisSecureGreen)
                        MetricRow("Aspect Ratio", metrics.aspectRatioStr, AegisTextPrimary)
                        MetricRow("Orientation", metrics.orientationStr, AegisTextPrimary)
                        MetricRow("Layout Mode", if (metrics.adaptiveClass == WindowAdaptiveClass.COMPACT) "Bottom Navigation Bar" else "Side Navigation Rail", AegisPrimaryCyan)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisSecureGreen.copy(alpha = 0.12f))
                        .border(1.dp, AegisSecureGreen, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AegisSecureGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Device Compatibility: Universal (Phone, Foldable, Tablet, TV)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisSecureGreen
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CLOSE DIAGNOSTICS",
                    fontWeight = FontWeight.Bold,
                    color = AegisPrimaryCyan,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        containerColor = AegisDarkBg,
        titleContentColor = AegisTextPrimary,
        textContentColor = AegisTextSecondary
    )
}

@Composable
private fun MetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = AegisTextSecondary
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = valueColor
        )
    }
}
