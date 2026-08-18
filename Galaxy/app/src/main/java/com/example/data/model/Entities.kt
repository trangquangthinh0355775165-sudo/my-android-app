package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Ghi chú",
    val colorHex: String = "#FFD97D",
    val isPinned: Boolean = false,
    val isDrawing: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val email: String = "",
    val avatarColorHex: String = "#1E6FFB",
    val isFavorite: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val callType: String, // INCOMING, OUTGOING, MISSED
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
)

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Báo thức",
    val repeatDays: String = "T2, T3, T4, T5, T6",
    val isEnabled: Boolean = true,
    val isVibrate: Boolean = true
)

@Entity(tableName = "gallery_items")
data class GalleryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val drawableResName: String = "",
    val localUri: String = "",
    val category: String = "Camera", // Camera, Screenshots, Wallpapers, Art
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val megapixels: String = "200 MP",
    val aperture: String = "f/1.7"
)

@Entity(tableName = "voice_memos")
data class VoiceMemoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val durationFormatted: String = "00:00",
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

