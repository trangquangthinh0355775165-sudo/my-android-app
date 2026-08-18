package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.system.NavigationType
import com.example.system.SoundMode
import com.example.ui.theme.*

@Composable
fun SettingsApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isCleaningRam by remember { mutableStateOf(false) }
    var ramCleanedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier
            .testTag("samsung_settings_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // One UI Large Header Title
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Text(
                    text = "Cài đặt",
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "One UI 6.1 • Galaxy AI",
                    color = SamsungBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Samsung Galaxy Account Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SamsungBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Samsung Account",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Samsung Account",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "galaxy.user@samsung.vn",
                            color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Galaxy Cloud • Samsung Pass",
                            color = SamsungBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // SECTION 1: KẾT NỐI (Connections)
            SettingsGroup(title = "Kết nối", isDarkMode = state.isDarkMode) {
                SettingsSwitchRow(
                    icon = Icons.Rounded.Wifi,
                    iconBg = Color(0xFF0381FE),
                    title = "Wi-Fi",
                    subtitle = if (state.isWifiOn) state.wifiNetworkName else "Đã tắt",
                    isChecked = state.isWifiOn,
                    onCheckedChange = { viewModel.toggleWifi() },
                    isDarkMode = state.isDarkMode
                )
                Divider(color = if (state.isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f))
                SettingsSwitchRow(
                    icon = Icons.Rounded.Bluetooth,
                    iconBg = Color(0xFF007AFF),
                    title = "Bluetooth",
                    subtitle = if (state.isBluetoothOn) "Galaxy Buds3 Pro" else "Đã tắt",
                    isChecked = state.isBluetoothOn,
                    onCheckedChange = { viewModel.toggleBluetooth() },
                    isDarkMode = state.isDarkMode
                )
                Divider(color = if (state.isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f))
                SettingsSwitchRow(
                    icon = Icons.Rounded.AirplanemodeActive,
                    iconBg = Color(0xFFFF9500),
                    title = "Chế độ máy bay",
                    subtitle = if (state.isAirplaneModeOn) "Đang bật" else "Đã tắt",
                    isChecked = state.isAirplaneModeOn,
                    onCheckedChange = { viewModel.toggleAirplaneMode() },
                    isDarkMode = state.isDarkMode
                )
            }

            // SECTION 2: ÂM THANH VÀ RUNG
            SettingsGroup(title = "Âm thanh và rung", isDarkMode = state.isDarkMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val modeText = when (state.soundMode) {
                        SoundMode.SOUND -> "Âm thanh"
                        SoundMode.VIBRATE -> "Rung"
                        SoundMode.MUTE -> "Tắt tiếng"
                    }
                    Column {
                        Text(
                            text = "Chế độ âm thanh",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = modeText,
                            color = SamsungBlue,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.cycleSoundMode() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Chuyển đổi", fontSize = 11.sp)
                    }
                }
            }

            // SECTION 3: MÀN HÌNH (Display)
            SettingsGroup(title = "Màn hình", isDarkMode = state.isDarkMode) {
                SettingsSwitchRow(
                    icon = Icons.Rounded.DarkMode,
                    iconBg = Color(0xFF5856D6),
                    title = "Chế độ tối (Dark Mode)",
                    subtitle = if (state.isDarkMode) "Bật (Tiết kiệm pin OLED)" else "Tắt (Giao diện sáng)",
                    isChecked = state.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    isDarkMode = state.isDarkMode
                )
                Divider(color = if (state.isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f))
                SettingsSwitchRow(
                    icon = Icons.Rounded.StayCurrentPortrait,
                    iconBg = Color(0xFF34C759),
                    title = "Khung viền Galaxy S24 Ultra",
                    subtitle = if (state.isPhoneFrameEnabled) "Viền Titanium & Phím bấm vật lý" else "Chế độ Toàn màn hình",
                    isChecked = state.isPhoneFrameEnabled,
                    onCheckedChange = { viewModel.togglePhoneFrame() },
                    isDarkMode = state.isDarkMode
                )
                Divider(color = if (state.isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f))
                // Brightness Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Độ sáng: ${(state.brightnessLevel * 100).toInt()}%",
                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = state.brightnessLevel,
                        onValueChange = { viewModel.setBrightness(it) },
                        valueRange = 0.1f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = SamsungBlue,
                            activeTrackColor = SamsungBlue
                        )
                    )
                }
            }

            // SECTION 4: HÌNH NỀN VÀ PHONG CÁCH
            SettingsGroup(title = "Hình nền và phong cách", isDarkMode = state.isDarkMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Chọn hình nền Samsung One UI:",
                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Wallpaper 1
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setWallpaper("img_samsung_wallpaper_1") },
                            shape = RoundedCornerShape(14.dp),
                            border = if (state.selectedWallpaperRes == "img_samsung_wallpaper_1") ButtonDefaults.outlinedButtonBorder else null
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_samsung_wallpaper_1),
                                contentDescription = "Wallpaper 1",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Wallpaper 2
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setWallpaper("img_samsung_wallpaper_2") },
                            shape = RoundedCornerShape(14.dp),
                            border = if (state.selectedWallpaperRes == "img_samsung_wallpaper_2") ButtonDefaults.outlinedButtonBorder else null
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_samsung_wallpaper_2),
                                contentDescription = "Wallpaper 2",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // SECTION 5: THANH ĐIỀU HƯỚNG (Navigation Bar)
            SettingsGroup(title = "Thanh điều hướng", isDarkMode = state.isDarkMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kiểu điều hướng",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (state.navigationType == NavigationType.BUTTONS_3) "3 Phím (Gần đây, Home, Quay lại)" else "Thanh cử chỉ vuốt",
                            color = SamsungBlue,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val next = if (state.navigationType == NavigationType.BUTTONS_3) NavigationType.GESTURES else NavigationType.BUTTONS_3
                            viewModel.setNavigationType(next)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Đổi kiểu", fontSize = 11.sp)
                    }
                }
            }

            // SECTION 6: CHĂM SÓC THIẾT BỊ (Device Care)
            SettingsGroup(title = "Chăm sóc thiết bị & Bộ nhớ", isDarkMode = state.isDarkMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bộ nhớ RAM: 12 GB LPDDR5X",
                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Bộ nhớ trong: 512 GB UFS 4.0",
                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                isCleaningRam = true
                                viewModel.cleanMemory {
                                    isCleaningRam = false
                                    ramCleanedMessage = "Đã dọn dẹp 3.2 GB RAM thành công! ✨"
                                }
                            },
                            enabled = !isCleaningRam,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                        ) {
                            Text(if (isCleaningRam) "Đang dọn..." else "Dọn ngay", fontSize = 12.sp)
                        }
                    }

                    if (ramCleanedMessage != null) {
                        Text(
                            text = ramCleanedMessage!!,
                            color = Color(0xFF2ECC71),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // SECTION 7: THÔNG TIN ĐIỆN THOẠI (About Phone)
            SettingsGroup(title = "Thông tin điện thoại", isDarkMode = state.isDarkMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow("Tên thiết bị", "Galaxy S24 Ultra", isDarkMode = state.isDarkMode)
                    InfoRow("Số kiểu máy", "SM-S928B/DS", isDarkMode = state.isDarkMode)
                    InfoRow("Phiên bản One UI", "6.1 (Android 15)", isDarkMode = state.isDarkMode)
                    InfoRow("Vi xử lý", "Snapdragon 8 Gen 3 for Galaxy", isDarkMode = state.isDarkMode)
                    InfoRow("Dung lượng Pin", "5000 mAh (Sạc nhanh 45W)", isDarkMode = state.isDarkMode)
                    InfoRow("Bút cảm ứng", "S-Pen tích hợp Bluetooth BLE", isDarkMode = state.isDarkMode)
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    isDarkMode: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = SamsungBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) OneUIDarkCard else OneUILightCard
            ),
            content = content
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = if (isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = if (isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SamsungBlue
            )
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, isDarkMode: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = if (isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
