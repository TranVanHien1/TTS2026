package com.example.tts2026.ui.movie

import com.example.tts2026.data.remote.ProductDto

data class MovieUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val errorMessage: String? = null
)
