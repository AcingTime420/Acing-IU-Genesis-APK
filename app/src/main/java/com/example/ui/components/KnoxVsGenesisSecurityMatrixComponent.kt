package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

data class SecurityComparisonItem(
    val category: String,
    val knoxBaseline: String,
    val genesisSpecification: String,
    val isElevated: Boolean = true
)

@Composable
fun KnoxVsGenesisSecurityMatrixComponent(
    modifier: Modifier = Modifier
) {
    val comparisonList = listOf(
        SecurityComparisonItem(
            category = "Platform Architecture",
            knoxBaseline = "Knox 3.13 (API level 40)",
            genesisSpecification = "Genesis Sentinel Mesh v4.0 (Knox 3.14+ Enterprise / API Level 42)"
        ),
        SecurityComparisonItem(
            category = "Data Encryption",
            knoxBaseline = "DualDAR 1.8.0 & MDF v3.3 (Dual-Layer Hardware)",
            genesisSpecification = "TriDAR 2.5.0 Quantum-Shield & MDF v4.0 (Triple-Layer Post-Quantum)"
        ),
        SecurityComparisonItem(
            category = "Government Cryptography",
            knoxBaseline = "FIPS BoringSSL & SKC / SCrypto v2.9",
            genesisSpecification = "NIST ML-KEM-1024 & ML-DSA-87 FIPS 140-3 BoringSSL Target"
        ),
        SecurityComparisonItem(
            category = "Access Control & Policy",
            knoxBaseline = "Enforcing SELinux SEPF Policies",
            genesisSpecification = "Autonomous Zero-Trust SELinux Policy Engine + Real-Time AVC Remediation"
        ),
        SecurityComparisonItem(
            category = "Hardware Root of Trust",
            knoxBaseline = "StrongBox EAL5+ ARM TrustZone",
            genesisSpecification = "Dual-Enclave StrongBox EAL6+ HSM + AVB 2.0 Rollback Guard v15"
        ),
        SecurityComparisonItem(
            category = "Threat Intelligence",
            knoxBaseline = "Periodic MDM Logging & Failsafes",
            genesisSpecification = "Continuous Sandbox Security Audit + Socket Scanner & TLS Header Audit"
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisPrimaryCyan.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("knox_vs_genesis_security_matrix_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AegisPrimaryCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AegisPrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SECURITY SPECIFICATIONS MATRIX",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = AegisPrimaryCyan
                        )
                        Text(
                            text = "Knox Defense Baseline vs. Acing IU: Genesis",
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AegisSecureGreen.copy(alpha = 0.15f))
                        .border(1.dp, AegisSecureGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NEXT-GEN ELEVATED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AegisSecureGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            comparisonList.forEach { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(AegisDarkBg, RoundedCornerShape(10.dp))
                        .border(1.dp, AegisBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisWarningGold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = AegisSecureGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ELEVATED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AegisSecureGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Knox Baseline
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Knox Defense: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AegisTextMuted
                        )
                        Text(
                            text = item.knoxBaseline,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Genesis Specification
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Acing Genesis: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AegisPrimaryCyan
                        )
                        Text(
                            text = item.genesisSpecification,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AegisSecureGreen
                        )
                    }
                }
            }
        }
    }
}
