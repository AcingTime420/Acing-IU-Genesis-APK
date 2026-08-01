package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

@Composable
fun ThreatIntelligenceScreen(viewModel: AcingViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "THREAT INTELLIGENCE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            IconButton(onClick = { 
                exportAndShareReport(context) 
            }) {
                Icon(Icons.Default.Share, contentDescription = "Share Report", tint = MaterialTheme.colorScheme.primary)
            }
        }
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VULNERABILITY FREQUENCY TRENDS",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Data Visualization Component
                ThreatTrendChart()
            }
        }
    }
}

@Composable
fun ThreatTrendChart() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxVal = 100f
        val points = listOf(20f, 45f, 30f, 80f, 65f, 90f)
        val stepX = size.width / (points.size - 1)
        val scaleY = size.height / maxVal
        
        points.forEachIndexed { index, value ->
            if (index < points.size - 1) {
                drawLine(
                    color = AegisDangerRed,
                    start = Offset(index * stepX, size.height - (value * scaleY)),
                    end = Offset((index + 1) * stepX, size.height - (points[index + 1] * scaleY)),
                    strokeWidth = 5f
                )
            }
            drawCircle(
                color = AegisPrimaryCyan,
                radius = 8f,
                center = Offset(index * stepX, size.height - (value * scaleY))
            )
        }
    }
}

fun exportAndShareReport(context: Context) {
    val report = JSONObject()
    report.put("title", "Threat Analysis Report")
    report.put("severity", "HIGH")
    report.put("details", "Multiple vulnerabilities detected in kernel modules.")
    
    val file = File(context.cacheDir, "threat_report.json")
    file.writeText(report.toString(4))
    
    // In a real app we'd use FileProvider, but for demo we just write the file
    // and print it out. Or we can just use intent with plain text.
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Threat Report")
        putExtra(Intent.EXTRA_TEXT, report.toString(4))
    }
    context.startActivity(Intent.createChooser(intent, "Share Report"))
}
