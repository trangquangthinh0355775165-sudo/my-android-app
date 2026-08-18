package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MessageEntity
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val allMessages by viewModel.messages.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    var activeConversationContact by remember { mutableStateOf<String?>(null) }
    var activePhoneNumber by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // Group messages by contact
    val conversations = remember(allMessages) {
        allMessages.groupBy { it.contactName }
    }

    Scaffold(
        modifier = modifier
            .testTag("samsung_messages_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeConversationContact == null) {
                // CONVERSATION LIST SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tin nhắn",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                val first = contacts.firstOrNull()
                                if (first != null) {
                                    activeConversationContact = first.name
                                    activePhoneNumber = first.phoneNumber
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .background(SamsungBlue, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "New message",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Search Bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm kiếm tin nhắn...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(22.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                            unfocusedContainerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Conversation List
                    val filteredConversations = conversations.filter { (contactName, msgs) ->
                        searchQuery.isEmpty() || contactName.contains(searchQuery, true) || msgs.any { it.text.contains(searchQuery, true) }
                    }

                    if (filteredConversations.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không có tin nhắn nào",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredConversations.forEach { (contactName, msgList) ->
                                val lastMsg = msgList.lastOrNull() ?: return@forEach
                                val contact = contacts.firstOrNull { it.name == contactName }
                                val phone = contact?.phoneNumber ?: lastMsg.phoneNumber

                                item(key = contactName) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable {
                                                activeConversationContact = contactName
                                                activePhoneNumber = phone
                                            },
                                        shape = RoundedCornerShape(18.dp),
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
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                // Avatar
                                                Box(
                                                    modifier = Modifier
                                                        .size(46.dp)
                                                        .clip(CircleShape)
                                                        .background(SamsungBlue),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = contactName.take(1).uppercase(),
                                                        color = Color.White,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = contactName,
                                                        color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = lastMsg.text,
                                                        color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            Text(
                                                text = timeFmt.format(Date(lastMsg.timestamp)),
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ACTIVE CHAT CONVERSATION SCREEN
                val contactName = activeConversationContact ?: ""
                val contactMessages = allMessages.filter { it.contactName == contactName || it.phoneNumber == activePhoneNumber }
                val listState = rememberLazyListState()

                LaunchedEffect(contactMessages.size) {
                    if (contactMessages.isNotEmpty()) {
                        listState.animateScrollToItem(contactMessages.size - 1)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Chat Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { activeConversationContact = null }) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (state.isDarkMode) Color.White else Color.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SamsungBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contactName.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = contactName,
                                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = activePhoneNumber,
                                    color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.startCall(contactName, activePhoneNumber) }) {
                            Icon(
                                imageVector = Icons.Rounded.Call,
                                contentDescription = "Call",
                                tint = Color(0xFF2ECC71)
                            )
                        }
                    }

                    // Message Bubbles Stream
                    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(contactMessages, key = { it.id }) { msg ->
                            val isMe = msg.isFromMe

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Column(
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 260.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 18.dp,
                                                    topEnd = 18.dp,
                                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                                )
                                            )
                                            .background(
                                                if (isMe) SamsungBlue else (if (state.isDarkMode) Color(0xFF282B35) else Color(0xFFE5E9F2))
                                            )
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = msg.text,
                                            color = if (isMe) Color.White else (if (state.isDarkMode) Color.White else Color(0xFF15181E)),
                                            fontSize = 14.sp,
                                            lineHeight = 19.sp
                                        )
                                    }

                                    Text(
                                        text = timeFmt.format(Date(msg.timestamp)),
                                        color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Message Input Field Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (state.isDarkMode) OneUIDarkSurface else OneUILightSurface)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircleOutline,
                                contentDescription = "Attach",
                                tint = SamsungBlue
                            )
                        }

                        TextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Nhập tin nhắn...", fontSize = 13.sp) },
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = if (state.isDarkMode) OneUIDarkCard else Color(0xFFEBEFF7),
                                unfocusedContainerColor = if (state.isDarkMode) OneUIDarkCard else Color(0xFFEBEFF7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendUserMessage(contactName, activePhoneNumber, messageInput)
                                    messageInput = ""
                                }
                            },
                            enabled = messageInput.isNotBlank(),
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    if (messageInput.isNotBlank()) SamsungBlue else Color.Gray.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
