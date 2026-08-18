package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*

private data class StoreApp(
    val id: Int,
    val name: String,
    val dev: String,
    val rating: String,
    val size: String,
    val isInstalled: Boolean = false
)

@Composable
fun GalaxyStoreApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val featuredApps = remember {
        mutableStateListOf(
            StoreApp(1, "Expert RAW", "Samsung Electronics", "4.8 ★", "64 MB", true),
            StoreApp(2, "Good Lock 2024", "Good Lock Labs", "4.9 ★", "28 MB", true),
            StoreApp(3, "Galaxy Enhance-X", "Samsung AI Research", "4.7 ★", "85 MB", false),
            StoreApp(4, "SoundAssistant", "Samsung R&D", "4.6 ★", "15 MB", false),
            StoreApp(5, "Camera Assistant", "Samsung Electronics", "4.8 ★", "18 MB", true)
        )
    }

    Scaffold(
        modifier = modifier
            .testTag("samsung_galaxy_store_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Galaxy Store",
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Store",
                    tint = if (state.isDarkMode) Color.White else Color.Black
                )
            }

            // Featured Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SamsungBlue)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Độc quyền trên Galaxy S24",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Trọn bộ công cụ sáng tạo Galaxy AI",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Featured List
            Text(
                text = "Ứng dụng & Tiện ích Samsung",
                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(featuredApps, key = { it.id }) { app ->
                    var isDownloading by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SamsungBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Apps,
                                        contentDescription = null,
                                        tint = SamsungBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = app.name,
                                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${app.dev} • ${app.rating} (${app.size})",
                                        color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isDownloading = true
                                    viewModel.vibrateShort(30)
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (app.isInstalled) Color.Gray.copy(alpha = 0.2f) else SamsungBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isDownloading) "Đang tải..." else (if (app.isInstalled) "Mở" else "Cài đặt"),
                                    fontSize = 12.sp,
                                    color = if (app.isInstalled) (if (state.isDarkMode) Color.White else Color.Black) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
