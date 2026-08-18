package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SamsungAlwaysOnDisplay(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFmt = SimpleDateFormat("EEE, d 'thg' M", Locale("vi", "VN"))
        while (true) {
            val now = Date()
            currentTime = timeFmt.format(now)
            currentDate = dateFmt.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .testTag("samsung_aod_screen")
            .fillMaxSize()
            .background(Color.Black)
            .clickable { viewModel.wakeToLockScreen() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // AOD Digital Clock
            Text(
                text = currentTime.ifEmpty { "12:45" },
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 58.sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp
            )

            // AOD Date
            Text(
                text = currentDate.ifEmpty { "Th 2, 18 thg 8" },
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            // Battery Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.BatteryFull,
                    contentDescription = "Battery",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${state.batteryLevel}%",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Glyphs
            if (state.notifications.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.notifications.take(4).forEach { notif ->
                        val (icon, _, _) = getAppIconDetails(notif.app)
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Hint
            Text(
                text = "Chạm 2 lần để đánh thức",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 12.sp
            )
        }
    }
}
