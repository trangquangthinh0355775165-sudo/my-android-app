package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*

private data class Bookmark(
    val title: String,
    val url: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun InternetApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var urlInput by remember { mutableStateOf("https://www.samsung.com/vn") }
    var pageTitle by remember { mutableStateOf("Samsung Việt Nam | Galaxy AI S24") }
    var isBrowsingArticle by remember { mutableStateOf(false) }

    val quickBookmarks = listOf(
        Bookmark("Google", "google.com", Icons.Rounded.Search, Color(0xFF4285F4)),
        Bookmark("Samsung", "samsung.com/vn", Icons.Rounded.Smartphone, SamsungBlue),
        Bookmark("VnExpress", "vnexpress.net", Icons.Rounded.Newspaper, Color(0xFF9E1428)),
        Bookmark("YouTube", "youtube.com", Icons.Rounded.PlayCircle, Color(0xFFFF0000)),
        Bookmark("Dân Trí", "dantri.com.vn", Icons.Rounded.Article, Color(0xFF008848)),
        Bookmark("AI Studio", "ai.google.dev", Icons.Rounded.AutoAwesome, Color(0xFF8E44AD))
    )

    Scaffold(
        modifier = modifier
            .testTag("samsung_internet_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        bottomBar = {
            NavigationBar(
                containerColor = if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface
            ) {
                IconButton(onClick = { isBrowsingArticle = false }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = if (state.isDarkMode) Color.White else Color.Black)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.ArrowForward, contentDescription = "Forward", tint = Color.Gray)
                }
                IconButton(onClick = { isBrowsingArticle = false }) {
                    Icon(Icons.Rounded.Home, contentDescription = "Home", tint = SamsungBlue)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.BookmarkBorder, contentDescription = "Bookmarks", tint = if (state.isDarkMode) Color.White else Color.Black)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Tabs", tint = if (state.isDarkMode) Color.White else Color.Black)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // URL Bar
            TextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock, contentDescription = "Secure", tint = Color(0xFF2ECC71), modifier = Modifier.size(16.dp))
                },
                trailingIcon = {
                    IconButton(onClick = {
                        isBrowsingArticle = true
                        pageTitle = urlInput
                    }) {
                        Icon(Icons.Rounded.ArrowForward, contentDescription = "Go", tint = SamsungBlue)
                    }
                },
                shape = RoundedCornerShape(22.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                    unfocusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isBrowsingArticle) {
                // HOME DASHBOARD WITH BOOKMARKS
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Trang ưa thích",
                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickBookmarks) { b ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        urlInput = "https://${b.url}"
                                        pageTitle = b.title
                                        isBrowsingArticle = true
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(b.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(b.icon, contentDescription = b.title, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                    Text(
                                        text = b.title,
                                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Samsung AI Web Assist Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SamsungBlue.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = SamsungBlue, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Galaxy AI Web Assist", color = SamsungBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Tóm tắt trang web và dịch thuật tự động theo thời gian thực", color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                // ARTICLE / WEB PAGE PREVIEW
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = pageTitle,
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        AssistChip(
                            onClick = { },
                            label = { Text("Galaxy AI: Tóm tắt bài viết", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = SamsungBlue, modifier = Modifier.size(14.dp)) }
                        )

                        Divider(color = Color.Gray.copy(alpha = 0.2f))

                        Text(
                            text = "Samsung Galaxy S24 Ultra mở ra kỷ nguyên mới với công nghệ Galaxy AI tiên tiến. Máy sở hữu khung viền Titanium bền bỉ, màn hình Dynamic AMOLED 2X 6.8 inch phẳng với kính cường lực Gorilla Armor chống phản xạ vượt trội.\n\nHệ thống camera 200MP ProVisual Engine cho chất lượng ảnh chụp đêm đỉnh cao và khả năng zoom quang học lên đến 100x Space Zoom.",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
