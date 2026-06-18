package com.example.tts2026.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerImageDao {

    // Room tra ve Flow: moi lan bang sticker_images thay doi, collector o ViewModel nhan list moi.
    @Query("SELECT * FROM sticker_images ORDER BY createdAt DESC")
    fun observeStickerImages(): Flow<List<StickerImageEntity>>

    // ContentProvider can doc nhanh cac sticker co the expose thanh pack ao cho WhatsApp.
    @Query("SELECT * FROM sticker_images ORDER BY createdAt DESC LIMIT 30")
    fun getStickerImagesForProvider(): List<StickerImageEntity>

    // Tim sticker theo id lay tu packIdentifier dang sticker_pack_<id>.
    @Query("SELECT * FROM sticker_images WHERE id = :id LIMIT 1")
    fun getStickerImageById(id: Long): StickerImageEntity?

    // Sau khi Cloudinary upload thanh cong, Repository insert metadata vao RoomDB.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStickerImage(stickerImage: StickerImageEntity)
}
