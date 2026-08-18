package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*

@Composable
fun SamsungPhoneShell(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!state.isPhoneFrameEnabled) {
        // Fullscreen Mode without outer hardware chassis
        Box(modifier = modifier.fillMaxSize()) {
            content()

            // Floating mode toggle chip at top-left
            AssistChip(
                onClick = { viewModel.togglePhoneFrame(true) },
                label = { Text("Chế độ Khung S24 Ultra", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Smartphone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    labelColor = Color.White,
                    leadingIconContentColor = SamsungBlue
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(top = 40.dp, start = 12.dp)
                    .align(Alignment.TopStart)
            )

            // Volume HUD Slider
            AnimatedVisibility(
                visible = state.isVolumeHudVisible,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            ) {
                OneUIVolumeHud(volume = state.volumeLevel)
            }
        }
        return
    }

    // Realistic Galaxy S24 Ultra Titanium Frame Layout
    Box(
        modifier = modifier
            .testTag("samsung_s24_shell")
            .fillMaxSize()
            .background(Color(0xFF0F1115))
            .padding(vertical = 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Titanium Phone Frame Chassis
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF383A42),
                            Color(0xFF1E2026),
                            Color(0xFF2B2D35),
                            Color(0xFF1A1B20)
                        )
                    )
                )
                .border(2.dp, Color(0xFF4E515D), RoundedCornerShape(32.dp))
                .padding(4.dp)
        ) {
            // Screen Bezel (Matte Obsidian)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black)
            ) {
                // Main Interactive Display Screen
                content()

                // Center Punch Hole Camera Cutout
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 7.dp)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0A0A0E))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C2D4A))
                    )
                }

                // Volume HUD Slider
                AnimatedVisibility(
                    visible = state.isVolumeHudVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    OneUIVolumeHud(volume = state.volumeLevel)
                }
            }
        }

        // Hardware Buttons on the Right Frame Edge
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 6.dp)
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Volume Up Button
            Box(
                modifier = Modifier
                    .testTag("hw_volume_up_btn")
                    .width(6.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(Color(0xFF555866))
                    .clickable { viewModel.adjustVolume(0.1f) }
            )

            // Volume Down Button
            Box(
                modifier = Modifier
                    .testTag("hw_volume_down_btn")
                    .width(6.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(Color(0xFF555866))
                    .clickable { viewModel.adjustVolume(-0.1f) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Power / Side Key
            Box(
                modifier = Modifier
                    .testTag("hw_power_btn")
                    .width(6.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(Color(0xFF6B6E7D))
                    .clickable { viewModel.powerButtonPressed() }
            )
        }

        // S-Pen Stylus Slot & Ejector at Bottom-Left Frame
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 24.dp, y = 6.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(if (state.isSPenExtracted) SamsungBlue else Color(0xFF383A42))
                .clickable { viewModel.toggleSPen() }
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Draw,
                    contentDescription = "S-Pen Stylus",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (state.isSPenExtracted) "S-Pen (Đang dùng)" else "S-Pen",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Floating Mode Toggle Chip at Top Bar
        AssistChip(
            onClick = { viewModel.togglePhoneFrame(false) },
            label = { Text("Toàn màn hình", fontSize = 10.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Fullscreen,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color.Black.copy(alpha = 0.65f),
                labelColor = Color.White,
                leadingIconContentColor = SamsungBlue
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-2).dp, x = (-16).dp)
        )
    }
}

@Composable
private fun OneUIVolumeHud(volume: Float) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD1E2128)),
        modifier = Modifier
            .width(44.dp)
            .height(140.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = if (volume <= 0.05f) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = "Volume",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )

            // Vertical Volume Bar
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = volume.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(4.dp))
                        .background(SamsungBlue)
                )
            }

            Text(
                text = "${(volume * 100).toInt()}%",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
