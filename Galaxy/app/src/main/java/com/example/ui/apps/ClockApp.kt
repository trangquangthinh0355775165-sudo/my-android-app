package com.example.ui.apps

import androidx.compose.animation.*
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
import com.example.data.model.AlarmEntity
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ClockApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val alarms by viewModel.alarms.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Alarm, 1: World, 2: Stopwatch, 3: Timer
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    // Stopwatch states
    var stopwatchRunning by remember { mutableStateOf(false) }
    var stopwatchMillis by remember { mutableStateOf(0L) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(stopwatchRunning) {
        if (stopwatchRunning) {
            val startTime = System.currentTimeMillis() - stopwatchMillis
            while (stopwatchRunning) {
                stopwatchMillis = System.currentTimeMillis() - startTime
                delay(30)
            }
        }
    }

    // Timer states
    var timerSecondsTotal by remember { mutableStateOf(300) } // 5 minutes default
    var timerSecondsRemaining by remember { mutableStateOf(300) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning && timerSecondsRemaining > 0) {
                delay(1000)
                timerSecondsRemaining -= 1
            }
            if (timerSecondsRemaining <= 0) {
                timerRunning = false
                viewModel.vibrateShort(100)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .testTag("samsung_clock_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        bottomBar = {
            NavigationBar(
                containerColor = if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface
            ) {
                listOf("Báo thức", "Quốc tế", "Bấm giờ", "Hẹn giờ").forEachIndexed { index, label ->
                    val icon = when (index) {
                        0 -> Icons.Rounded.Alarm
                        1 -> Icons.Rounded.Public
                        2 -> Icons.Rounded.Timer
                        else -> Icons.Rounded.HourglassTop
                    }
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp) },
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // ALARM TAB
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Báo thức",
                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { showAddAlarmDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SamsungBlue, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Add Alarm",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(alarms, key = { it.id }) { alarm ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${alarm.hour}:${if (alarm.minute < 10) "0${alarm.minute}" else alarm.minute}",
                                                color = if (alarm.isEnabled) (if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary) else Color.Gray,
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Light
                                            )
                                            Text(
                                                text = "${alarm.label} • ${alarm.repeatDays}",
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Switch(
                                            checked = alarm.isEnabled,
                                            onCheckedChange = { viewModel.toggleAlarm(alarm) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = SamsungBlue
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // WORLD CLOCK TAB
                    val cities = listOf(
                        Triple("Hà Nội", "+0 giờ • Hôm nay", "12:45"),
                        Triple("Tokyo", "+2 giờ • Hôm nay", "14:45"),
                        Triple("London", "-6 giờ • Hôm nay", "06:45"),
                        Triple("New York", "-11 giờ • Hôm nay", "01:45"),
                        Triple("Sydney", "+3 giờ • Hôm nay", "15:45")
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Giờ quốc tế",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(cities) { (city, diff, time) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = city,
                                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = diff,
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Text(
                                            text = time,
                                            color = SamsungBlue,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Light
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // STOPWATCH TAB
                    val min = (stopwatchMillis / 1000) / 60
                    val sec = (stopwatchMillis / 1000) % 60
                    val millis = (stopwatchMillis % 1000) / 10
                    val formatted = String.format("%02d:%02d.%02d", min, sec, millis)

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bấm giờ",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        // Timer Display
                        Text(
                            text = formatted,
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 2.sp
                        )

                        // Lap list
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(laps.reversed()) { lapTime ->
                                val lMin = (lapTime / 1000) / 60
                                val lSec = (lapTime / 1000) % 60
                                val lMil = (lapTime % 1000) / 10
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Vòng", color = Color.Gray, fontSize = 13.sp)
                                    Text(String.format("%02d:%02d.%02d", lMin, lSec, lMil), color = SamsungBlue, fontSize = 13.sp)
                                }
                            }
                        }

                        // Buttons (Lap, Start/Stop, Reset)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (stopwatchRunning) {
                                        laps.add(stopwatchMillis)
                                    } else {
                                        stopwatchMillis = 0L
                                        laps.clear()
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.3f)),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Text(if (stopwatchRunning) "Vòng" else "Đặt lại", fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    stopwatchRunning = !stopwatchRunning
                                    viewModel.vibrateShort(30)
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (stopwatchRunning) Color(0xFFFF3B30) else Color(0xFF2ECC71)
                                ),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Text(if (stopwatchRunning) "Dừng" else "Bắt đầu", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                3 -> {
                    // TIMER TAB
                    val tMin = timerSecondsRemaining / 60
                    val tSec = timerSecondsRemaining % 60
                    val tFormatted = String.format("%02d:%02d", tMin, tSec)

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hẹn giờ",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        // Big Countdown Circle
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(CircleShape)
                                .background(if (state.isDarkMode) OneUIDarkCard else OneUILightCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tFormatted,
                                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Light
                                )
                                Text(
                                    text = "Đang đếm ngược",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Quick presets (1m, 3m, 5m, 10m)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            listOf(60 to "1 phút", 180 to "3 phút", 300 to "5 phút", 600 to "10 phút").forEach { (sec, label) ->
                                AssistChip(
                                    onClick = {
                                        timerSecondsTotal = sec
                                        timerSecondsRemaining = sec
                                        timerRunning = false
                                    },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }

                        // Timer Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    timerRunning = false
                                    timerSecondsRemaining = timerSecondsTotal
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.3f)),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Text("Huỷ", fontSize = 12.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    timerRunning = !timerRunning
                                    viewModel.vibrateShort(30)
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (timerRunning) Color(0xFFFF3B30) else SamsungBlue
                                ),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Text(if (timerRunning) "Tạm dừng" else "Bắt đầu", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Alarm Dialog
    if (showAddAlarmDialog) {
        var hourInput by remember { mutableStateOf("07") }
        var minuteInput by remember { mutableStateOf("30") }
        var labelInput by remember { mutableStateOf("Thức dậy") }

        AlertDialog(
            onDismissRequest = { showAddAlarmDialog = false },
            title = { Text("Thêm báo thức mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = hourInput,
                            onValueChange = { hourInput = it },
                            label = { Text("Giờ (0-23)") },
                            modifier = Modifier.weight(1f)
                        )
                        TextField(
                            value = minuteInput,
                            onValueChange = { minuteInput = it },
                            label = { Text("Phút (0-59)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TextField(
                        value = labelInput,
                        onValueChange = { labelInput = it },
                        label = { Text("Tên báo thức") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = hourInput.toIntOrNull() ?: 7
                        val m = minuteInput.toIntOrNull() ?: 30
                        viewModel.addAlarm(
                            AlarmEntity(
                                hour = h,
                                minute = m,
                                label = labelInput.ifBlank { "Báo thức" }
                            )
                        )
                        showAddAlarmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAlarmDialog = false }) {
                    Text("Huỷ")
                }
            }
        )
    }
}
