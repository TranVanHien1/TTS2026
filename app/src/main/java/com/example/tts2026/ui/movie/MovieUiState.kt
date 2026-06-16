package com.example.tts2026.ui.movie

import com.example.tts2026.data.remote.ProductDto

data class MovieUiState(
    val isLoading: Boolean = false,
    val allProducts: List<ProductDto> = emptyList(),
    val filteredProducts: List<ProductDto> = emptyList(),
    val products: List<ProductDto> = emptyList(),
    val ratingInput: String = "",
    val filterMessage: String = "Nhap rating va bam Tim de loc bang async/await",
    val priceSortOrder: PriceSortOrder = PriceSortOrder.NONE,
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalFilteredProducts: Int = 0,
    val totalPages: Int = 1,
    val errorMessage: String? = null
)

enum class PriceSortOrder {
    NONE,
    ASCENDING,
    DESCENDING
}
