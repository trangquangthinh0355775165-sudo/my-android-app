package com.example.system

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.GalaxyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GalaxyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = GalaxyRepository(db)

    // Flow Data from Room
    val notes: StateFlow<List<NoteEntity>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val messages: StateFlow<List<MessageEntity>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val callLogs: StateFlow<List<CallLogEntity>> = repository.allCallLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val galleryItems: StateFlow<List<GalleryItemEntity>> = repository.allGalleryItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val voiceMemos: StateFlow<List<VoiceMemoEntity>> = repository.allVoiceMemos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // System & Phone State
    private val _systemState = MutableStateFlow(GalaxySystemState())
    val systemState: StateFlow<GalaxySystemState> = _systemState.asStateFlow()

    private var callTimerJob: Job? = null
    private var volumeHudJob: Job? = null

    init {
        // Start background battery simulation
        startSystemSimulation()
    }

    private fun startSystemSimulation() {
        viewModelScope.launch {
            while (isActive) {
                delay(60000) // update battery or status periodically
                _systemState.update { state ->
                    if (state.isCharging && state.batteryLevel < 100) {
                        state.copy(batteryLevel = state.batteryLevel + 1)
                    } else state
                }
            }
        }
    }

    fun vibrateShort(durationMs: Long = 40) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    // Navigation & App Switching
    fun openApp(app: GalaxyAppType) {
        vibrateShort(25)
        _systemState.update { state ->
            val updatedHistory = if (app != GalaxyAppType.HOME && !state.runningAppHistory.contains(app)) {
                listOf(app) + state.runningAppHistory
            } else {
                state.runningAppHistory
            }
            state.copy(
                currentApp = app,
                isAppDrawerOpen = false,
                isRecentAppsOpen = false,
                isQuickSettingsOpen = false,
                isNotificationShadeOpen = false,
                isEdgePanelOpen = false,
                runningAppHistory = updatedHistory
            )
        }
    }

    fun goHome() {
        vibrateShort(25)
        _systemState.update { state ->
            state.copy(
                currentApp = GalaxyAppType.HOME,
                isAppDrawerOpen = false,
                isRecentAppsOpen = false,
                isQuickSettingsOpen = false,
                isNotificationShadeOpen = false,
                isEdgePanelOpen = false
            )
        }
    }

    fun goBack() {
        vibrateShort(20)
        _systemState.update { state ->
            when {
                state.isQuickSettingsOpen || state.isNotificationShadeOpen -> {
                    state.copy(isQuickSettingsOpen = false, isNotificationShadeOpen = false)
                }
                state.isEdgePanelOpen -> state.copy(isEdgePanelOpen = false)
                state.isRecentAppsOpen -> state.copy(isRecentAppsOpen = false)
                state.isAppDrawerOpen -> state.copy(isAppDrawerOpen = false)
                state.currentApp != GalaxyAppType.HOME -> state.copy(currentApp = GalaxyAppType.HOME)
                else -> state
            }
        }
    }

    fun openRecentApps() {
        vibrateShort(30)
        _systemState.update { state ->
            state.copy(
                isRecentAppsOpen = !state.isRecentAppsOpen,
                isAppDrawerOpen = false,
                isQuickSettingsOpen = false,
                isNotificationShadeOpen = false,
                isEdgePanelOpen = false
            )
        }
    }

    fun closeRecentApp(app: GalaxyAppType) {
        _systemState.update { state ->
            val newHistory = state.runningAppHistory.filter { it != app }
            val nextApp = if (state.currentApp == app) {
                newHistory.firstOrNull() ?: GalaxyAppType.HOME
            } else {
                state.currentApp
            }
            state.copy(runningAppHistory = newHistory, currentApp = nextApp)
        }
    }

    fun closeAllRecentApps() {
        vibrateShort(40)
        _systemState.update { state ->
            state.copy(
                runningAppHistory = emptyList(),
                currentApp = GalaxyAppType.HOME,
                isRecentAppsOpen = false
            )
        }
    }

    fun toggleAppDrawer(open: Boolean? = null) {
        vibrateShort(20)
        _systemState.update { state ->
            state.copy(
                isAppDrawerOpen = open ?: !state.isAppDrawerOpen,
                isRecentAppsOpen = false,
                isQuickSettingsOpen = false,
                isNotificationShadeOpen = false,
                isEdgePanelOpen = false
            )
        }
    }

    fun toggleQuickSettings(open: Boolean? = null) {
        vibrateShort(25)
        _systemState.update { state ->
            state.copy(
                isQuickSettingsOpen = open ?: !state.isQuickSettingsOpen,
                isNotificationShadeOpen = false,
                isEdgePanelOpen = false
            )
        }
    }

    fun toggleNotificationShade(open: Boolean? = null) {
        vibrateShort(25)
        _systemState.update { state ->
            state.copy(
                isNotificationShadeOpen = open ?: !state.isNotificationShadeOpen,
                isQuickSettingsOpen = false,
                isEdgePanelOpen = false
            )
        }
    }

    fun toggleEdgePanel(open: Boolean? = null) {
        vibrateShort(30)
        _systemState.update { state ->
            state.copy(isEdgePanelOpen = open ?: !state.isEdgePanelOpen)
        }
    }

    // Power, Lock & AOD
    fun powerButtonPressed() {
        vibrateShort(45)
        _systemState.update { state ->
            when (state.screenState) {
                PhoneScreenState.UNLOCKED -> state.copy(screenState = PhoneScreenState.LOCK_SCREEN)
                PhoneScreenState.LOCK_SCREEN -> state.copy(screenState = PhoneScreenState.AOD)
                PhoneScreenState.AOD -> state.copy(screenState = PhoneScreenState.LOCK_SCREEN)
            }
        }
    }

    fun unlockPhone() {
        vibrateShort(35)
        _systemState.update { state ->
            state.copy(screenState = PhoneScreenState.UNLOCKED)
        }
    }

    fun wakeToLockScreen() {
        vibrateShort(20)
        _systemState.update { state ->
            state.copy(screenState = PhoneScreenState.LOCK_SCREEN)
        }
    }

    fun togglePhoneFrame(enabled: Boolean? = null) {
        _systemState.update { state ->
            state.copy(isPhoneFrameEnabled = enabled ?: !state.isPhoneFrameEnabled)
        }
    }

    fun toggleSPen() {
        vibrateShort(50)
        _systemState.update { state ->
            val extracted = !state.isSPenExtracted
            if (extracted && state.screenState == PhoneScreenState.UNLOCKED) {
                // Open notes or drawing
                state.copy(isSPenExtracted = extracted, currentApp = GalaxyAppType.NOTES)
            } else {
                state.copy(isSPenExtracted = extracted)
            }
        }
    }

    // Hardware Volume Buttons
    fun adjustVolume(delta: Float) {
        vibrateShort(15)
        _systemState.update { state ->
            val newVol = (state.volumeLevel + delta).coerceIn(0f, 1f)
            state.copy(volumeLevel = newVol, isVolumeHudVisible = true)
        }
        volumeHudJob?.cancel()
        volumeHudJob = viewModelScope.launch {
            delay(2200)
            _systemState.update { it.copy(isVolumeHudVisible = false) }
        }
    }

    // Quick Setting Toggles
    fun toggleWifi() = _systemState.update { it.copy(isWifiOn = !it.isWifiOn) }
    fun toggleBluetooth() = _systemState.update { it.copy(isBluetoothOn = !it.isBluetoothOn) }
    fun toggleMobileData() = _systemState.update { it.copy(isMobileDataOn = !it.isMobileDataOn) }
    fun toggleFlashlight() = _systemState.update { it.copy(isFlashlightOn = !it.isFlashlightOn) }
    fun toggleDarkMode() = _systemState.update { it.copy(isDarkMode = !it.isDarkMode) }
    fun toggleEyeComfort() = _systemState.update { it.copy(isEyeComfortOn = !it.isEyeComfortOn) }
    fun togglePowerSaving() = _systemState.update { it.copy(isPowerSavingOn = !it.isPowerSavingOn) }
    fun toggleAutoRotate() = _systemState.update { it.copy(isAutoRotateOn = !it.isAutoRotateOn) }
    fun toggleAirplaneMode() = _systemState.update { it.copy(isAirplaneModeOn = !it.isAirplaneModeOn) }
    fun toggleHotspot() = _systemState.update { it.copy(isHotspotOn = !it.isHotspotOn) }

    fun cycleSoundMode() {
        vibrateShort(30)
        _systemState.update { state ->
            val nextMode = when (state.soundMode) {
                SoundMode.SOUND -> SoundMode.VIBRATE
                SoundMode.VIBRATE -> SoundMode.MUTE
                SoundMode.MUTE -> SoundMode.SOUND
            }
            state.copy(soundMode = nextMode)
        }
    }

    fun setBrightness(value: Float) {
        _systemState.update { it.copy(brightnessLevel = value.coerceIn(0.1f, 1f)) }
    }

    fun setWallpaper(resName: String) {
        vibrateShort(30)
        _systemState.update { it.copy(selectedWallpaperRes = resName) }
    }

    fun setNavigationType(type: NavigationType) {
        _systemState.update { it.copy(navigationType = type) }
    }

    fun dismissNotification(id: String) {
        _systemState.update { state ->
            state.copy(notifications = state.notifications.filter { it.id != id })
        }
    }

    fun clearAllNotifications() {
        _systemState.update { it.copy(notifications = emptyList()) }
    }

    // Phone / Calling Flow
    fun startCall(name: String, phone: String) {
        vibrateShort(50)
        callTimerJob?.cancel()
        _systemState.update { state ->
            state.copy(
                activeCall = ActiveCallState(
                    isInCall = true,
                    contactName = name.ifEmpty { phone },
                    phoneNumber = phone,
                    isIncoming = false,
                    callDurationSeconds = 0
                ),
                currentApp = GalaxyAppType.PHONE
            )
        }
        callTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _systemState.update { state ->
                    if (state.activeCall.isInCall) {
                        state.copy(
                            activeCall = state.activeCall.copy(
                                callDurationSeconds = state.activeCall.callDurationSeconds + 1
                            )
                        )
                    } else state
                }
            }
        }
    }

    fun endCall() {
        vibrateShort(50)
        val currentCall = _systemState.value.activeCall
        if (currentCall.isInCall) {
            viewModelScope.launch {
                repository.addCallLog(
                    CallLogEntity(
                        contactName = currentCall.contactName,
                        phoneNumber = currentCall.phoneNumber,
                        callType = "OUTGOING",
                        durationSeconds = currentCall.callDurationSeconds
                    )
                )
            }
        }
        callTimerJob?.cancel()
        _systemState.update { state ->
            state.copy(activeCall = ActiveCallState(isInCall = false))
        }
    }

    fun toggleMuteCall() {
        _systemState.update { state ->
            state.copy(activeCall = state.activeCall.copy(isMuted = !state.activeCall.isMuted))
        }
    }

    fun toggleSpeakerCall() {
        _systemState.update { state ->
            state.copy(activeCall = state.activeCall.copy(isSpeakerOn = !state.activeCall.isSpeakerOn))
        }
    }

    // Messages Flow with intelligent auto-reply simulation
    fun sendUserMessage(contactName: String, phone: String, text: String) {
        if (text.isBlank()) return
        vibrateShort(25)
        viewModelScope.launch {
            repository.sendMessage(
                MessageEntity(
                    contactName = contactName,
                    phoneNumber = phone,
                    text = text.trim(),
                    isFromMe = true
                )
            )

            // Simulate realistic reply after short delay
            delay(1500)
            val replyText = when {
                text.contains("cơm", true) || text.contains("ăn", true) -> "Oke con, mẹ để phần cơm nóng cho con nhé ❤️"
                text.contains("ở đâu", true) || text.contains("đang ở", true) -> "Mình đang ở quán cafe cạnh toà nhà Landmark nè!"
                text.contains("giá", true) || text.contains("tiền", true) -> "Tổng cộng hết 150.000đ bạn nha."
                text.contains("chào", true) || text.contains("hi", true) || text.contains("hello", true) -> "Chào bạn! Mình có thể giúp gì cho bạn trên chiếc Galaxy S24 này?"
                text.contains("samsung", true) || text.contains("galaxy", true) -> "Galaxy AI với One UI 6.1 siêu mượt luôn bạn ơi! 🚀"
                else -> "Đã nhận được tin nhắn của bạn nhé! 👍"
            }

            repository.sendMessage(
                MessageEntity(
                    contactName = contactName,
                    phoneNumber = phone,
                    text = replyText,
                    isFromMe = false
                )
            )

            // Add notification
            _systemState.update { state ->
                val newNotif = SystemNotification(
                    id = "msg_${System.currentTimeMillis()}",
                    app = GalaxyAppType.MESSAGES,
                    title = contactName,
                    message = replyText,
                    timeAgo = "Vừa xong"
                )
                state.copy(notifications = listOf(newNotif) + state.notifications)
            }
        }
    }

    // Camera Capture -> Saved into Gallery
    fun capturePhoto(zoomLevel: String, mode: String) {
        vibrateShort(60)
        viewModelScope.launch {
            val item = GalleryItemEntity(
                title = "Ảnh Galaxy $mode ($zoomLevel)",
                drawableResName = if (zoomLevel.contains("100") || zoomLevel.contains("10")) "img_gallery_city" else "img_gallery_nature",
                category = "Camera",
                megapixels = if (zoomLevel == "1x") "200 MP Pro" else "50 MP Optical",
                aperture = "f/1.7 Dual Pixel PDAF"
            )
            repository.addGalleryItem(item)

            // Push capture notification
            _systemState.update { state ->
                val notif = SystemNotification(
                    id = "cam_${System.currentTimeMillis()}",
                    app = GalaxyAppType.GALLERY,
                    title = "Máy ảnh Samsung",
                    message = "Đã lưu ảnh $mode $zoomLevel vào Bộ sưu tập",
                    timeAgo = "Vừa xong"
                )
                state.copy(notifications = listOf(notif) + state.notifications)
            }
        }
    }

    // Notes Actions
    fun createOrUpdateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.saveNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Gallery
    fun toggleFavoriteGalleryItem(item: GalleryItemEntity) {
        viewModelScope.launch {
            repository.updateGalleryItem(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun deleteGalleryItem(item: GalleryItemEntity) {
        viewModelScope.launch {
            repository.deleteGalleryItem(item)
        }
    }

    // Contacts
    fun addContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.addContact(contact)
        }
    }

    // Voice Memos
    fun addVoiceMemo(title: String, durationSeconds: Int, durationFormatted: String) {
        viewModelScope.launch {
            repository.addVoiceMemo(
                VoiceMemoEntity(
                    title = title,
                    durationSeconds = durationSeconds,
                    durationFormatted = durationFormatted
                )
            )
        }
    }

    // Alarms
    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
        }
    }

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.addAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }

    // Device Care / Clean Memory
    fun cleanMemory(onCompleted: () -> Unit) {
        vibrateShort(50)
        viewModelScope.launch {
            delay(1200)
            onCompleted()
        }
    }
}
