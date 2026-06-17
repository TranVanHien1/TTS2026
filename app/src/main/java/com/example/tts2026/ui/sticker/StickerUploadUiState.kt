package com.example.tts2026.ui.sticker

import com.example.tts2026.data.local.StickerImageEntity

data class StickerUploadUiState(
    val stickerImages: List<StickerImageEntity> = emptyList(),
    val isUploading: Boolean = false,
    val message: String = "Chon anh tu thiet bi de tao sticker 512x512",
    val errorMessage: String? = null
)
