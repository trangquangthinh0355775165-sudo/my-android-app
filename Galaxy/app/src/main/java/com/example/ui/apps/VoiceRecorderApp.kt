package com.example.ui.apps

import androidx.compose.animation.core.*
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
import com.example.data.model.VoiceMemoEntity
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun VoiceRecorderApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val voiceMemos by viewModel.voiceMemos.collectAsState()
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }
    var waveHeights by remember { mutableStateOf(List(24) { 10f }) }
    var playingMemoId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(100)
                waveHeights = List(24) { Random.nextFloat() * 60f + 10f }
                if (System.currentTimeMillis() % 1000 < 120) {
                    recordingSeconds += 1
                }
            }
        }
    }

    val recMin = recordingSeconds / 60
    val recSec = recordingSeconds % 60
    val recFormatted = String.format("%02d:%02d", recMin, recSec)

    Scaffold(
        modifier = modifier
            .testTag("samsung_voice_recorder_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ghi âm Samsung",
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Chất lượng cao 24-bit",
                    color = SamsungRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Audio Waveform & Timer Center Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Text(
                    text = recFormatted,
                    color = if (isRecording) SamsungRed else (if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Light
                )

                // Simulated Dynamic Waveform Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (state.isDarkMode) OneUIDarkCard else OneUILightCard)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    waveHeights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(if (isRecording) h.dp else 8.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isRecording) SamsungRed else Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            // Record History List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Danh sách bản ghi (${voiceMemos.size})",
                    color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(voiceMemos, key = { it.id }) { memo ->
                        val isPlaying = playingMemoId == memo.id

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            playingMemoId = if (isPlaying) null else memo.id
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (isPlaying) SamsungBlue else Color.Gray.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = "Play",
                                            tint = if (isPlaying) Color.White else (if (state.isDarkMode) Color.White else Color.Black),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = memo.title,
                                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${memo.durationFormatted} • ${dateFmt.format(Date(memo.timestamp))}",
                                            color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Big Record / Stop Button
            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(SamsungRed)
                    .clickable {
                        viewModel.vibrateShort(45)
                        if (isRecording) {
                            // Stop & Save
                            viewModel.addVoiceMemo(
                                title = "Ghi âm ${voiceMemos.size + 1}",
                                durationSeconds = recordingSeconds,
                                durationFormatted = recFormatted
                            )
                            isRecording = false
                        } else {
                            isRecording = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}
