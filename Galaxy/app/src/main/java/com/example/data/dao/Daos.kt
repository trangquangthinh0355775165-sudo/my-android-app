package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, lastModified DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY isFavorite DESC, name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE phoneNumber = :phone OR contactName = :name ORDER BY timestamp ASC")
    fun getMessagesForContact(phone: String, name: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM messages WHERE phoneNumber = :phone OR contactName = :name")
    suspend fun deleteMessagesForContact(phone: String, name: String)
}

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogEntity): Long

    @Query("DELETE FROM call_logs")
    suspend fun clearAllCallLogs()
}

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}

@Dao
interface GalleryDao {
    @Query("SELECT * FROM gallery_items ORDER BY timestamp DESC")
    fun getAllGalleryItems(): Flow<List<GalleryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(item: GalleryItemEntity): Long

    @Update
    suspend fun updateGalleryItem(item: GalleryItemEntity)

    @Delete
    suspend fun deleteGalleryItem(item: GalleryItemEntity)
}

@Dao
interface VoiceMemoDao {
    @Query("SELECT * FROM voice_memos ORDER BY timestamp DESC")
    fun getAllVoiceMemos(): Flow<List<VoiceMemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceMemo(memo: VoiceMemoEntity): Long

    @Delete
    suspend fun deleteVoiceMemo(memo: VoiceMemoEntity)
}
