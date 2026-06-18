package com.example.tts2026.ui.sticker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tts2026.data.local.StickerImageEntity
import java.io.File

@Composable
fun StickerUploadRoute(
    viewModel: StickerUploadViewModel = hiltViewModel()
) {
    // UI collect StateFlow tu ViewModel theo lifecycle.
    // Khi RoomDB co sticker moi hoac trang thai upload thay doi, uiState emit va Compose recompose.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Route noi state va event:
    // state di tu ViewModel xuong UI, event chon anh di tu UI nguoc len ViewModel.
    StickerUploadScreen(
        uiState = uiState,
        onImageSelected = viewModel::uploadSticker
    )
}

@Composable
fun StickerUploadScreen(
    uiState: StickerUploadUiState,
    onImageSelected: (android.net.Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSticker by remember {
        mutableStateOf<StickerImageEntity?>(null)
    }
    val context = LocalContext.current
    val addStickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val validationError = result.data?.getStringExtra(WHATSAPP_VALIDATION_ERROR_EXTRA)
        when {
            !validationError.isNullOrBlank() -> {
                Toast.makeText(
                    context,
                    "WhatsApp tu choi pack: $validationError",
                    Toast.LENGTH_LONG
                ).show()
            }
            result.resultCode == Activity.RESULT_OK -> {
                Toast.makeText(context, "Da gui yeu cau sang WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Picker tra ve Uri cua anh tren thiet bi.
    // Uri nay chua phai file sticker; ViewModel se gui Uri vao Repository de resize/compress/upload.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sticker Cloudinary Demo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = uiState.message)
                    uiState.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        // Event dau tien cua flow: nguoi dung bam nut, Android mo picker anh.
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !uiState.isUploading
                    ) {
                        Text(if (uiState.isUploading) "Dang upload..." else "Chon anh tu thiet bi")
                    }
                }
            }

            Text(
                text = "Da luu ${uiState.stickerImages.size} sticker URL",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (uiState.isUploading) {
                // isUploading nam trong UiState, nen UI khong can tu quan ly bien loading rieng.
                UploadingContent()
            }

            // Danh sach nay den tu RoomDB thong qua Flow -> Repository -> ViewModel -> StateFlow.
            StickerList(
                stickerImages = uiState.stickerImages,
                onStickerClick = { sticker ->
                    selectedSticker = sticker
                }
            )

            selectedSticker?.let { sticker ->
                // Dialog nay chi preview sticker dang chon tren UI cua app.
                // Khi bam Add to WhatsApp, app khong tu them truc tiep vao WhatsApp,
                // ma gui packIdentifier dang sticker_pack_<id> de WhatsApp query provider.
                // Provider se tra ve sticker dang chon + 2 sticker trong de du toi thieu 3 sticker/pack.
                StickerPreviewDialog(
                    sticker = sticker,
                    onDismiss = {
                        selectedSticker = null
                    },
                    onAddToWhatsApp = {
                        // Goi intent chinh thuc de WhatsApp mo dialog add sticker pack.
                        // Dialog nay la UI rieng cua WhatsApp, app minh chi truyen id/authority/name.
                        // Sau intent nay, WhatsApp se query StickerContentProvider de doc pack.
                        val intent = createWhatsAppAddStickerIntent(
                            context = context,
                            sticker = sticker,
                            packIdentifier = sticker.toPackIdentifier(),
                            packName = "Sticker ${sticker.id}"
                        )
                        if (intent != null) {
                            try {
                                addStickerLauncher.launch(intent)
                            } catch (exception: Exception) {
                                Toast.makeText(
                                    context,
                                    "Khong mo duoc WhatsApp: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            selectedSticker = null
                        }
                    }
                )
            }

        }
    }
}

@Composable
private fun UploadingContent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Text(text = "Dang xu ly anh va upload...")
    }
}

@Composable
private fun StickerList(stickerImages: List<StickerImageEntity>, onStickerClick: (StickerImageEntity) -> Unit) {
    if (stickerImages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chua co sticker nao trong RoomDB")
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = stickerImages,
            key = { item -> item.id }
        ) { sticker ->
            StickerItem(sticker = sticker, onClick = {onStickerClick(sticker)})
        }
    }
}

@Composable
private fun StickerItem(sticker: StickerImageEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coil load anh tu secure_url Cloudinary da luu trong RoomDB.
            AsyncImage(
                model = sticker.cloudinaryUrl,
                contentDescription = sticker.publicId,
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sticker.publicId,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${sticker.width} x ${sticker.height}px")
                Text(text = "Local size: ${sticker.bytes.toKbText()}")
                Text(
                    text = sticker.cloudinaryUrl,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StickerPreviewDialog(
    sticker: StickerImageEntity,
    onDismiss: () -> Unit,
    onAddToWhatsApp: () -> Unit
) {
    val whatsapp = Color(0xFF25D366)

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(text = "Sticker")},
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = sticker.cloudinaryUrl,
                    contentDescription = sticker.publicId,
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = sticker.publicId,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = "${sticker.width} x ${sticker.height}px")
                Text(text = "Size: ${sticker.bytes.toKbText()}")
            }
        },
        confirmButton = {
            Button(onClick = onAddToWhatsApp,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = whatsapp,
                    contentColor = Color.White)) {
                Text(text = "Add to WhatsApp")
            }
        },
                dismissButton = {TextButton(onClick = onDismiss) {Text(text = "Dong")}})

}

private fun Long.toKbText(): String {
    return "${this / 1024} KB"
}

private fun StickerImageEntity.toPackIdentifier(): String {
    return "$PACK_PREFIX$id"
}

private fun createWhatsAppAddStickerIntent(
    context: Context,
    sticker: StickerImageEntity,
    packIdentifier: String,
    packName: String
): Intent? {
    // Buoc truoc khi mo dialog cua WhatsApp:
    // 1. Kiem tra may co WhatsApp/WhatsApp Business.
    // 2. Kiem tra thu muc local co sticker dang chon, 2 sticker trong va tray icon.
    // 3. Tao intent ENABLE_STICKER_PACK voi pack id ao va authority cua provider.
    val whatsAppPackage = getInstalledWhatsAppPackage(context)
    if (whatsAppPackage == null) {
        Toast.makeText(
            context,
            "Khong tim thay WhatsApp tren thiet bi",
            Toast.LENGTH_SHORT
        ).show()
        return null
    }

    if (!hasEnoughLocalStickerFiles(context, sticker)) {
        Toast.makeText(
            context,
            "Hay upload lai sticker nay de tao file local va sticker trong cho WhatsApp",
            Toast.LENGTH_LONG
        ).show()
        return null
    }

    val authority = "${context.packageName}.stickercontentprovider"
    return Intent(WHATSAPP_ENABLE_STICKER_PACK_ACTION).apply {
        setPackage(whatsAppPackage)
        putExtra("sticker_pack_id", packIdentifier)
        putExtra("sticker_pack_authority", authority)
        putExtra("sticker_pack_name", packName)
    }
}

private fun getInstalledWhatsAppPackage(context: Context): String? {
    return listOf("com.whatsapp", "com.whatsapp.w4b").firstOrNull { packageName ->
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (exception: PackageManager.NameNotFoundException) {
            false
        }
    }
}

private fun hasEnoughLocalStickerFiles(context: Context, sticker: StickerImageEntity): Boolean {
    // Check nhanh o UI de tranh goi WhatsApp khi chac chan file local chua hop le.
    // Provider van se loc lai bang RoomDB va validate kich thuoc/dung luong lan cuoi.
    val packDirectory = File(context.filesDir, "stickers/$DEFAULT_BASE_PACK_IDENTIFIER")
    val selectedStickerExists = File(packDirectory, sticker.localFileName).exists()
    val hasTrayIcon = File(packDirectory, "tray.png").exists()
    val hasBlankOne = File(packDirectory, "blank_1.webp").exists()
    val hasBlankTwo = File(packDirectory, "blank_2.webp").exists()
    return selectedStickerExists && hasTrayIcon && hasBlankOne && hasBlankTwo
}

private const val WHATSAPP_ENABLE_STICKER_PACK_ACTION = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
private const val WHATSAPP_VALIDATION_ERROR_EXTRA = "validation_error"
private const val DEFAULT_BASE_PACK_IDENTIFIER = "my_pack_1"
private const val PACK_PREFIX = "sticker_pack_"
