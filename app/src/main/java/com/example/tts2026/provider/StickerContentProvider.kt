package com.example.tts2026.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.room.Room
import com.example.tts2026.data.local.AppDatabase
import com.example.tts2026.data.local.StickerImageEntity
import com.example.tts2026.util.StickerImageProcessor
import java.io.File

class StickerContentProvider : ContentProvider() {

    private lateinit var database: AppDatabase

    override fun onCreate(): Boolean {
        // Provider la cau noi rieng cho WhatsApp.
        // UI khong doc provider nay; WhatsApp se query provider sau khi app gui intent add pack.
        val appContext = context?.applicationContext ?: return false
        database = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            // WhatsApp query provider truc tiep va can Cursor tra ve ngay.
            .allowMainThreadQueries()
            .build()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Moi sticker user chon duoc expose thanh mot pack ao theo id trong Room:
        // content://<authority>/metadata/sticker_pack_<id>
        // content://<authority>/stickers/sticker_pack_<id>
        val pathSegments = uri.pathSegments
        return when {
            pathSegments.size == 1 && pathSegments[0] == PATH_METADATA -> {
                createPackMetadataCursor(database.stickerImageDao().getStickerImagesForProvider())
            }
            pathSegments.size == 2 && pathSegments[0] == PATH_METADATA -> {
                createPackMetadataCursor(listOfNotNull(findStickerByPackIdentifier(pathSegments[1])))
            }
            pathSegments.size == 2 && pathSegments[0] == PATH_STICKERS -> {
                createStickerListCursor(pathSegments[1])
            }
            else -> null
        }
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        // WhatsApp goi vao day de doc file anh that cua pack ao.
        // File duoc lay tu thu muc luu local chung filesDir/stickers/my_pack_1,
        // khong lay tu Cloudinary URL, vi WhatsApp chi doc duoc asset qua ContentProvider.
        val pathSegments = uri.pathSegments
        if (pathSegments.size != 3 || pathSegments[0] != PATH_STICKERS_ASSET) {
            return null
        }

        val packIdentifier = pathSegments[1]
        val fileName = pathSegments[2]
        val sticker = findStickerByPackIdentifier(packIdentifier) ?: return null
        val allowedFiles = getPackFileNames(sticker)
        if (fileName !in allowedFiles) return null

        val file = File(getStickerPackDirectory(), fileName)
        if (!file.exists()) return null

        val parcelFileDescriptor = ParcelFileDescriptor.open(
            file,
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        return AssetFileDescriptor(parcelFileDescriptor, 0, file.length())
    }

    override fun getType(uri: Uri): String? {
        val pathSegments = uri.pathSegments
        return when (pathSegments.firstOrNull()) {
            PATH_METADATA -> "vnd.android.cursor.dir/vnd.com.example.tts2026.stickerpack"
            PATH_STICKERS -> "vnd.android.cursor.dir/vnd.com.example.tts2026.sticker"
            PATH_STICKERS_ASSET -> {
                if (pathSegments.lastOrNull() == StickerImageProcessor.TRAY_FILE_NAME) {
                    "image/png"
                } else {
                    "image/webp"
                }
            }
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    private fun createPackMetadataCursor(stickers: List<StickerImageEntity>): Cursor {
        // Metadata mo ta sticker pack, khong phai mot sticker don le.
        // Moi row la mot pack ao sticker_pack_<id> ung voi mot sticker da upload.
        val cursor = MatrixCursor(PACK_METADATA_COLUMNS)
        stickers
            .filter { sticker -> isStickerFileValid(sticker) }
            .forEach { sticker ->
                cursor.addRow(
                    arrayOf(
                        sticker.toPackIdentifier(),
                        "Sticker ${sticker.id}",
                        PACK_PUBLISHER,
                        StickerImageProcessor.TRAY_FILE_NAME,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        IMAGE_DATA_VERSION,
                        1,
                        0
                    )
                )
            }
        context?.contentResolver?.let { resolver ->
            cursor.setNotificationUri(resolver, AUTHORITY_URI)
        }
        return cursor
    }

    private fun createStickerListCursor(packIdentifier: String): Cursor {
        // WhatsApp yeu cau toi thieu 3 sticker/pack.
        // Vi demo dang add tung sticker, provider bo sung 2 sticker trong vao pack ao.
        val cursor = MatrixCursor(STICKER_COLUMNS)
        val sticker = findStickerByPackIdentifier(packIdentifier)
        if (sticker != null && isStickerFileValid(sticker)) {
            cursor.addRow(arrayOf(sticker.localFileName, DEFAULT_EMOJI, sticker.publicId))

        }
        context?.contentResolver?.let { resolver ->
            cursor.setNotificationUri(resolver, AUTHORITY_URI)
        }
        return cursor
    }

    private fun findStickerByPackIdentifier(packIdentifier: String): StickerImageEntity? {
        val id = packIdentifier.removePrefix(PACK_PREFIX).toLongOrNull() ?: return null
        return database.stickerImageDao().getStickerImageById(id)
    }

    private fun StickerImageEntity.toPackIdentifier(): String {
        return "$PACK_PREFIX$id"
    }

    private fun getPackFileNames(sticker: StickerImageEntity): Set<String> {
        return setOf(
            StickerImageProcessor.TRAY_FILE_NAME,
            sticker.localFileName,
        )
    }

    private fun isStickerFileValid(sticker: StickerImageEntity): Boolean {
        val file = File(getStickerPackDirectory(), sticker.localFileName)
        val tray = File(getStickerPackDirectory(), StickerImageProcessor.TRAY_FILE_NAME)

        return file.exists() &&
            file.extension == "webp" &&
            sticker.width == STICKER_SIZE &&
            sticker.height == STICKER_SIZE &&
            file.length() <= MAX_STICKER_BYTES &&
            tray.exists()
    }

    private fun getStickerPackDirectory(): File {
        val appContext = context?.applicationContext
            ?: error("StickerContentProvider context is null")
        return File(appContext.filesDir, "stickers/${StickerImageProcessor.DEFAULT_PACK_IDENTIFIER}")
    }

    companion object {
        private const val DATABASE_NAME = "tts2026_sticker.db"
        private const val PACK_PREFIX = "sticker_pack_"
        private const val PACK_PUBLISHER = "TTS2026"
        private const val IMAGE_DATA_VERSION = "1"
        private const val DEFAULT_EMOJI = "\uD83D\uDE00"
        private const val STICKER_SIZE = 512
        private const val MAX_STICKER_BYTES = 100 * 1024

        private const val PATH_METADATA = "metadata"
        private const val PATH_STICKERS = "stickers"
        private const val PATH_STICKERS_ASSET = "stickers_asset"

        private val AUTHORITY_URI: Uri = Uri.Builder()
            .scheme("content")
            .authority("com.example.tts2026.stickercontentprovider")
            .appendPath(PATH_METADATA)
            .build()

        private val PACK_METADATA_COLUMNS = arrayOf(
            "sticker_pack_identifier",
            "sticker_pack_name",
            "sticker_pack_publisher",
            "sticker_pack_icon",
            "android_play_store_link",
            "ios_app_download_link",
            "sticker_pack_publisher_email",
            "sticker_pack_publisher_website",
            "sticker_pack_privacy_policy_website",
            "sticker_pack_license_agreement_website",
            "image_data_version",
            "whatsapp_will_not_cache_stickers",
            "animated_sticker_pack"
        )

        private val STICKER_COLUMNS = arrayOf(
            "sticker_file_name",
            "sticker_emoji",
            "sticker_accessibility_text"
        )
    }
}
