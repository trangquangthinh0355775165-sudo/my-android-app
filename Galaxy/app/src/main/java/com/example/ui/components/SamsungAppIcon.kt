package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType

@Composable
fun getAppIconDetails(app: GalaxyAppType): Triple<ImageVector, List<Color>, String> {
    return when (app) {
        GalaxyAppType.HOME -> Triple(
            Icons.Rounded.Home,
            listOf(Color(0xFF2979FF), Color(0xFF1565C0)),
            "Trang chủ"
        )
        GalaxyAppType.PHONE -> Triple(
            Icons.Rounded.Phone,
            listOf(Color(0xFF2ECC71), Color(0xFF27AE60)),
            "Điện thoại"
        )
        GalaxyAppType.MESSAGES -> Triple(
            Icons.Rounded.Message,
            listOf(Color(0xFF3498DB), Color(0xFF2980B9)),
            "Tin nhắn"
        )
        GalaxyAppType.CAMERA -> Triple(
            Icons.Rounded.PhotoCamera,
            listOf(Color(0xFFE74C3C), Color(0xFFC0392B)),
            "Máy ảnh"
        )
        GalaxyAppType.GALLERY -> Triple(
            Icons.Rounded.PhotoLibrary,
            listOf(Color(0xFFE67E22), Color(0xFFD35400)),
            "Bộ sưu tập"
        )
        GalaxyAppType.NOTES -> Triple(
            Icons.Rounded.EditNote,
            listOf(Color(0xFFF39C12), Color(0xFFE67E22)),
            "Notes"
        )
        GalaxyAppType.CLOCK -> Triple(
            Icons.Rounded.AccessTime,
            listOf(Color(0xFF16A085), Color(0xFF1ABC9C)),
            "Đồng hồ"
        )
        GalaxyAppType.CALCULATOR -> Triple(
            Icons.Rounded.Calculate,
            listOf(Color(0xFF27AE60), Color(0xFF1E824C)),
            "Máy tính"
        )
        GalaxyAppType.SETTINGS -> Triple(
            Icons.Rounded.Settings,
            listOf(Color(0xFF7F8C8D), Color(0xFF34495E)),
            "Cài đặt"
        )
        GalaxyAppType.WEATHER -> Triple(
            Icons.Rounded.WbSunny,
            listOf(Color(0xFF3498DB), Color(0xFF00BCD4)),
            "Thời tiết"
        )
        GalaxyAppType.RECORDER -> Triple(
            Icons.Rounded.Mic,
            listOf(Color(0xFFC0392B), Color(0xFF962D22)),
            "Ghi âm"
        )
        GalaxyAppType.MUSIC -> Triple(
            Icons.Rounded.MusicNote,
            listOf(Color(0xFFE91E63), Color(0xFFC2185B)),
            "Music"
        )
        GalaxyAppType.INTERNET -> Triple(
            Icons.Rounded.Language,
            listOf(Color(0xFF1976D2), Color(0xFF0D47A1)),
            "Internet"
        )
        GalaxyAppType.GALAXY_STORE -> Triple(
            Icons.Rounded.ShoppingBag,
            listOf(Color(0xFF8E44AD), Color(0xFF6C3483)),
            "Store"
        )
        GalaxyAppType.PLAY_STORE -> Triple(
            Icons.Rounded.PlayArrow,
            listOf(Color(0xFF00C9FF), Color(0xFF92FE9D)),
            "CH Play"
        )
    }
}

@Composable
fun SamsungAppIcon(
    app: GalaxyAppType,
    modifier: Modifier = Modifier,
    iconSize: Dp = 60.dp,
    showLabel: Boolean = true,
    labelColor: Color = Color.White,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val (icon, gradientColors, defaultLabel) = getAppIconDetails(app)
    val label = app.appName.ifEmpty { defaultLabel }

    Column(
        modifier = modifier
            .testTag("app_icon_${app.name.lowercase()}")
            .width(iconSize + 20.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(iconSize * 0.28f))
                .clip(RoundedCornerShape(iconSize * 0.28f))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(iconSize * 0.55f)
            )

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(18.dp)
                        .background(Color(0xFFFF3B30), shape = RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = labelColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
