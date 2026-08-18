package com.example.system

enum class GalaxyAppType(val appName: String, val packageName: String) {
    HOME("One UI Home", "com.sec.android.app.launcher"),
    PHONE("Điện thoại", "com.samsung.android.dialer"),
    MESSAGES("Tin nhắn", "com.samsung.android.messaging"),
    CAMERA("Máy ảnh", "com.sec.android.app.camera"),
    GALLERY("Bộ sưu tập", "com.sec.android.gallery3d"),
    NOTES("Samsung Notes", "com.samsung.android.app.notes"),
    CLOCK("Đồng hồ", "com.sec.android.app.clockpackage"),
    CALCULATOR("Máy tính", "com.sec.android.app.popupcalculator"),
    SETTINGS("Cài đặt", "com.android.settings"),
    WEATHER("Thời tiết", "com.sec.android.daemonapp"),
    RECORDER("Ghi âm", "com.sec.android.app.voicenote"),
    MUSIC("Samsung Music", "com.sec.android.app.music"),
    INTERNET("Samsung Internet", "com.sec.android.app.sbrowser"),
    GALAXY_STORE("Galaxy Store", "com.sec.android.app.samsungapps"),
    PLAY_STORE("Google Play", "com.android.vending")
}

enum class PhoneScreenState {
    AOD,         // Always On Display
    LOCK_SCREEN, // One UI Lockscreen
    UNLOCKED     // Active home or app
}

enum class NavigationType {
    BUTTONS_3,   // Recents |||, Home O, Back <
    GESTURES     // Gesture navigation bar pill
}

enum class SoundMode {
    SOUND,
    VIBRATE,
    MUTE
}

data class SystemNotification(
    val id: String,
    val app: GalaxyAppType,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isExpandable: Boolean = false,
    val quickActionText: String? = null
)

data class ActiveCallState(
    val isInCall: Boolean = false,
    val contactName: String = "",
    val phoneNumber: String = "",
    val isIncoming: Boolean = false,
    val callDurationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isKeypadOpen: Boolean = false
)

data class GalaxySystemState(
    val screenState: PhoneScreenState = PhoneScreenState.UNLOCKED,
    val currentApp: GalaxyAppType = GalaxyAppType.HOME,
    val runningAppHistory: List<GalaxyAppType> = listOf(GalaxyAppType.PHONE, GalaxyAppType.MESSAGES, GalaxyAppType.CAMERA, GalaxyAppType.NOTES),
    val isAppDrawerOpen: Boolean = false,
    val isRecentAppsOpen: Boolean = false,
    val isQuickSettingsOpen: Boolean = false,
    val isNotificationShadeOpen: Boolean = false,
    val isEdgePanelOpen: Boolean = false,
    val isPhoneFrameEnabled: Boolean = true, // Samsung S24 Titanium Hardware Bezel View
    val isSPenExtracted: Boolean = false,
    val isVolumeHudVisible: Boolean = false,
    val volumeLevel: Float = 0.75f,
    val brightnessLevel: Float = 0.85f,
    val isFlashlightOn: Boolean = false,
    val isWifiOn: Boolean = true,
    val wifiNetworkName: String = "Galaxy-Wi-Fi 6E (5GHz)",
    val isBluetoothOn: Boolean = true,
    val isMobileDataOn: Boolean = true,
    val soundMode: SoundMode = SoundMode.SOUND,
    val isDarkMode: Boolean = true,
    val isEyeComfortOn: Boolean = false,
    val isPowerSavingOn: Boolean = false,
    val isAutoRotateOn: Boolean = true,
    val isAirplaneModeOn: Boolean = false,
    val isHotspotOn: Boolean = false,
    val batteryLevel: Int = 94,
    val isCharging: Boolean = false,
    val selectedWallpaperRes: String = "img_samsung_wallpaper_1",
    val navigationType: NavigationType = NavigationType.BUTTONS_3,
    val activeCall: ActiveCallState = ActiveCallState(),
    val notifications: List<SystemNotification> = listOf(
        SystemNotification(
            id = "notif_msg_1",
            app = GalaxyAppType.MESSAGES,
            title = "Mẹ Yêu ❤️",
            message = "Tối nay con có về ăn cơm không mẹ nấu canh chua cá lóc?",
            timeAgo = "10 phút trước",
            quickActionText = "Trả lời"
        ),
        SystemNotification(
            id = "notif_galaxy_ai",
            app = GalaxyAppType.SETTINGS,
            title = "Galaxy AI",
            message = "Tính năng Khoanh tròn để tìm kiếm (Circle to Search) đã sẵn sàng.",
            timeAgo = "1 giờ trước"
        ),
        SystemNotification(
            id = "notif_weather_1",
            app = GalaxyAppType.WEATHER,
            title = "Thời tiết Hà Nội / TP.HCM",
            message = "Nhiệt độ hiện tại 29°C • Nắng nhẹ • Độ ẩm 65%",
            timeAgo = "2 giờ trước"
        ),
        SystemNotification(
            id = "notif_health",
            app = GalaxyAppType.GALAXY_STORE,
            title = "Samsung Health",
            message = "Mục tiêu hôm nay: 6.420 / 8.000 bước chân 🏃‍♂️",
            timeAgo = "Vừa xong"
        )
    )
)
