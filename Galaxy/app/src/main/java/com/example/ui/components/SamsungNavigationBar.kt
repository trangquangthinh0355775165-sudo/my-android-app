package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.system.NavigationType

@Composable
fun SamsungNavigationBar(
    navigationType: NavigationType,
    isDarkContent: Boolean = false,
    modifier: Modifier = Modifier,
    onRecentsClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val navColor = if (isDarkContent) Color(0xFF1B1C1F) else Color.White

    Box(
        modifier = modifier
            .testTag("samsung_navigation_bar")
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (navigationType == NavigationType.BUTTONS_3) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recents (Samsung 3 vertical bars |||)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_recents_button")
                        .clickable(onClick = onRecentsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(navColor.copy(alpha = 0.9f))
                            )
                        }
                    }
                }

                // Home (Rounded Squircle / Circle)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_home_button")
                        .clickable(onClick = onHomeClick),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.Transparent)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                                .background(navColor.copy(alpha = 0.9f))
                        )
                    }
                }

                // Back (< Arrow)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_back_button")
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = navColor.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            // Gesture Bar (One UI navigation pill)
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(4.5.dp)
                    .testTag("nav_gesture_pill")
                    .clip(RoundedCornerShape(3.dp))
                    .background(navColor.copy(alpha = 0.7f))
                    .clickable { onHomeClick() }
            )
        }
    }
}
