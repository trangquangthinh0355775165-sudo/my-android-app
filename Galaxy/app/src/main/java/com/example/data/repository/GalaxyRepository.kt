package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class GalaxyRepository(private val db: AppDatabase) {

    // Notes
    val allNotes: Flow<List<NoteEntity>> = db.noteDao().getAllNotes()
    suspend fun saveNote(note: NoteEntity): Long = db.noteDao().insertNote(note)
    suspend fun deleteNote(note: NoteEntity) = db.noteDao().deleteNote(note)
    suspend fun deleteNoteById(id: Long) = db.noteDao().deleteNoteById(id)

    // Contacts
    val allContacts: Flow<List<ContactEntity>> = db.contactDao().getAllContacts()
    suspend fun addContact(contact: ContactEntity): Long = db.contactDao().insertContact(contact)
    suspend fun updateContact(contact: ContactEntity) = db.contactDao().updateContact(contact)
    suspend fun deleteContact(contact: ContactEntity) = db.contactDao().deleteContact(contact)

    // Messages
    val allMessages: Flow<List<MessageEntity>> = db.messageDao().getAllMessages()
    fun getMessagesForContact(phone: String, name: String): Flow<List<MessageEntity>> =
        db.messageDao().getMessagesForContact(phone, name)
    suspend fun sendMessage(message: MessageEntity): Long = db.messageDao().insertMessage(message)

    // Call logs
    val allCallLogs: Flow<List<CallLogEntity>> = db.callLogDao().getAllCallLogs()
    suspend fun addCallLog(log: CallLogEntity): Long = db.callLogDao().insertCallLog(log)
    suspend fun clearCallLogs() = db.callLogDao().clearAllCallLogs()

    // Alarms
    val allAlarms: Flow<List<AlarmEntity>> = db.alarmDao().getAllAlarms()
    suspend fun addAlarm(alarm: AlarmEntity): Long = db.alarmDao().insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = db.alarmDao().updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = db.alarmDao().deleteAlarm(alarm)

    // Gallery
    val allGalleryItems: Flow<List<GalleryItemEntity>> = db.galleryDao().getAllGalleryItems()
    suspend fun addGalleryItem(item: GalleryItemEntity): Long = db.galleryDao().insertGalleryItem(item)
    suspend fun updateGalleryItem(item: GalleryItemEntity) = db.galleryDao().updateGalleryItem(item)
    suspend fun deleteGalleryItem(item: GalleryItemEntity) = db.galleryDao().deleteGalleryItem(item)

    // Voice Memos
    val allVoiceMemos: Flow<List<VoiceMemoEntity>> = db.voiceMemoDao().getAllVoiceMemos()
    suspend fun addVoiceMemo(memo: VoiceMemoEntity): Long = db.voiceMemoDao().insertVoiceMemo(memo)
    suspend fun deleteVoiceMemo(memo: VoiceMemoEntity) = db.voiceMemoDao().deleteVoiceMemo(memo)
}
