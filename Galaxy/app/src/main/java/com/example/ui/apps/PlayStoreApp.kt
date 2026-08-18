package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.system.SystemNotification
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PlayAppItem(
    val id: String,
    val name: String,
    val developer: String,
    val category: String,
    val rating: Double,
    val reviewCount: String,
    val size: String,
    val downloads: String,
    val iconBg: List<Color>,
    val icon: ImageVector,
    var isInstalled: Boolean = false,
    var isDownloading: Boolean = false,
    var downloadProgress: Float = 0f
)

@Composable
fun PlayStoreApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedBottomNav by remember { mutableStateOf(0) } // 0: Games, 1: Apps, 2: Books
    var selectedCategoryChip by remember { mutableStateOf(0) } // 0: For you, 1: Top charts, 2: Kids, 3: Categories
    var searchQuery by remember { mutableStateOf("") }
    var selectedAppDetail by remember { mutableStateOf<PlayAppItem?>(null) }

    val playApps = remember {
        mutableStateListOf(
            PlayAppItem(
                id = "tiktok",
                name = "TikTok",
                developer = "TikTok Pte. Ltd.",
                category = "Xem video & Sáng tạo",
                rating = 4.5,
                reviewCount = "68 Tr",
                size = "95 MB",
                downloads = "1 T+",
                iconBg = listOf(Color(0xFF000000), Color(0xFF25F4EE)),
                icon = Icons.Rounded.MusicVideo,
                isInstalled = true
            ),
            PlayAppItem(
                id = "zalo",
                name = "Zalo",
                developer = "Zalo Group (VNG)",
                category = "Liên lạc & Nhắn tin",
                rating = 4.4,
                reviewCount = "12 Tr",
                size = "82 MB",
                downloads = "100 Tr+",
                iconBg = listOf(Color(0xFF0068FF), Color(0xFF0040A8)),
                icon = Icons.Rounded.ChatBubble,
                isInstalled = true
            ),
            PlayAppItem(
                id = "lienquan",
                name = "Liên Quân Mobile",
                developer = "Garena Mobile Private",
                category = "Chiến thuật & MOBA",
                rating = 4.6,
                reviewCount = "8.5 Tr",
                size = "450 MB",
                downloads = "50 Tr+",
                iconBg = listOf(Color(0xFFE74C3C), Color(0xFF962D22)),
                icon = Icons.Rounded.SportsEsports,
                isInstalled = false
            ),
            PlayAppItem(
                id = "shopee",
                name = "Shopee: Mua Sắm Online",
                developer = "Shopee Vietnam",
                category = "Mua sắm",
                rating = 4.6,
                reviewCount = "15 Tr",
                size = "78 MB",
                downloads = "100 Tr+",
                iconBg = listOf(Color(0xFFEE4D2D), Color(0xFFFF7337)),
                icon = Icons.Rounded.ShoppingBag,
                isInstalled = false
            ),
            PlayAppItem(
                id = "capcut",
                name = "CapCut - Chỉnh sửa video",
                developer = "Bytedance Pte. Ltd.",
                category = "Chụp ảnh & Video",
                rating = 4.7,
                reviewCount = "22 Tr",
                size = "110 MB",
                downloads = "500 Tr+",
                iconBg = listOf(Color(0xFF1E272E), Color(0xFF485460)),
                icon = Icons.Rounded.MovieCreation,
                isInstalled = false
            ),
            PlayAppItem(
                id = "freefire",
                name = "Free Fire: Cuộc Chiến Sinh Tồn",
                developer = "Garena International I",
                category = "Hành động & Bắn súng",
                rating = 4.3,
                reviewCount = "118 Tr",
                size = "380 MB",
                downloads = "1 T+",
                iconBg = listOf(Color(0xFFF39C12), Color(0xFFD35400)),
                icon = Icons.Rounded.Whatshot,
                isInstalled = false
            ),
            PlayAppItem(
                id = "chatgpt",
                name = "ChatGPT",
                developer = "OpenAI",
                category = "Năng suất & AI",
                rating = 4.8,
                reviewCount = "6.2 Tr",
                size = "45 MB",
                downloads = "100 Tr+",
                iconBg = listOf(Color(0xFF10A37F), Color(0xFF0D8265)),
                icon = Icons.Rounded.AutoAwesome,
                isInstalled = false
            ),
            PlayAppItem(
                id = "youtube",
                name = "YouTube",
                developer = "Google LLC",
                category = "Xem video & Trực tiếp",
                rating = 4.6,
                reviewCount = "142 Tr",
                size = "52 MB",
                downloads = "10 T+",
                iconBg = listOf(Color(0xFFFF0000), Color(0xFFCC0000)),
                icon = Icons.Rounded.PlayCircle,
                isInstalled = true
            ),
            PlayAppItem(
                id = "facebook",
                name = "Facebook",
                developer = "Meta Platforms, Inc.",
                category = "Mạng xã hội",
                rating = 4.2,
                reviewCount = "135 Tr",
                size = "64 MB",
                downloads = "5 T+",
                iconBg = listOf(Color(0xFF1877F2), Color(0xFF0E52AB)),
                icon = Icons.Rounded.People,
                isInstalled = true
            ),
            PlayAppItem(
                id = "spotify",
                name = "Spotify: Nhạc & Podcast",
                developer = "Spotify AB",
                category = "Nhạc & Âm thanh",
                rating = 4.7,
                reviewCount = "31 Tr",
                size = "35 MB",
                downloads = "1 T+",
                iconBg = listOf(Color(0xFF1DB954), Color(0xFF14833B)),
                icon = Icons.Rounded.Headphones,
                isInstalled = false
            )
        )
    }

    fun startDownload(app: PlayAppItem) {
        if (app.isInstalled || app.isDownloading) return
        val index = playApps.indexOfFirst { it.id == app.id }
        if (index == -1) return

        viewModel.vibrateShort(35)
        playApps[index] = playApps[index].copy(isDownloading = true, downloadProgress = 0.05f)

        coroutineScope.launch {
            for (step in 1..10) {
                delay(300)
                val progress = step / 10f
                val idx = playApps.indexOfFirst { it.id == app.id }
                if (idx != -1) {
                    playApps[idx] = playApps[idx].copy(downloadProgress = progress)
                }
            }
            val finalIdx = playApps.indexOfFirst { it.id == app.id }
            if (finalIdx != -1) {
                playApps[finalIdx] = playApps[finalIdx].copy(isDownloading = false, isInstalled = true, downloadProgress = 1f)
            }

            viewModel.vibrateShort(60)
            coroutineScope.launch {
                // Done installing
            }
        }
    }

    Scaffold(
        modifier = modifier
            .testTag("google_play_store_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        bottomBar = {
            NavigationBar(
                containerColor = if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface
            ) {
                NavigationBarItem(
                    selected = selectedBottomNav == 0,
                    onClick = { selectedBottomNav = 0 },
                    icon = { Icon(Icons.Rounded.SportsEsports, contentDescription = "Games") },
                    label = { Text("Trò chơi", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0086F8),
                        selectedTextColor = Color(0xFF0086F8),
                        indicatorColor = Color(0xFF0086F8).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Rounded.Apps, contentDescription = "Apps") },
                    label = { Text("Ứng dụng", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0086F8),
                        selectedTextColor = Color(0xFF0086F8),
                        indicatorColor = Color(0xFF0086F8).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Rounded.Book, contentDescription = "Books") },
                    label = { Text("Sách", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0086F8),
                        selectedTextColor = Color(0xFF0086F8),
                        indicatorColor = Color(0xFF0086F8).copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Play Pill Search Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search Google Play",
                                tint = if (state.isDarkMode) Color.White.copy(alpha = 0.7f) else Color.Gray
                            )

                            Text(
                                text = "Tìm kiếm trên Google Play",
                                color = if (state.isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Mic,
                                contentDescription = "Voice",
                                tint = if (state.isDarkMode) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )

                            // Google Account Avatar
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Category Chips Row (Cho bạn, Bảng xếp hạng, Trẻ em, Danh mục)
                val categoryChips = listOf("Cho bạn", "Bảng xếp hạng", "Trẻ em", "Danh mục")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categoryChips.size) { index ->
                        val isSelected = selectedCategoryChip == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryChip = index },
                            label = {
                                Text(
                                    text = categoryChips[index],
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0086F8).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFF0086F8)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Hero Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0086F8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Lựa chọn của biên tập viên",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Khám phá top ứng dụng & game hot nhất 2024",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Apps List Header
                Text(
                    text = if (selectedBottomNav == 0) "Trò chơi phổ biến" else "Ứng dụng đề xuất",
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Apps List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(playApps, key = { it.id }) { app ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedAppDetail = app },
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
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // App Icon
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Brush.linearGradient(app.iconBg)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = app.icon,
                                            contentDescription = app.name,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = app.name,
                                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${app.developer} • ${app.category}",
                                            color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = "${app.rating} ★",
                                                color = if (state.isDarkMode) Color(0xFFFFD54F) else Color(0xFFF39C12),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "• ${app.size} • ${app.downloads}",
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                // Install / Downloading / Open Button
                                if (app.isDownloading) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { app.downloadProgress },
                                            color = Color(0xFF0086F8),
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Text(
                                            text = "${(app.downloadProgress * 100).toInt()}%",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0086F8)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { startDownload(app) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (app.isInstalled) Color.Gray.copy(alpha = 0.25f) else Color(0xFF0086F8)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = if (app.isInstalled) "Mở" else "Cài đặt",
                                            fontSize = 12.sp,
                                            color = if (app.isInstalled) (if (state.isDarkMode) Color.White else Color.Black) else Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // App Detail Sheet Dialog
    if (selectedAppDetail != null) {
        val app = selectedAppDetail!!

        AlertDialog(
            onDismissRequest = { selectedAppDetail = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(app.iconBg)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(app.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(app.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(app.developer, fontSize = 11.sp, color = Color(0xFF0086F8))
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${app.rating} ★", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${app.reviewCount} đánh giá", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(app.size, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Dung lượng", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(app.downloads, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Lượt tải xuống", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    Text(
                        text = "Ứng dụng ${app.name} chính thức trên Google Play dành cho Samsung Galaxy S24 Ultra. Tối ưu hoá phần cứng Snapdragon 8 Gen 3 và One UI 6.1.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        startDownload(app)
                        selectedAppDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0086F8))
                ) {
                    Text(if (app.isInstalled) "Mở ứng dụng" else "Cài đặt")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppDetail = null }) {
                    Text("Đóng")
                }
            }
        )
    }
}
