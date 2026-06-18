package com.example.tts2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticker_images")
data class StickerImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // URL Cloudinary dung de hien thi lai anh bang Coil va co the sync/share ve sau.
    val cloudinaryUrl: String,
    // publicId cua Cloudinary, can neu sau nay muon quan ly/xoa asset tren cloud.
    val publicId: String,
    // Ten file WebP da tao local trong filesDir/stickers/my_pack_1 truoc khi upload.
    // ContentProvider dung ten file nay de tra asset cho WhatsApp.
    val localFileName: String,
    // Metadata giup kiem tra anh da dung yeu cau sticker 512x512.
    val width: Int,
    val height: Int,
    // Dung luong file local sau khi nen, muc tieu nho hon 100KB cho WhatsApp static sticker.
    val bytes: Long,
    val createdAt: Long = System.currentTimeMillis()
)
