package com.example.tts2026.util

import java.io.File

data class ProcessedStickerImage(
    val file: File,
    val width: Int,
    val height: Int,
    val bytes: Long
)
