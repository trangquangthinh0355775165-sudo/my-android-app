package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.components.SamsungAppIcon
import com.example.ui.components.SamsungStatusBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val sdfTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val sdfDate = remember { SimpleDateFormat("EEE, d 'thg' M", Locale("vi", "VN")) }
    var currentTime by remember { mutableStateOf(sdfTime.format(Date())) }
    var currentDate by remember { mutableStateOf(sdfDate.format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = sdfTime.format(now)
            currentDate = sdfDate.format(now)
            kotlinx.coroutines.delay(1000)
        }
    }

    val wallpaperRes = when (state.selectedWallpaperRes) {
        "img_samsung_wallpaper_2" -> R.drawable.img_samsung_wallpaper_2
        else -> R.drawable.img_samsung_wallpaper_1
    }

    Box(
        modifier = modifier
            .testTag("one_ui_home_screen")
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -25 && !state.isAppDrawerOpen) {
                        viewModel.toggleAppDrawer(true)
                    } else if (dragAmount > 25 && state.isAppDrawerOpen) {
                        viewModel.toggleAppDrawer(false)
                    } else if (dragAmount > 30 && !state.isAppDrawerOpen) {
                        viewModel.toggleNotificationShade(true)
                    }
                }
            }
    ) {
        // Wallpaper
        Image(
            painter = painterResource(id = wallpaperRes),
            contentDescription = "Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dim gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        // Status Bar
        SamsungStatusBar(
            state = state,
            onStatusClick = { viewModel.toggleNotificationShade(true) },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (!state.isAppDrawerOpen) {
            // Main Home Screen Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Samsung Weather & Clock Widget
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .clickable { viewModel.openApp(GalaxyAppType.WEATHER) },
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x4D000000))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = currentTime,
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = currentDate,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF4DA2FF),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Hà Nội",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Icon(
                                imageVector = Icons.Rounded.WbSunny,
                                contentDescription = "Sunny",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "29°",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Nắng đẹp",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Google / Samsung Search Bar Widget
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { viewModel.openApp(GalaxyAppType.INTERNET) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x66181A22))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tìm kiếm hoặc nhập URL...",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.Mic,
                                contentDescription = "Voice Search",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.CenterFocusWeak,
                                contentDescription = "Lens / Circle to Search",
                                tint = Color(0xFF34A853),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Main Home App Grid (4x3)
                val homeApps = listOf(
                    GalaxyAppType.PLAY_STORE,
                    GalaxyAppType.GALLERY,
                    GalaxyAppType.NOTES,
                    GalaxyAppType.CLOCK,
                    GalaxyAppType.CALCULATOR,
                    GalaxyAppType.WEATHER,
                    GalaxyAppType.RECORDER,
                    GalaxyAppType.MUSIC,
                    GalaxyAppType.GALAXY_STORE,
                    GalaxyAppType.SETTINGS
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(homeApps) { app ->
                        SamsungAppIcon(
                            app = app,
                            iconSize = 56.dp,
                            showLabel = true,
                            labelColor = Color.White,
                            onClick = { viewModel.openApp(app) }
                        )
                    }
                }

                // Bottom App Dock (Pinned 4 main apps)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dockApps = listOf(
                            GalaxyAppType.PHONE,
                            GalaxyAppType.MESSAGES,
                            GalaxyAppType.INTERNET,
                            GalaxyAppType.CAMERA
                        )
                        dockApps.forEach { app ->
                            SamsungAppIcon(
                                app = app,
                                iconSize = 54.dp,
                                showLabel = false,
                                badgeCount = if (app == GalaxyAppType.MESSAGES) 1 else 0,
                                onClick = { viewModel.openApp(app) }
                            )
                        }
                    }
                }
            }
        } else {
            // App Drawer (One UI Full App Menu)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE60E1015))
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // App Drawer Search Bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Tìm kiếm ứng dụng One UI...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.7f))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0x33FFFFFF),
                            unfocusedContainerColor = Color(0x22FFFFFF),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // All Apps Grid
                    val allApps = GalaxyAppType.values().filter {
                        it != GalaxyAppType.HOME && (searchQuery.isEmpty() || it.appName.contains(searchQuery, true))
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(22.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allApps) { app ->
                            SamsungAppIcon(
                                app = app,
                                iconSize = 56.dp,
                                showLabel = true,
                                labelColor = Color.White,
                                onClick = { viewModel.openApp(app) }
                            )
                        }
                    }

                    // Swipe Down Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleAppDrawer(false) }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Close Drawer",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Vuốt xuống để về màn hình chính",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
