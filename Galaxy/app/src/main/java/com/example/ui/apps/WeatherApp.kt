package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel

@Composable
fun WeatherApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val hourlyData = listOf(
        Triple("Bây giờ", Icons.Rounded.WbSunny, "29°"),
        Triple("13:00", Icons.Rounded.WbSunny, "31°"),
        Triple("14:00", Icons.Rounded.WbSunny, "32°"),
        Triple("15:00", Icons.Rounded.WbCloudy, "30°"),
        Triple("16:00", Icons.Rounded.Thunderstorm, "28°"),
        Triple("17:00", Icons.Rounded.Thunderstorm, "27°"),
        Triple("18:00", Icons.Rounded.WbCloudy, "26°"),
        Triple("19:00", Icons.Rounded.NightsStay, "25°")
    )

    val dailyData = listOf(
        Triple("Hôm nay", "Nắng rực rỡ", "24° / 32°"),
        Triple("Thứ Ba", "Có mưa dông", "23° / 29°"),
        Triple("Thứ Tư", "Mây rải rác", "24° / 31°"),
        Triple("Thứ Năm", "Trời nắng", "25° / 33°"),
        Triple("Thứ Sáu", "Nhiều mây", "24° / 30°"),
        Triple("Thứ Bảy", "Nắng đẹp", "25° / 34°"),
        Triple("Chủ Nhật", "Mưa rào", "23° / 28°")
    )

    Box(
        modifier = modifier
            .testTag("samsung_weather_app")
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2980B9),
                        Color(0xFF6DD5FA),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Hà Nội, Việt Nam",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Search, contentDescription = "Search City", tint = Color.White)
                }
            }

            // Big Main Weather Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WbSunny,
                    contentDescription = "Sunny",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(72.dp)
                )

                Text(
                    text = "29°",
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraLight
                )

                Text(
                    text = "Trời nắng • Chất lượng KK tốt (AQI 32)",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Hourly Forecast Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Dự báo theo giờ",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        items(hourlyData) { (time, icon, temp) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(time, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Icon(icon, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(24.dp))
                                Text(temp, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 7-Day Forecast Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Dự báo 7 ngày tới",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    dailyData.forEach { (day, status, temp) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(day, color = Color.White, fontSize = 13.sp, modifier = Modifier.width(80.dp))
                            Text(status, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text(temp, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
