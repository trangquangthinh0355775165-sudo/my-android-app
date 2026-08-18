package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.SamsungBlue

@Composable
fun SamsungRecentApps(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("samsung_recent_apps_view")
            .fillMaxSize()
            .background(Color(0xDF0D0E12))
            .clickable(onClick = { viewModel.goHome() })
            .padding(vertical = 48.dp, horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Text(
                text = "Ứng dụng gần đây",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Carousel
            if (state.runningAppHistory.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Không có ứng dụng nào được mở gần đây",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(state.runningAppHistory, key = { it.name }) { app ->
                        val (icon, gradient, label) = getAppIconDetails(app)

                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .height(340.dp)
                                .shadow(elevation = 12.dp, shape = RoundedCornerShape(26.dp))
                                .clip(RoundedCornerShape(26.dp))
                                .clickable {
                                    viewModel.openApp(app)
                                },
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2128))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Card Top Bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2B2E38))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(RoundedCornerShape(7.dp))
                                                .background(gradient.first()),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        Text(
                                            text = label,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.closeRecentApp(app) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Close app",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Card Body / Preview
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(listOf(Color(0xFF181A20), Color(0xFF101216)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(Brush.linearGradient(gradient)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                        Text(
                                            text = "Nhấn để mở",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom "Đóng tất cả" (Close all) Button
            Button(
                onClick = { viewModel.closeAllRecentApps() },
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier
                    .testTag("close_all_recent_apps")
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Đóng tất cả",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
