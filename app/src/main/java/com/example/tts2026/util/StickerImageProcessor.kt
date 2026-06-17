package com.example.tts2026.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class StickerImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun resizeToStickerImage(uri: Uri): ProcessedStickerImage = withContext(Dispatchers.IO) {
        // Xu ly anh la viec nang nen chay tren Dispatchers.IO, khong chan main thread.
        // Input la Uri tu Android picker, doc bang ContentResolver.
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val sourceBitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.isMutableRequired = false
        }

        // Dua anh ve khung 512x512 dung format can cho sticker static.
        // Anh goc giu ti le, phan trong duoc de transparent.
        val stickerBitmap = sourceBitmap.fitCenterInTransparentSquare(STICKER_SIZE)
        // my_pack_1 chi la thu muc local chung de luu asset that.
        // Pack WhatsApp that su duoc expose sau nay la pack ao sticker_pack_<id> trong ContentProvider.
        // WhatsApp doc file o day qua openAssetFile(), khong doc truc tiep Cloudinary.
        val packDirectory = File(context.filesDir, "stickers/$DEFAULT_PACK_IDENTIFIER").apply {
            mkdirs()
        }
        val outputFile = File(
            packDirectory,
            "sticker_${System.currentTimeMillis()}.webp"
        )

        // Nen WebP voi muc chat luong giam dan de file nho hon.
        compressWebpUnderTargetSize(stickerBitmap, outputFile)
        // Tray icon dai dien cho cac pack ao, WhatsApp yeu cau PNG 96x96.
        createTrayIcon(stickerBitmap, File(packDirectory, TRAY_FILE_NAME))
        createBlankStickerIfNeeded(File(packDirectory, BLANK_STICKER_ONE))
        createBlankStickerIfNeeded(File(packDirectory, BLANK_STICKER_TWO))

        sourceBitmap.recycle()
        stickerBitmap.recycle()

        ProcessedStickerImage(
            file = outputFile,
            width = STICKER_SIZE,
            height = STICKER_SIZE,
            bytes = outputFile.length()
        )
    }

    private fun Bitmap.fitCenterInTransparentSquare(size: Int): Bitmap {
        // Tao bitmap vuong nen trong suot, sau do ve anh goc vao giua.
        // Cach nay tranh crop mat noi dung khi anh goc khong phai hinh vuong.
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val scale = minOf(
            size.toFloat() / width.toFloat(),
            size.toFloat() / height.toFloat()
        )
        val targetWidth = width * scale
        val targetHeight = height * scale
        val left = (size - targetWidth) / 2f
        val top = (size - targetHeight) / 2f

        canvas.drawBitmap(
            this,
            null,
            RectF(left, top, left + targetWidth, top + targetHeight),
            paint
        )

        return output
    }

    @Suppress("DEPRECATION")
    private fun compressWebpUnderTargetSize(bitmap: Bitmap, outputFile: File) {
        var quality = 90

        do {
            // Moi vong lap ghi lai file voi quality thap hon.
            // Muc tieu hien tai la <= 100KB de phu hop sticker static WhatsApp.
            FileOutputStream(outputFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, quality, stream)
            }

            quality -= 10
        } while (outputFile.length() > TARGET_MAX_BYTES && quality >= 10)
    }

    @Suppress("DEPRECATION")
    private fun createTrayIcon(bitmap: Bitmap, outputFile: File) {
        val trayBitmap = bitmap.fitCenterInTransparentSquare(TRAY_SIZE)
        FileOutputStream(outputFile).use { stream ->
            trayBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        trayBitmap.recycle()
    }

    @Suppress("DEPRECATION")
    private fun createBlankStickerIfNeeded(outputFile: File) {
        if (outputFile.exists()) return

        val blankBitmap = Bitmap.createBitmap(STICKER_SIZE, STICKER_SIZE, Bitmap.Config.ARGB_8888)
        FileOutputStream(outputFile).use { stream ->
            blankBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream)
        }
        blankBitmap.recycle()
    }

    companion object {
        const val DEFAULT_PACK_IDENTIFIER = "my_pack_1"
        const val TRAY_FILE_NAME = "tray.png"
        const val BLANK_STICKER_ONE = "blank_1.webp"
        const val BLANK_STICKER_TWO = "blank_2.webp"
        private const val STICKER_SIZE = 512
        private const val TRAY_SIZE = 96
        private const val TARGET_MAX_BYTES = 100 * 1024
    }
}
