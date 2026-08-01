package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisDangerRed
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary

@Composable
fun AuthenticationErrorView(
    errorMessage: String,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AegisSurface)
            .border(1.dp, AegisDangerRed, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = AegisDangerRed,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AUTHENTICATION FAILED",
            color = AegisDangerRed,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            color = AegisTextPrimary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AegisDarkBg)
                .padding(12.dp)
        ) {
            Text(
                text = "Troubleshooting Steps:\n" +
                        "1. Open device Settings.\n" +
                        "2. Go to Security > Biometrics.\n" +
                        "3. Add a fingerprint or face scan.\n" +
                        "4. Ensure your sensor is clean.",
                color = AegisTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val context = androidx.compose.ui.platform.LocalContext.current

        Button(
            onClick = {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, 
                                    androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        }
                        context.startActivity(intent)
                    } else {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                        context.startActivity(intent)
                    }
                } catch (e: Throwable) {
                    android.widget.Toast.makeText(context, "Cannot open enrollment settings.", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AegisDarkBg),
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan)
        ) {
            Text("ENROLL BIOMETRICS", color = AegisPrimaryCyan, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onTryAgain,
            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("TRY AGAIN", color = AegisDarkBg, fontWeight = FontWeight.Bold)
        }
    }
}
