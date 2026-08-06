package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AcingViewModel
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextMuted
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.AegisWarningGold

@Composable
fun ShopifyLicenseCard(
    viewModel: AcingViewModel,
    modifier: Modifier = Modifier
) {
    val licenseState by viewModel.shopifyLicenseState.collectAsState()
    val isValidating by viewModel.isValidatingShopifyLicense.collectAsState()
    var inputToken by remember { mutableStateOf("GENESIS-PRO-S25U-938U-ENTERPRISE") }

    Card(
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (licenseState.isLicensed) AegisSecureGreen else AegisBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shopify_license_validation_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopify Storefront",
                        tint = AegisPrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Headless Shopify Storefront Licensing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AegisTextPrimary
                        )
                        Text(
                            text = "GraphQL API Validation for Digital Token Purchase",
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextSecondary
                        )
                    }
                }

                SeverityBadge(
                    severity = if (licenseState.isLicensed) "SECURE" else "WARNING"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputToken,
                onValueChange = { inputToken = it },
                label = { Text("Shopify Storefront Customer Purchase Token", color = AegisTextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = AegisPrimaryCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shopify_token_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AegisPrimaryCyan,
                    unfocusedBorderColor = AegisBorder,
                    focusedTextColor = AegisTextPrimary,
                    unfocusedTextColor = AegisTextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.validateShopifyLicenseToken(inputToken) },
                    enabled = !isValidating && inputToken.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("validate_shopify_token_button")
                ) {
                    if (isValidating) {
                        CircularProgressIndicator(
                            color = androidx.compose.ui.graphics.Color.Black,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Querying Storefront...", color = androidx.compose.ui.graphics.Color.Black)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify Token via GraphQL", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (licenseState.lastValidatedTimestamp > 0) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (licenseState.isLicensed)
                            AegisSecureGreen.copy(alpha = 0.08f)
                        else
                            AegisWarningGold.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (licenseState.isLicensed) AegisSecureGreen else AegisWarningGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (licenseState.isLicensed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (licenseState.isLicensed) AegisSecureGreen else AegisWarningGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tier: ${licenseState.licenseTier.displayName}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AegisTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Email: ${licenseState.customerEmail}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = AegisTextSecondary
                        )

                        if (licenseState.activeSubscriptionId != null) {
                            Text(
                                text = "Subscription Ref: ${licenseState.activeSubscriptionId}",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = AegisPrimaryCyan
                            )
                        }

                        Text(
                            text = licenseState.validationMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
