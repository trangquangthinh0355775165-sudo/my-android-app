package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.system.SoundMode
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamsungStatusBar(
    state: GalaxySystemState,
    modifier: Modifier = Modifier,
    isDarkContent: Boolean = false,
    onStatusClick: () -> Unit = {}
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            currentTime = sdf.format(Date())
            delay(1000)
        }
    }

    val contentColor = if (isDarkContent) Color(0xFF1B1C1F) else Color.White

    Row(
        modifier = modifier
            .testTag("samsung_status_bar")
            .fillMaxWidth()
            .height(34.dp)
            .clickable { onStatusClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Time & Notification icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = currentTime.ifEmpty { "12:45" },
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (state.notifications.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = contentColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
            }
            if (state.activeCall.isInCall) {
                Icon(
                    imageVector = Icons.Rounded.PhoneInTalk,
                    contentDescription = "In Call",
                    tint = Color(0xFF2ECC71),
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Center Punch-hole space (Camera cutout simulated in phone shell)
        Spacer(modifier = Modifier.weight(1f))

        // Right: System Telemetry Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (state.soundMode == SoundMode.VIBRATE) {
                Icon(
                    imageVector = Icons.Rounded.Vibration,
                    contentDescription = "Vibrate",
                    tint = contentColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
            } else if (state.soundMode == SoundMode.MUTE) {
                Icon(
                    imageVector = Icons.Rounded.VolumeOff,
                    contentDescription = "Mute",
                    tint = contentColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
            }

            if (state.isBluetoothOn) {
                Icon(
                    imageVector = Icons.Rounded.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint = contentColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
            }

            if (state.isWifiOn) {
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = "Wi-Fi",
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            if (state.isMobileDataOn && !state.isAirplaneModeOn) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "5G",
                        color = contentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.SignalCellular4Bar,
                        contentDescription = "5G Signal",
                        tint = contentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            if (state.isAirplaneModeOn) {
                Icon(
                    imageVector = Icons.Rounded.AirplanemodeActive,
                    contentDescription = "Airplane mode",
                    tint = contentColor,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Battery Percentage & Graphic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "${state.batteryLevel}%",
                    color = contentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(contentColor.copy(alpha = 0.35f))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = (state.batteryLevel / 100f).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(if (state.batteryLevel <= 15) Color(0xFFFF3B30) else contentColor)
                    )
                }
            }
        }
    }
}
