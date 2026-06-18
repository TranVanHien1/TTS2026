package com.example.tts2026.data.repository

import android.net.Uri
import com.example.tts2026.data.local.StickerImageEntity
import kotlinx.coroutines.flow.Flow

interface StickerRepository {
    // Output doc du lieu: Room Flow -> Repository -> ViewModel -> Compose.
    fun observeStickerImages(): Flow<List<StickerImageEntity>>

    // Input ghi du lieu: Uri tu picker -> resize/compress -> Cloudinary -> RoomDB.
    suspend fun uploadAndSaveSticker(uri: Uri): Result<Unit>
}
