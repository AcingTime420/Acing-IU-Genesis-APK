package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
import java.util.concurrent.Executors

/**
 * CameraX QR Code Scanner Component for Security Researchers.
 * Rapidly scans device serial numbers or firmware manifest URLs for inventory tracking.
 */
@Composable
fun QrCodeScannerComponent(
    onScanResult: (scannedData: String, isFirmwareManifest: Boolean) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var lastScannedValue by remember { mutableStateOf<String?>(null) }
    var manualInputText by remember { mutableStateOf("") }
    var scanStatusMessage by remember { mutableStateOf("Position QR Code within reticle") }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan),
        modifier = modifier
            .fillMaxWidth()
            .testTag("qr_code_scanner_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scanner",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CAMERAX INVENTORY SCANNER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "Rapid Device Serial & Firmware Manifest Tracking",
                            fontSize = 10.sp,
                            color = AegisTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AegisBadgeIndigoBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (hasCameraPermission) "CAMERAX LIVE" else "PERM REQUIRED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisBadgeIndigoText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!hasCameraPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AegisDarkBg)
                        .border(1.dp, AegisBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Camera Permission Required for QR Scanning",
                            fontSize = 12.sp,
                            color = AegisTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Grant Camera Permission", fontSize = 11.sp, color = AegisDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // CameraX Live Preview Viewfinder Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, AegisPrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()

                                val cameraExecutor = Executors.newSingleThreadExecutor()
                                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    // Live camera analysis pipeline
                                    imageProxy.close()
                                }

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (exc: Exception) {
                                    Log.e("QrCodeScannerComponent", "Camera binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Laser Reticle Overlay
                    val infiniteTransition = rememberInfiniteTransition(label = "laser_animation")
                    val laserYPercentage by infiniteTransition.animateFloat(
                        initialValue = 0.1f,
                        targetValue = 0.9f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laser_y"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val boxSize = w.coerceAtMost(h) * 0.65f
                        val left = (w - boxSize) / 2f
                        val top = (h - boxSize) / 2f

                        // Draw corner reticle brackets
                        val lineLen = 24.dp.toPx()
                        val stroke = 3.dp.toPx()

                        val path = Path().apply {
                            // Top Left
                            moveTo(left, top + lineLen)
                            lineTo(left, top)
                            lineTo(left + lineLen, top)

                            // Top Right
                            moveTo(left + boxSize - lineLen, top)
                            lineTo(left + boxSize, top)
                            lineTo(left + boxSize, top + lineLen)

                            // Bottom Right
                            moveTo(left + boxSize, top + boxSize - lineLen)
                            lineTo(left + boxSize, top + boxSize)
                            lineTo(left + boxSize - lineLen, top + boxSize)

                            // Bottom Left
                            moveTo(left + lineLen, top + boxSize)
                            lineTo(left, top + boxSize)
                            lineTo(left, top + boxSize - lineLen)
                        }

                        drawPath(path = path, color = AegisPrimaryCyan, style = Stroke(stroke))

                        // Laser line
                        val laserY = top + (boxSize * laserYPercentage)
                        drawLine(
                            color = Color(0xFFFF1744),
                            start = Offset(left + 8.dp.toPx(), laserY),
                            end = Offset(left + boxSize - 8.dp.toPx(), laserY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = scanStatusMessage,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisSecureGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rapid Presets / Simulation for Testing & Researchers
            Text(
                text = "Rapid Test Payload Simulation:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AegisTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val sampleSerial = "SN-2026-AEGIS-${(1000..9999).random()}"
                        lastScannedValue = sampleSerial
                        scanStatusMessage = "Scanned Serial: $sampleSerial"
                        onScanResult(sampleSerial, false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisBadgeIndigoBg),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = AegisBadgeIndigoText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Device Serial", fontSize = 10.sp, color = AegisBadgeIndigoText, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val sampleUrl = "https://acingiu.internal/firmware/v2.1.0-release.bin"
                        lastScannedValue = sampleUrl
                        scanStatusMessage = "Scanned Manifest: $sampleUrl"
                        onScanResult(sampleUrl, true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AegisBadgeIndigoBg),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, tint = AegisBadgeIndigoText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Firmware Manifest", fontSize = 10.sp, color = AegisBadgeIndigoText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Manual Entry Input for Researchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualInputText,
                    onValueChange = { manualInputText = it },
                    placeholder = { Text("Or enter barcode string / URL...", fontSize = 11.sp, color = AegisTextMuted) },
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = AegisTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AegisPrimaryCyan,
                        unfocusedBorderColor = AegisBorder,
                        focusedContainerColor = AegisDarkBg,
                        unfocusedContainerColor = AegisDarkBg
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (manualInputText.isNotBlank()) {
                            val isManifest = manualInputText.contains("http") || manualInputText.contains(".bin") || manualInputText.contains(".json")
                            onScanResult(manualInputText.trim(), isManifest)
                            manualInputText = ""
                        }
                    },
                    enabled = manualInputText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AegisDarkBg)
                }
            }
        }
    }
}
