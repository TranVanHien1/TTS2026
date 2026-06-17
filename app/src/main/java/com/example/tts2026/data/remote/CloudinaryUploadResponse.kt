package com.example.tts2026.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudinaryUploadResponse(
    @SerialName("secure_url")
    val secureUrl: String,
    @SerialName("public_id")
    val publicId: String,
    val width: Int = 0,
    val height: Int = 0,
    val bytes: Long = 0
)
