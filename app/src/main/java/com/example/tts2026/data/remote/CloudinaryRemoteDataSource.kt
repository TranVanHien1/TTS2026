package com.example.tts2026.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CloudinaryRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {

    suspend fun uploadStickerFile(file: File): CloudinaryUploadResponse = withContext(Dispatchers.IO) {
        // Remote step cua flow:
        // file dau vao da la WebP 512x512 do StickerImageProcessor tao.
        // Upload dung unsigned preset, nen app khong can luu API secret.
        val webpMediaType = "image/webp".toMediaType()

        // Cloudinary nhan multipart/form-data:
        // upload_preset xac dinh preset unsigned, file la anh sticker da nen.
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "upload_preset",
                value = CloudinaryConfig.UPLOAD_PRESET
            )
            .addFormDataPart(
                name = "folder",
                value = "tts2026_stickers"
            )
            .addFormDataPart(
                name = "file",
                filename = file.name,
                body = file.asRequestBody(webpMediaType)
            )
            .addFormDataPart(
                name = "resource_type",
                value = "image"
            )
            .build()

        val request = Request.Builder()
            .url(CloudinaryConfig.UPLOAD_URL)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                error("Cloudinary upload failed ${response.code}: $responseBody")
            }

            // Response JSON duoc parse thanh object co secureUrl/publicId.
            // secureUrl se duoc luu vao RoomDB de UI load lai bang Coil.
            json.decodeFromString<CloudinaryUploadResponse>(responseBody)
        }
    }
}
