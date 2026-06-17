package com.example.tts2026.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StickerImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Database gom DAO luu cac sticker da upload.
    // Hien tai moi sticker trong bang nay co the duoc ContentProvider expose thanh mot pack ao.
    // Neu sau nay can pack that gom nhieu sticker, co the them StickerPackEntity rieng.
    abstract fun stickerImageDao(): StickerImageDao
}
