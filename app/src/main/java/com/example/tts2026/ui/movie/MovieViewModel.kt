package com.example.tts2026.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel: nhan event tu UI, goi Repository va quan ly UI state.
@HiltViewModel
class MovieViewModel @Inject constructor(
    // Hilt tim binding ProductRepository va inject implementation vao day.
    private val productRepository: ProductRepository
) : ViewModel() {

    // MutableStateFlow luon giu gia tri MovieUiState moi nhat va phat lai cho collector moi.
    private val _uiState = MutableStateFlow(MovieUiState())
    // asStateFlow an ham update, nen UI chi co the collect chu khong the sua state.
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        // launch coroutine moi.
        // viewModelScope tu huy coroutine khi ViewModel bi huy.
        viewModelScope.launch {
            _uiState.update {
                // Moi lan update tao mot MovieUiState moi va emit cho collector o MovieRoute.
                it.copy(isLoading = true, errorMessage = null)
            }

            productRepository.getProducts()
                .onSuccess { products ->
                    // State moi lam Compose recompose va ve lai danh sach.
                    _uiState.update {
                        it.copy(isLoading = false, products = products)
                    }
                }
                .onFailure {
                    _uiState.update {
                        // Collector nhan state loi, sau do UI chuyen tu loading sang ErrorContent.
                        it.copy(
                            isLoading = false,
                            errorMessage = "Không thể tải danh sách phim"
                        )
                    }
                }
        }
    }
}
