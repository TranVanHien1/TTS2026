package com.example.tts2026.data.repository

import android.net.Uri
import com.example.tts2026.data.local.StickerImageDao
import com.example.tts2026.data.local.StickerImageEntity
import com.example.tts2026.data.remote.CloudinaryRemoteDataSource
import com.example.tts2026.util.StickerImageProcessor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultStickerRepository @Inject constructor(
    // Local data source: doc/ghi URL sticker vao RoomDB.
    private val stickerImageDao: StickerImageDao,
    // Local processor: bien anh goc thanh file sticker WebP 512x512.
    private val imageProcessor: StickerImageProcessor,
    // Remote data source: upload file da xu ly len Cloudinary.
    private val cloudinaryRemoteDataSource: CloudinaryRemoteDataSource
) : StickerRepository {

    override fun observeStickerImages(): Flow<List<StickerImageEntity>> {
        // UI khong doc database truc tiep.
        // Repository expose Flow de ViewModel collect va day len StateFlow.
        return stickerImageDao.observeStickerImages()
    }

    override suspend fun uploadAndSaveSticker(uri: Uri): Result<Unit> {
        return runCatching {
            // Flow chinh:
            // 1. URI tu picker duoc resize/compress thanh file WebP 512x512.
            // 2. File da toi uu duoc upload len Cloudinary bang unsigned upload preset.
            // 3. Cloudinary tra secure_url/public_id.
            // 4. URL va metadata duoc luu vao RoomDB de app dung lai ve sau.
            // 5. File local van duoc giu trong filesDir/stickers/my_pack_1.
            //    ContentProvider se dung file nay de tao pack ao sticker_pack_<id> cho WhatsApp.
            val processedImage = imageProcessor.resizeToStickerImage(uri)
            val uploadResponse = cloudinaryRemoteDataSource.uploadStickerFile(processedImage.file)

            stickerImageDao.insertStickerImage(
                StickerImageEntity(
                    cloudinaryUrl = uploadResponse.secureUrl,
                    publicId = uploadResponse.publicId,
                    localFileName = processedImage.file.name,
                    width = processedImage.width,
                    height = processedImage.height,
                    bytes = processedImage.bytes
                )
            )
        }
    }
}
