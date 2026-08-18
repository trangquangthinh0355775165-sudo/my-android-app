package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType
import com.example.system.GalaxyViewModel

@Composable
fun SamsungEdgePanel(
    isOpen: Boolean,
    viewModel: GalaxyViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Edge Handle (Visible when panel is closed)
        if (!isOpen) {
            Box(
                modifier = Modifier
                    .testTag("edge_panel_handle")
                    .width(14.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(Color(0x990381FE))
                    .clickable { viewModel.toggleEdgePanel(true) }
            )
        }

        // Expanded Edge Panel
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                    .background(Color(0xEE16181D))
                    .padding(vertical = 40.dp, horizontal = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edge Panel",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.toggleEdgePanel(false) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "Close Edge Panel",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.15f))

                    Text(
                        text = "Ứng dụng ưa thích",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Quick App Icons Grid
                    val edgeApps = listOf(
                        GalaxyAppType.PHONE,
                        GalaxyAppType.MESSAGES,
                        GalaxyAppType.CAMERA,
                        GalaxyAppType.NOTES,
                        GalaxyAppType.CALCULATOR,
                        GalaxyAppType.WEATHER
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        edgeApps.forEach { app ->
                            SamsungAppIcon(
                                app = app,
                                iconSize = 46.dp,
                                showLabel = true,
                                labelColor = Color.White,
                                onClick = {
                                    viewModel.openApp(app)
                                    viewModel.toggleEdgePanel(false)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Tools Shortcut (S-Pen / Flashlight)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleSPen()
                                viewModel.toggleEdgePanel(false)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Draw,
                                contentDescription = "S-Pen",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bút S-Pen",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
