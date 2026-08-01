sed -i '/import androidx.compose.ui.platform.LocalContext/a \
import androidx.compose.ui.platform.LocalView\
import android.view.HapticFeedbackConstants' app/src/main/java/com/example/ui/screens/ThreatIntelligenceScreen.kt

sed -i '/ThreatTrendChart()/a \
                Spacer(modifier = Modifier.height(16.dp))\
                val view = LocalView.current\
                var showAlert by remember { mutableStateOf(false) }\
                Button(\
                    onClick = {\
                        showAlert = true\
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)\
                    },\
                    colors = ButtonDefaults.buttonColors(containerColor = AegisDangerRed),\
                    modifier = Modifier.fillMaxWidth()\
                ) {\
                    Text("Scan for Critical Alerts", fontWeight = FontWeight.Bold, color = Color.White)\
                }\
                if (showAlert) {\
                    Spacer(modifier = Modifier.height(8.dp))\
                    Text("CRITICAL: Rootkit detected in boot partition!", color = AegisDangerRed, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)\
                }' app/src/main/java/com/example/ui/screens/ThreatIntelligenceScreen.kt
