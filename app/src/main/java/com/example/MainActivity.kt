package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcingViewModel
import com.example.ui.AppTab
import com.example.ui.screens.AegisAiScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.FirmwareScreen
import com.example.ui.screens.ForensicsScreen
import com.example.ui.screens.GovernanceScreen
import com.example.ui.theme.AegisBadgeIndigoBg
import com.example.ui.theme.AegisBadgeIndigoText
import com.example.ui.theme.AegisBorder
import com.example.ui.theme.AegisDarkBg
import com.example.ui.theme.AegisPrimaryCyan
import com.example.ui.theme.AegisSecureGreen
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary
import com.example.ui.theme.AegisTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AcingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AcingGenesisApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcingGenesisApp(viewModel: AcingViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val lockdownActive by viewModel.zeroTrustLockdown.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Acing IU",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AegisTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AegisBadgeIndigoBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v1.3.1",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AegisBadgeIndigoText
                                )
                            }
                        }
                        Text(
                            text = "Genesis IRP | Zero-Trust Platform",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AegisTextSecondary
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (lockdownActive) com.example.ui.theme.AegisDangerRed.copy(alpha = 0.15f) else AegisBadgeIndigoBg)
                            .border(1.dp, if (lockdownActive) com.example.ui.theme.AegisDangerRed else AegisBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (lockdownActive) "LOCKDOWN" else "ZERO-TRUST OK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (lockdownActive) com.example.ui.theme.AegisDangerRed else AegisBadgeIndigoText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisDarkBg,
                    titleContentColor = AegisTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AegisDarkBg,
                contentColor = AegisTextSecondary,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(androidx.compose.foundation.BorderStroke(1.dp, AegisBorder))
                    .testTag("bottom_navigation_bar")
            ) {
                val navItems = listOf(
                    AppTab.DASHBOARD to ("Status" to Icons.Default.Dashboard),
                    AppTab.FIRMWARE to ("Firmware" to Icons.Default.FolderZip),
                    AppTab.DEVICES to ("Devices" to Icons.Default.PhoneAndroid),
                    AppTab.FORENSICS to ("Forensics" to Icons.Default.BugReport),
                    AppTab.AEGIS_AI to ("Aegis AI" to Icons.Default.SmartToy),
                    AppTab.GOVERNANCE to ("Security" to Icons.Default.AdminPanelSettings)
                )

                navItems.forEach { (tab, pair) ->
                    val (label, icon) = pair
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) AegisBadgeIndigoText else AegisTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AegisBadgeIndigoText else AegisTextSecondary,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = AegisBadgeIndigoBg
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = AegisDarkBg,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppTab.FIRMWARE -> FirmwareScreen(viewModel = viewModel)
                AppTab.DEVICES -> DevicesScreen(viewModel = viewModel)
                AppTab.FORENSICS -> ForensicsScreen(viewModel = viewModel)
                AppTab.AEGIS_AI -> AegisAiScreen(viewModel = viewModel)
                AppTab.GOVERNANCE -> GovernanceScreen(viewModel = viewModel)
            }
        }
    }
}
