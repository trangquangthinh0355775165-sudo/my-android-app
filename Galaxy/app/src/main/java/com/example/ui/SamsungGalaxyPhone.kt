package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.system.*
import com.example.ui.apps.*
import com.example.ui.components.*

@Composable
fun SamsungGalaxyPhone(
    viewModel: GalaxyViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.systemState.collectAsState()

    SamsungPhoneShell(
        viewModel = viewModel,
        state = state,
        modifier = modifier
    ) {
        // Handle Screen State (AOD, Lockscreen, Unlocked)
        when (state.screenState) {
            PhoneScreenState.AOD -> {
                SamsungAlwaysOnDisplay(
                    viewModel = viewModel,
                    state = state
                )
            }
            PhoneScreenState.LOCK_SCREEN -> {
                SamsungLockScreen(
                    viewModel = viewModel,
                    state = state
                )
            }
            PhoneScreenState.UNLOCKED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    // Main Screen Area with Status Bar and Navigation Bar
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top Status Bar (Hidden in Camera mode for immersive viewfinder)
                        if (state.currentApp != GalaxyAppType.CAMERA) {
                            SamsungStatusBar(
                                state = state,
                                isDarkContent = !state.isDarkMode && state.currentApp != GalaxyAppType.HOME,
                                onStatusClick = { viewModel.toggleNotificationShade(true) }
                            )
                        }

                        // App Viewport
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            when (state.currentApp) {
                                GalaxyAppType.HOME -> HomeScreen(viewModel = viewModel, state = state)
                                GalaxyAppType.PHONE -> PhoneApp(viewModel = viewModel, state = state)
                                GalaxyAppType.MESSAGES -> MessagesApp(viewModel = viewModel, state = state)
                                GalaxyAppType.CAMERA -> CameraApp(viewModel = viewModel, state = state)
                                GalaxyAppType.GALLERY -> GalleryApp(viewModel = viewModel, state = state)
                                GalaxyAppType.NOTES -> NotesApp(viewModel = viewModel, state = state)
                                GalaxyAppType.CLOCK -> ClockApp(viewModel = viewModel, state = state)
                                GalaxyAppType.CALCULATOR -> CalculatorApp(viewModel = viewModel, state = state)
                                GalaxyAppType.SETTINGS -> SettingsApp(viewModel = viewModel, state = state)
                                GalaxyAppType.WEATHER -> WeatherApp(viewModel = viewModel, state = state)
                                GalaxyAppType.RECORDER -> VoiceRecorderApp(viewModel = viewModel, state = state)
                                GalaxyAppType.MUSIC -> MusicApp(viewModel = viewModel, state = state)
                                GalaxyAppType.INTERNET -> InternetApp(viewModel = viewModel, state = state)
                                GalaxyAppType.GALAXY_STORE -> GalaxyStoreApp(viewModel = viewModel, state = state)
                                GalaxyAppType.PLAY_STORE -> PlayStoreApp(viewModel = viewModel, state = state)
                            }
                        }

                        // Bottom Navigation Bar
                        SamsungNavigationBar(
                            navigationType = state.navigationType,
                            isDarkContent = !state.isDarkMode && state.currentApp != GalaxyAppType.HOME && state.currentApp != GalaxyAppType.CAMERA,
                            onRecentsClick = { viewModel.openRecentApps() },
                            onHomeClick = { viewModel.goHome() },
                            onBackClick = { viewModel.goBack() }
                        )
                    }

                    // Overlays: Recent Apps Multi-Tasking Carousel
                    AnimatedVisibility(
                        visible = state.isRecentAppsOpen,
                        enter = fadeIn() + scaleIn(initialScale = 0.92f),
                        exit = fadeOut() + scaleOut(targetScale = 0.92f)
                    ) {
                        SamsungRecentApps(viewModel = viewModel, state = state)
                    }

                    // Overlays: Quick Settings Shade
                    AnimatedVisibility(
                        visible = state.isQuickSettingsOpen,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        SamsungQuickSettings(viewModel = viewModel, state = state)
                    }

                    // Overlays: Notification Center
                    AnimatedVisibility(
                        visible = state.isNotificationShadeOpen,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        SamsungNotificationShade(viewModel = viewModel, state = state)
                    }

                    // Overlays: Samsung Edge Panel
                    SamsungEdgePanel(
                        isOpen = state.isEdgePanelOpen,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
