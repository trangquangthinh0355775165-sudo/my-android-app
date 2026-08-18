package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamsungLockScreen(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFmt = SimpleDateFormat("EEEE, d 'tháng' M", Locale("vi", "VN"))
        while (true) {
            val now = Date()
            currentTime = timeFmt.format(now)
            currentDate = dateFmt.format(now)
            delay(1000)
        }
    }

    // Ultrasonic Fingerprint glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "fp_ripple")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ripple_scale"
    )

    Box(
        modifier = modifier
            .testTag("samsung_lock_screen")
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) {
                        viewModel.unlockPhone()
                    }
                }
            }
    ) {
        // Wallpaper
        val wallpaperRes = when (state.selectedWallpaperRes) {
            "img_samsung_wallpaper_2" -> R.drawable.img_samsung_wallpaper_2
            else -> R.drawable.img_samsung_wallpaper_1
        }
        Image(
            painter = painterResource(id = wallpaperRes),
            contentDescription = "Lockscreen Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark dim gradient for lockscreen readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
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

        // Lockscreen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, bottom = 30.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Clock & Date & Weather Widget
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currentTime.ifEmpty { "12:45" },
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp
                )

                Text(
                    text = currentDate.ifEmpty { "Thứ Hai, 18 tháng 8" },
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = "Weather",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "29°C • Nắng nhẹ • Hà Nội",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }

            // Notification Stack (One UI Pill)
            if (state.notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.unlockPhone() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x661A1C23))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            state.notifications.take(3).forEach { notif ->
                                val (icon, gradient, _) = getAppIconDetails(notif.app)
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(gradient.first()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${state.notifications.size} thông báo",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Ultrasonic Fingerprint Sensor Target & Swipe Hint
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Glowing Fingerprint Sensor
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("fingerprint_sensor")
                        .clickable { viewModel.unlockPhone() },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ripple
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(rippleScale)
                            .clip(CircleShape)
                            .background(Color(0x330381FE))
                    )

                    // Inner Sensor Target
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x8015181E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Fingerprint,
                            contentDescription = "Fingerprint Unlock",
                            tint = Color(0xFF4DA2FF),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Text(
                    text = "Vuốt hoặc chạm vân tay để mở khoá",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Bottom Corner Shortcuts: Phone (Left) & Camera (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phone Shortcut
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("lockscreen_phone_shortcut")
                        .clip(CircleShape)
                        .background(Color(0x55000000))
                        .clickable {
                            viewModel.unlockPhone()
                            viewModel.openApp(GalaxyAppType.PHONE)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Phone,
                        contentDescription = "Phone shortcut",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Camera Shortcut
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("lockscreen_camera_shortcut")
                        .clip(CircleShape)
                        .background(Color(0x55000000))
                        .clickable {
                            viewModel.unlockPhone()
                            viewModel.openApp(GalaxyAppType.CAMERA)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoCamera,
                        contentDescription = "Camera shortcut",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
