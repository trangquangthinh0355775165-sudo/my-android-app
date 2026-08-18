package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NoteEntity::class,
        ContactEntity::class,
        MessageEntity::class,
        CallLogEntity::class,
        AlarmEntity::class,
        GalleryItemEntity::class,
        VoiceMemoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun callLogDao(): CallLogDao
    abstract fun alarmDao(): AlarmDao
    abstract fun galleryDao(): GalleryDao
    abstract fun voiceMemoDao(): VoiceMemoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "galaxy_s24_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                val contactDao = db.contactDao()
                val messageDao = db.messageDao()
                val noteDao = db.noteDao()
                val callLogDao = db.callLogDao()
                val alarmDao = db.alarmDao()
                val galleryDao = db.galleryDao()
                val voiceMemoDao = db.voiceMemoDao()

                // 1. Contacts
                val c1 = ContactEntity(name = "Mẹ Yêu ❤️", phoneNumber = "0912 345 678", email = "meyeu@gmail.com", avatarColorHex = "#E91E63", isFavorite = true)
                val c2 = ContactEntity(name = "Bố", phoneNumber = "0988 765 432", email = "bo@gmail.com", avatarColorHex = "#1E6FFB", isFavorite = true)
                val c3 = ContactEntity(name = "Samsung Care+ VIP", phoneNumber = "1800 588 889", email = "support.vn@samsung.com", avatarColorHex = "#0381FE", isFavorite = true)
                val c4 = ContactEntity(name = "Anh Minh (Đồng nghiệp)", phoneNumber = "0903 112 233", email = "minh.tech@galaxy.com", avatarColorHex = "#2ECC71", isFavorite = false)
                val c5 = ContactEntity(name = "Linh (Bạn thân)", phoneNumber = "0977 445 566", email = "linh.nguyen@gmail.com", avatarColorHex = "#8E44AD", isFavorite = true)
                
                contactDao.insertContact(c1)
                contactDao.insertContact(c2)
                contactDao.insertContact(c3)
                contactDao.insertContact(c4)
                contactDao.insertContact(c5)

                // 2. Messages
                val now = System.currentTimeMillis()
                messageDao.insertMessage(MessageEntity(contactName = "Mẹ Yêu ❤️", phoneNumber = "0912 345 678", text = "Tối nay con có về ăn cơm không mẹ nấu canh chua cá lóc?", isFromMe = false, timestamp = now - 7200000))
                messageDao.insertMessage(MessageEntity(contactName = "Mẹ Yêu ❤️", phoneNumber = "0912 345 678", text = "Dạ có ạ, tầm 6h30 con về tới nhà mẹ nhé!", isFromMe = true, timestamp = now - 3600000))
                messageDao.insertMessage(MessageEntity(contactName = "Samsung Care+ VIP", phoneNumber = "1800 588 889", text = "Chào mừng quý khách đến với Galaxy S24 Ultra One UI 6.1! Tính năng Galaxy AI và S-Pen đã sẵn sàng phục vụ.", isFromMe = false, timestamp = now - 86400000))
                messageDao.insertMessage(MessageEntity(contactName = "Linh (Bạn thân)", phoneNumber = "0977 445 566", text = "Cuối tuần này đi cafe chụp hình camera 200MP nha!", isFromMe = false, timestamp = now - 1800000))

                // 3. Notes
                noteDao.insertNote(NoteEntity(
                    title = "✨ Danh sách công việc Galaxy S24",
                    content = "1. Trải nghiệm Galaxy AI Khoanh tròn để tìm kiếm (Circle to Search)\n2. Chụp ảnh đêm Space Zoom 100x\n3. Vẽ phác thảo bằng bút S-Pen trên Samsung Notes\n4. Tùy biến màn hình khoá One UI & Always-On Display",
                    colorHex = "#FFD97D",
                    isPinned = true,
                    category = "Công việc"
                ))
                noteDao.insertNote(NoteEntity(
                    title = "🛒 Đi siêu thị cuối tuần",
                    content = "- Cà phê hạt Arabica\n- Sữa chua Hy Lạp\n- Bánh mì hoa cúc\n- Trái cây nhiệt đới (dâu tây, kiwi)",
                    colorHex = "#B8E986",
                    isPinned = false,
                    category = "Mua sắm"
                ))
                noteDao.insertNote(NoteEntity(
                    title = "🎨 Ý tưởng phác thảo S-Pen",
                    content = "Bản vẽ ý tưởng thiết kế giao diện Samsung Galaxy One UI siêu mượt.",
                    colorHex = "#A8D8EA",
                    isPinned = false,
                    isDrawing = true,
                    category = "S-Pen"
                ))

                // 4. Call logs
                callLogDao.insertCallLog(CallLogEntity(contactName = "Mẹ Yêu ❤️", phoneNumber = "0912 345 678", callType = "INCOMING", timestamp = now - 10800000, durationSeconds = 145))
                callLogDao.insertCallLog(CallLogEntity(contactName = "Linh (Bạn thân)", phoneNumber = "0977 445 566", callType = "OUTGOING", timestamp = now - 21600000, durationSeconds = 320))
                callLogDao.insertCallLog(CallLogEntity(contactName = "Shipper ViettelPost", phoneNumber = "0934 999 888", callType = "MISSED", timestamp = now - 43200000, durationSeconds = 0))

                // 5. Alarms
                alarmDao.insertAlarm(AlarmEntity(hour = 6, minute = 30, label = "Thức dậy buổi sáng ☀️", repeatDays = "T2, T3, T4, T5, T6", isEnabled = true))
                alarmDao.insertAlarm(AlarmEntity(hour = 7, minute = 45, label = "Uống cà phê & Đi làm ☕", repeatDays = "T2, T3, T4, T5, T6", isEnabled = true))
                alarmDao.insertAlarm(AlarmEntity(hour = 22, minute = 30, label = "Nhắc nhở đọc sách & Ngủ 🌙", repeatDays = "Hàng ngày", isEnabled = false))

                // 6. Gallery Items
                galleryDao.insertGalleryItem(GalleryItemEntity(
                    title = "Ảnh chụp đêm 200MP Pro Night",
                    drawableResName = "img_gallery_city",
                    category = "Camera",
                    timestamp = now - 172800000,
                    isFavorite = true,
                    megapixels = "200 MP Super Quad Pixel",
                    aperture = "f/1.7 OIS"
                ))
                galleryDao.insertGalleryItem(GalleryItemEntity(
                    title = "Hoàng hôn Hồ trên núi Đỉnh cao",
                    drawableResName = "img_gallery_nature",
                    category = "Camera",
                    timestamp = now - 259200000,
                    isFavorite = true,
                    megapixels = "50 MP Periscope 5x",
                    aperture = "f/3.4"
                ))
                galleryDao.insertGalleryItem(GalleryItemEntity(
                    title = "Hình nền Galaxy S24 Titanium Crystal",
                    drawableResName = "img_samsung_wallpaper_1",
                    category = "Wallpapers",
                    timestamp = now - 345600000,
                    isFavorite = false,
                    megapixels = "8K Ultra HD",
                    aperture = "Dynamic AMOLED 2X"
                ))
                galleryDao.insertGalleryItem(GalleryItemEntity(
                    title = "Hình nền One UI Silk Abstract",
                    drawableResName = "img_samsung_wallpaper_2",
                    category = "Wallpapers",
                    timestamp = now - 432000000,
                    isFavorite = false,
                    megapixels = "8K Ultra HD",
                    aperture = "120Hz LTPO"
                ))

                // 7. Voice Memos
                voiceMemoDao.insertVoiceMemo(VoiceMemoEntity(
                    title = "Ghi chú cuộc họp chiến lược One UI",
                    durationFormatted = "02:04",
                    durationSeconds = 124,
                    timestamp = now - 86400000
                ))
            }
        }
    }
}
