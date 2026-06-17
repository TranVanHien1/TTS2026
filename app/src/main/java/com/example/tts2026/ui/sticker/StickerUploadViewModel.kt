package com.example.tts2026.ui.sticker

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.repository.StickerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StickerUploadViewModel @Inject constructor(
    // ViewModel chi phu thuoc vao abstraction StickerRepository.
    // Implementation cu the duoc Hilt bind trong RepositoryModule.
    private val stickerRepository: StickerRepository
) : ViewModel() {

    // MutableStateFlow giu state hien tai cua man hinh.
    // UI chi collect uiState, khong duoc sua truc tiep _uiState.
    private val _uiState = MutableStateFlow(StickerUploadUiState())
    val uiState: StateFlow<StickerUploadUiState> = _uiState.asStateFlow()

    init {
        // Vua vao man hinh la bat dau lang nghe RoomDB.
        // Khi insert sticker moi, Flow tu DAO emit list moi va UI tu cap nhat.
        observeStickerImages()
    }

    private fun observeStickerImages() {
        viewModelScope.launch {
            // Flow RoomDB -> Repository -> ViewModel.
            // collect chay trong viewModelScope, tu huy khi ViewModel bi clear.
            stickerRepository.observeStickerImages().collect { stickerImages ->
                _uiState.update {
                    it.copy(stickerImages = stickerImages)
                }
            }
        }
    }

    fun uploadSticker(uri: Uri) {
        viewModelScope.launch {
            // UI event "chon anh" di vao day.
            // Cap nhat loading truoc de Compose hien thi trang thai dang xu ly.
            _uiState.update {
                it.copy(
                    isUploading = true,
                    errorMessage = null,
                    message = "Dang resize 512x512, nen WebP va upload Cloudinary..."
                )
            }

            // Repository thuc hien toan bo viec nang:
            // resize/compress anh, upload Cloudinary, luu RoomDB.
            // ViewModel chi nhan Result de cap nhat UI state.
            stickerRepository.uploadAndSaveSticker(uri)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            message = "Upload thanh cong. URL da duoc luu vao RoomDB"
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            errorMessage = throwable.message ?: "Upload that bai",
                            message = "Co loi khi upload sticker"
                        )
                    }
                }
        }
    }
}
