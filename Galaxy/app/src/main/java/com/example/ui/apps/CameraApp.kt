package com.example.ui.apps

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.SamsungBlue

@Composable
fun CameraApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf("HÌNH ẢNH") }
    var selectedZoom by remember { mutableStateOf("1x") }
    var isFlashOn by remember { mutableStateOf(false) }
    var isTimerActive by remember { mutableStateOf(false) }
    var isFrontFacing by remember { mutableStateOf(false) }
    var isRecordingVideo by remember { mutableStateOf(false) }
    var captureTriggered by remember { mutableStateOf(false) }

    // Shutter flash animation
    val shutterAlpha by animateFloatAsState(
        targetValue = if (captureTriggered) 0.8f else 0f,
        animationSpec = tween(durationMillis = 150),
        finishedListener = { captureTriggered = false },
        label = "shutter_flash"
    )

    Box(
        modifier = modifier
            .testTag("samsung_camera_app")
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Viewfinder Preview (City or Nature or Selfie)
        val previewDrawable = when {
            selectedZoom.contains("100") || selectedZoom.contains("10") -> R.drawable.img_gallery_city
            else -> R.drawable.img_gallery_nature
        }

        Image(
            painter = painterResource(id = previewDrawable),
            contentDescription = "Camera Viewfinder",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 50.dp)
                .clip(RoundedCornerShape(18.dp))
        )

        // Shutter White Flash overlay
        if (shutterAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = shutterAlpha))
            )
        }

        // Viewfinder Grid & Sensor Info HUD
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp, horizontal = 16.dp)
        ) {
            // Sensor watermark
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (selectedZoom == "1x") "200MP Ultra" else "50MP Space Zoom",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Focus reticle in center
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            )
        }

        // Top Camera Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isFlashOn = !isFlashOn }) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color(0xFFFFD54F) else Color.White
                )
            }

            IconButton(onClick = { isTimerActive = !isTimerActive }) {
                Icon(
                    imageVector = if (isTimerActive) Icons.Rounded.Timer else Icons.Rounded.TimerOff,
                    contentDescription = "Timer",
                    tint = if (isTimerActive) SamsungBlue else Color.White
                )
            }

            Text(
                text = "3:4",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }

            IconButton(onClick = { viewModel.openApp(GalaxyAppType.SETTINGS) }) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Camera Settings",
                    tint = Color.White
                )
            }
        }

        // Bottom Camera Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(bottom = 12.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Zoom Selector Buttons (.6, 1x, 3x, 5x, 10x, 100x)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(".6", "1x", "3x", "5x", "10x", "100x").forEach { zoom ->
                    val isSelected = selectedZoom == zoom
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFFFD54F) else Color.Transparent)
                            .clickable { selectedZoom = zoom },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = zoom,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Mode Selector Bar (CHÂN DUNG, HÌNH ẢNH, VIDEO, BAN ĐÊM, KHÁC)
            val modes = listOf("CHÂN DUNG", "HÌNH ẢNH", "VIDEO", "BAN ĐÊM")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                modes.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Text(
                        text = mode,
                        color = if (isSelected) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable {
                                selectedMode = mode
                                isRecordingVideo = false
                            }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    )
                }
            }

            // Shutter Button & Flank Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail Preview Shortcut
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.openApp(GalaxyAppType.GALLERY) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_gallery_city),
                        contentDescription = "Gallery",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Big Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("camera_shutter_button")
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clickable {
                            if (selectedMode == "VIDEO") {
                                isRecordingVideo = !isRecordingVideo
                                viewModel.vibrateShort(40)
                            } else {
                                captureTriggered = true
                                viewModel.capturePhoto(selectedZoom, selectedMode)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(if (isRecordingVideo) RoundedCornerShape(8.dp) else CircleShape)
                            .background(if (selectedMode == "VIDEO") Color(0xFFFF3B30) else Color.White)
                    )
                }

                // Front/Rear Camera Flip Button
                IconButton(
                    onClick = {
                        viewModel.vibrateShort(25)
                        isFrontFacing = !isFrontFacing
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FlipCameraAndroid,
                        contentDescription = "Flip Camera",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
