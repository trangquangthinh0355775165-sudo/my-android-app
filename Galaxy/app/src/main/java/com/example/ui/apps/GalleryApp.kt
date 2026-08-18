package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GalleryItemEntity
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GalleryApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val galleryItems by viewModel.galleryItems.collectAsState()
    var selectedItem by remember { mutableStateOf<GalleryItemEntity?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Pictures, 1: Albums
    var showDetailsDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .testTag("samsung_gallery_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        bottomBar = {
            if (selectedItem == null) {
                NavigationBar(
                    containerColor = if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Rounded.PhotoLibrary, contentDescription = "Pictures") },
                        label = { Text("Hình ảnh", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SamsungBlue,
                            selectedTextColor = SamsungBlue,
                            indicatorColor = SamsungBlue.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Rounded.FolderCopy, contentDescription = "Albums") },
                        label = { Text("Album", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SamsungBlue,
                            selectedTextColor = SamsungBlue,
                            indicatorColor = SamsungBlue.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedItem == null) {
                // GALLERY GRID VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTab == 0) "Bộ sưu tập (${galleryItems.size})" else "Album ảnh",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search Photos",
                                tint = if (state.isDarkMode) Color.White else Color.Black
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        // Pictures Grid (3 columns)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(galleryItems, key = { it.id }) { item ->
                                val resId = when (item.drawableResName) {
                                    "img_gallery_city" -> R.drawable.img_gallery_city
                                    "img_samsung_wallpaper_2" -> R.drawable.img_samsung_wallpaper_2
                                    "img_samsung_wallpaper_1" -> R.drawable.img_samsung_wallpaper_1
                                    else -> R.drawable.img_gallery_nature
                                }

                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { selectedItem = item }
                                ) {
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    } else {
                        // Albums View
                        val albums = listOf(
                            "Camera" to galleryItems.filter { it.category == "Camera" }.size,
                            "Ảnh chụp màn hình" to 4,
                            "Hình nền Samsung" to 2,
                            "Yêu thích" to galleryItems.filter { it.isFavorite }.size
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(albums) { (albumName, count) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { selectedTab = 0 },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                    )
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.img_gallery_nature),
                                                contentDescription = albumName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = albumName,
                                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "$count mục",
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // FULLSCREEN PHOTO VIEWER
                val item = selectedItem!!
                val resId = when (item.drawableResName) {
                    "img_gallery_city" -> R.drawable.img_gallery_city
                    "img_samsung_wallpaper_2" -> R.drawable.img_samsung_wallpaper_2
                    "img_samsung_wallpaper_1" -> R.drawable.img_samsung_wallpaper_1
                    else -> R.drawable.img_gallery_nature
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    // Fullscreen Image
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = item.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Viewer Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedItem = null }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        IconButton(onClick = { showDetailsDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = "Details",
                                tint = Color.White
                            )
                        }
                    }

                    // Viewer Bottom Action Bar (Favorite, Edit, Share, Set Wallpaper, Delete)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.toggleFavoriteGalleryItem(item)
                            selectedItem = item.copy(isFavorite = !item.isFavorite)
                        }) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (item.isFavorite) Color(0xFFFF3B30) else Color.White
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.setWallpaper(item.drawableResName)
                                viewModel.vibrateShort(40)
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đặt làm hình nền", fontSize = 12.sp)
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = {
                            viewModel.deleteGalleryItem(item)
                            selectedItem = null
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF3B30)
                            )
                        }
                    }
                }
            }
        }
    }

    // Photo Details Dialog (EXIF & Galaxy Specs)
    if (showDetailsDialog && selectedItem != null) {
        val item = selectedItem!!
        val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Chi tiết ảnh Galaxy") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tên tệp: ${item.title}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Cảm biến: ${item.megapixels}", fontSize = 13.sp)
                    Text("Khẩu độ: ${item.aperture}", fontSize = 13.sp)
                    Text("Thời gian chụp: ${dateFmt.format(Date(item.timestamp))}", fontSize = 13.sp)
                    Text("Vị trí: Hà Nội, Việt Nam", fontSize = 13.sp)
                    Text("Thiết bị: Samsung Galaxy S24 Ultra", fontSize = 13.sp, color = SamsungBlue)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}
