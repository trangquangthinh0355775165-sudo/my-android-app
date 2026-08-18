package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.system.SoundMode
import com.example.ui.theme.SamsungBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamsungQuickSettings(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val sdfDate = remember { SimpleDateFormat("EEEE, d 'tháng' M", Locale("vi", "VN")) }
    val sdfTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val currentDate = remember { sdfDate.format(Date()) }
    val currentTime = remember { sdfTime.format(Date()) }

    Box(
        modifier = modifier
            .testTag("samsung_quick_settings_panel")
            .fillMaxSize()
            .background(Color(0xE6101216))
            .padding(top = 36.dp, start = 14.dp, end = 14.dp, bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header: Time, Date & Quick System Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentTime,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDate,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.powerButtonPressed() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openApp(GalaxyAppType.SETTINGS) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleQuickSettings(false) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Big Connectivity Pills (Wi-Fi & Bluetooth)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wi-Fi Pill
                QuickBigPill(
                    icon = Icons.Rounded.Wifi,
                    title = "Wi-Fi",
                    subtitle = if (state.isWifiOn) state.wifiNetworkName else "Đã tắt",
                    isActive = state.isWifiOn,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleWifi() }
                )

                // Bluetooth Pill
                QuickBigPill(
                    icon = Icons.Rounded.Bluetooth,
                    title = "Bluetooth",
                    subtitle = if (state.isBluetoothOn) "Galaxy Buds3 Pro" else "Đã tắt",
                    isActive = state.isBluetoothOn,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleBluetooth() }
                )
            }

            // 3x4 Quick Settings Tiles Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val (soundIcon, soundLabel) = when (state.soundMode) {
                            SoundMode.SOUND -> Icons.Rounded.VolumeUp to "Âm thanh"
                            SoundMode.VIBRATE -> Icons.Rounded.Vibration to "Rung"
                            SoundMode.MUTE -> Icons.Rounded.VolumeOff to "Tắt tiếng"
                        }
                        QuickToggleTile(
                            icon = soundIcon,
                            label = soundLabel,
                            isActive = state.soundMode != SoundMode.MUTE,
                            onClick = { viewModel.cycleSoundMode() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.ScreenRotation,
                            label = if (state.isAutoRotateOn) "Tự xoay" else "Dọc",
                            isActive = state.isAutoRotateOn,
                            onClick = { viewModel.toggleAutoRotate() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.FlashlightOn,
                            label = "Đèn pin",
                            isActive = state.isFlashlightOn,
                            onClick = { viewModel.toggleFlashlight() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.SignalCellularAlt,
                            label = "Dữ liệu di động",
                            isActive = state.isMobileDataOn,
                            onClick = { viewModel.toggleMobileData() }
                        )
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickToggleTile(
                            icon = Icons.Rounded.DarkMode,
                            label = "Chế độ tối",
                            isActive = state.isDarkMode,
                            onClick = { viewModel.toggleDarkMode() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.RemoveRedEye,
                            label = "Bảo vệ mắt",
                            isActive = state.isEyeComfortOn,
                            onClick = { viewModel.toggleEyeComfort() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.BatterySaver,
                            label = "Tiết kiệm pin",
                            isActive = state.isPowerSavingOn,
                            onClick = { viewModel.togglePowerSaving() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.AirplanemodeActive,
                            label = "Máy bay",
                            isActive = state.isAirplaneModeOn,
                            onClick = { viewModel.toggleAirplaneMode() }
                        )
                    }

                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickToggleTile(
                            icon = Icons.Rounded.WifiTethering,
                            label = "Điểm phát sóng",
                            isActive = state.isHotspotOn,
                            onClick = { viewModel.toggleHotspot() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.Draw,
                            label = "Bút S-Pen",
                            isActive = state.isSPenExtracted,
                            onClick = { viewModel.toggleSPen() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.StayCurrentPortrait,
                            label = if (state.isPhoneFrameEnabled) "Viền S24" else "Toàn màn hình",
                            isActive = state.isPhoneFrameEnabled,
                            onClick = { viewModel.togglePhoneFrame() }
                        )

                        QuickToggleTile(
                            icon = Icons.Rounded.DoNotDisturb,
                            label = "Không làm phiền",
                            isActive = false,
                            onClick = {}
                        )
                    }
                }
            }

            // Brightness Slider Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WbSunny,
                            contentDescription = "Brightness",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Slider(
                            value = state.brightnessLevel,
                            onValueChange = { viewModel.setBrightness(it) },
                            valueRange = 0.1f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = SamsungBlue,
                                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${(state.brightnessLevel * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Smart View & Device Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DevicesOther,
                            contentDescription = "Device Control",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Điều khiển thiết bị",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Cast,
                            contentDescription = "Smart View",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart View",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickBigPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) SamsungBlue else Color(0x33FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isActive) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickToggleTile(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(68.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) SamsungBlue else Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
