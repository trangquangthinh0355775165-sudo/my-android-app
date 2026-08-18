package com.example.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.system.SystemNotification
import com.example.ui.theme.SamsungBlue

@Composable
fun SamsungNotificationShade(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("samsung_notification_shade")
            .fillMaxSize()
            .background(Color(0xEE121418))
            .padding(top = 40.dp, start = 14.dp, end = 14.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông báo",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.notifications.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearAllNotifications() },
                            colors = ButtonDefaults.textButtonColors(contentColor = SamsungBlue)
                        ) {
                            Text("Xoá tất cả", fontSize = 13.sp)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.toggleQuickSettings(true) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Quick Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleNotificationShade(false) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
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

            // Notifications List
            if (state.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsNone,
                            contentDescription = "No notifications",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Không có thông báo mới",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.notifications, key = { it.id }) { notif ->
                        NotificationCard(
                            notification = notif,
                            onCardClick = {
                                viewModel.openApp(notif.app)
                                viewModel.dismissNotification(notif.id)
                            },
                            onDismiss = { viewModel.dismissNotification(notif.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: SystemNotification,
    onCardClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val (appIcon, gradientColors, _) = getAppIconDetails(notification.app)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF23262E))
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(gradientColors.first()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = appIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = notification.app.appName,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )

                    Text(
                        text = notification.timeAgo,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = notification.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = notification.message,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (notification.quickActionText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = notification.quickActionText, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
