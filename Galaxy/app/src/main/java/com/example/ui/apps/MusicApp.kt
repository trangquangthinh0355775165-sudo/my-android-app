package com.example.ui.apps

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*

private data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String,
    val coverRes: Int
)

@Composable
fun MusicApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val songs = listOf(
        Song(1, "Over the Horizon (2024)", "Samsung Galaxy & SUGA", "03:45", R.drawable.img_samsung_wallpaper_1),
        Song(2, "Galaxy Symphony", "Vienna Philharmonic", "04:12", R.drawable.img_samsung_wallpaper_2),
        Song(3, "Epic Nightscape", "Chill Electronic", "02:58", R.drawable.img_gallery_city),
        Song(4, "Morning Breeze", "Acoustic Sunset", "03:20", R.drawable.img_gallery_nature)
    )

    var currentSong by remember { mutableStateOf(songs[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.35f) }

    Scaffold(
        modifier = modifier
            .testTag("samsung_music_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Samsung Music",
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Music",
                    tint = if (state.isDarkMode) Color.White else Color.Black
                )
            }

            // Big Album Art & Active Track Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = currentSong.coverRes),
                        contentDescription = currentSong.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentSong.title,
                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentSong.artist,
                        color = SamsungBlue,
                        fontSize = 13.sp
                    )
                }

                // Progress Bar & Durations
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = SamsungBlue,
                            activeTrackColor = SamsungBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("01:15", color = Color.Gray, fontSize = 11.sp)
                        Text(currentSong.duration, color = Color.Gray, fontSize = 11.sp)
                    }
                }

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = Color.Gray)
                    }

                    IconButton(onClick = {
                        val idx = songs.indexOf(currentSong)
                        currentSong = if (idx > 0) songs[idx - 1] else songs.last()
                    }) {
                        Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = if (state.isDarkMode) Color.White else Color.Black, modifier = Modifier.size(32.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SamsungBlue)
                            .clickable {
                                isPlaying = !isPlaying
                                viewModel.vibrateShort(25)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = {
                        val idx = songs.indexOf(currentSong)
                        currentSong = if (idx < songs.size - 1) songs[idx + 1] else songs.first()
                    }) {
                        Icon(Icons.Rounded.SkipNext, contentDescription = "Next", tint = if (state.isDarkMode) Color.White else Color.Black, modifier = Modifier.size(32.dp))
                    }

                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Repeat, contentDescription = "Repeat", tint = Color.Gray)
                    }
                }
            }

            // Playlist Queue
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(songs) { song ->
                    val isCurrent = song.id == currentSong.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                currentSong = song
                                isPlaying = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) SamsungBlue.copy(alpha = 0.15f) else (if (state.isDarkMode) OneUIDarkCard else OneUILightCard)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = song.title,
                                    color = if (isCurrent) SamsungBlue else (if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(song.artist, color = Color.Gray, fontSize = 11.sp)
                            }
                            Text(song.duration, color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
