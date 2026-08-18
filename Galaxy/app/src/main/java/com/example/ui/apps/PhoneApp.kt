package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallLogEntity
import com.example.data.model.ContactEntity
import com.example.system.ActiveCallState
import com.example.system.GalaxyAppType
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PhoneApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.contacts.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Keypad, 1: Recents, 2: Contacts
    var dialedNumber by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddContactDialog by remember { mutableStateOf(false) }

    // If currently in a call, display the active call screen
    if (state.activeCall.isInCall) {
        ActiveCallScreen(
            activeCall = state.activeCall,
            viewModel = viewModel
        )
        return
    }

    Scaffold(
        modifier = modifier
            .testTag("samsung_phone_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        bottomBar = {
            NavigationBar(
                containerColor = if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Dialpad, contentDescription = "Keypad") },
                    label = { Text("Bàn phím", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SamsungBlue,
                        selectedTextColor = SamsungBlue,
                        indicatorColor = SamsungBlue.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "Recents") },
                    label = { Text("Gần đây", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SamsungBlue,
                        selectedTextColor = SamsungBlue,
                        indicatorColor = SamsungBlue.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Contacts, contentDescription = "Contacts") },
                    label = { Text("Danh bạ", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SamsungBlue,
                        selectedTextColor = SamsungBlue,
                        indicatorColor = SamsungBlue.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // KEYPAD TAB
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Number Display Area
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dialedNumber.ifEmpty { " " },
                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (dialedNumber.isNotEmpty()) {
                                // Matching contact suggestion
                                val match = contacts.firstOrNull { it.phoneNumber.replace(" ", "").contains(dialedNumber.replace(" ", "")) }
                                if (match != null) {
                                    Text(
                                        text = match.name,
                                        color = SamsungBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        // Dialer Keypad Matrix (1-9, *, 0, #)
                        val keypadButtons = listOf(
                            listOf("1" to "", "2" to "ABC", "3" to "DEF"),
                            listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
                            listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
                            listOf("*" to "", "0" to "+", "#" to "")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            keypadButtons.forEach { row ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    row.forEach { (digit, sub) ->
                                        KeypadButton(
                                            digit = digit,
                                            sub = sub,
                                            isDarkMode = state.isDarkMode,
                                            onClick = {
                                                viewModel.vibrateShort(20)
                                                dialedNumber += digit
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Actions: Video Call, Call Button, Backspace
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Add Contact from Number
                            IconButton(
                                onClick = {
                                    if (dialedNumber.isNotEmpty()) showAddContactDialog = true
                                },
                                enabled = dialedNumber.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PersonAdd,
                                    contentDescription = "Add Contact",
                                    tint = if (dialedNumber.isNotEmpty()) (if (state.isDarkMode) Color.White else Color.Black) else Color.Gray.copy(alpha = 0.4f)
                                )
                            }

                            // Big Green Call Button
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .shadow(elevation = 6.dp, shape = CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2ECC71))
                                    .clickable {
                                        if (dialedNumber.isNotEmpty()) {
                                            val matchedName = contacts.firstOrNull { it.phoneNumber.replace(" ", "") == dialedNumber.replace(" ", "") }?.name ?: dialedNumber
                                            viewModel.startCall(matchedName, dialedNumber)
                                        } else if (contacts.isNotEmpty()) {
                                            val first = contacts.first()
                                            viewModel.startCall(first.name, first.phoneNumber)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Call,
                                    contentDescription = "Dial",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            // Backspace
                            IconButton(
                                onClick = {
                                    if (dialedNumber.isNotEmpty()) {
                                        viewModel.vibrateShort(15)
                                        dialedNumber = dialedNumber.dropLast(1)
                                    }
                                },
                                enabled = dialedNumber.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Backspace,
                                    contentDescription = "Delete",
                                    tint = if (dialedNumber.isNotEmpty()) (if (state.isDarkMode) Color.White else Color.Black) else Color.Gray.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // RECENTS TAB
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Lịch sử cuộc gọi",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (callLogs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Chưa có cuộc gọi nào",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            val timeFmt = remember { SimpleDateFormat("HH:mm, dd/MM", Locale.getDefault()) }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(callLogs, key = { it.id }) { log ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { viewModel.startCall(log.contactName, log.phoneNumber) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                val (callIcon, iconColor) = when (log.callType) {
                                                    "INCOMING" -> Icons.Rounded.CallReceived to Color(0xFF2ECC71)
                                                    "OUTGOING" -> Icons.Rounded.CallMade to SamsungBlue
                                                    else -> Icons.Rounded.CallMissed to Color(0xFFFF3B30)
                                                }

                                                Icon(
                                                    imageVector = callIcon,
                                                    contentDescription = log.callType,
                                                    tint = iconColor,
                                                    modifier = Modifier.size(20.dp)
                                                )

                                                Column {
                                                    Text(
                                                        text = log.contactName,
                                                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "${log.phoneNumber} • ${timeFmt.format(Date(log.timestamp))}",
                                                        color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.startCall(log.contactName, log.phoneNumber) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Call,
                                                    contentDescription = "Call",
                                                    tint = Color(0xFF2ECC71),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // CONTACTS TAB
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Danh bạ (${contacts.size})",
                                color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { showAddContactDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SamsungBlue, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Add Contact",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Search
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm kiếm danh bạ...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                                unfocusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        val filteredContacts = contacts.filter {
                            searchQuery.isEmpty() || it.name.contains(searchQuery, true) || it.phoneNumber.contains(searchQuery)
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredContacts, key = { it.id }) { contact ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { viewModel.startCall(contact.name, contact.phoneNumber) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(android.graphics.Color.parseColor(contact.avatarColorHex))),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = contact.name.take(1).uppercase(),
                                                    color = Color.White,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = contact.name,
                                                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = contact.phoneNumber,
                                                    color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.openApp(GalaxyAppType.MESSAGES)
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Message,
                                                    contentDescription = "Message",
                                                    tint = SamsungBlue,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.startCall(contact.name, contact.phoneNumber) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Call,
                                                    contentDescription = "Call",
                                                    tint = Color(0xFF2ECC71),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        var newName by remember { mutableStateOf("") }
        var newPhone by remember { mutableStateOf(dialedNumber) }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Thêm liên hệ mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newPhone.isNotBlank()) {
                            viewModel.addContact(
                                ContactEntity(
                                    name = newName.trim(),
                                    phoneNumber = newPhone.trim(),
                                    avatarColorHex = "#1E6FFB"
                                )
                            )
                            showAddContactDialog = false
                            dialedNumber = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Huỷ")
                }
            }
        )
    }
}

@Composable
private fun KeypadButton(
    digit: String,
    sub: String,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(if (isDarkMode) Color(0xFF242730) else Color(0xFFE8EBF2))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                color = if (isDarkMode) Color.White else Color(0xFF15181E),
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (sub.isNotEmpty()) {
                Text(
                    text = sub,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ActiveCallScreen(
    activeCall: ActiveCallState,
    viewModel: GalaxyViewModel
) {
    val durationMin = activeCall.callDurationSeconds / 60
    val durationSec = activeCall.callDurationSeconds % 60
    val durationFormatted = String.format("%02d:%02d", durationMin, durationSec)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
            .padding(vertical = 48.dp, horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E6FFB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeCall.contactName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = activeCall.contactName,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = activeCall.phoneNumber,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Text(
                    text = durationFormatted,
                    color = Color(0xFF2ECC71),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // In-Call Action Grid (Mute, Keypad, Speaker, Video, Add, Bluetooth)
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InCallActionButton(
                        icon = if (activeCall.isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        label = if (activeCall.isMuted) "Đã tắt mic" else "Tắt mic",
                        isActive = activeCall.isMuted,
                        onClick = { viewModel.toggleMuteCall() }
                    )
                    InCallActionButton(
                        icon = Icons.Rounded.Dialpad,
                        label = "Bàn phím",
                        isActive = activeCall.isKeypadOpen,
                        onClick = { }
                    )
                    InCallActionButton(
                        icon = if (activeCall.isSpeakerOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeDown,
                        label = "Loa ngoài",
                        isActive = activeCall.isSpeakerOn,
                        onClick = { viewModel.toggleSpeakerCall() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InCallActionButton(
                        icon = Icons.Rounded.Videocam,
                        label = "Cuộc gọi video",
                        isActive = false,
                        onClick = { }
                    )
                    InCallActionButton(
                        icon = Icons.Rounded.PersonAdd,
                        label = "Thêm cuộc gọi",
                        isActive = false,
                        onClick = { }
                    )
                    InCallActionButton(
                        icon = Icons.Rounded.BluetoothAudio,
                        label = "Bluetooth",
                        isActive = false,
                        onClick = { }
                    )
                }
            }

            // End Call Button (Red Circle)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .clickable { viewModel.endCall() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
private fun InCallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isActive) Color.White else Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
        )
    }
}
