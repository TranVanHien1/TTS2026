package com.example.tts2026.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tts2026.data.product.ProductRepository
import com.example.tts2026.data.remote.ProductDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
                    val sortedProducts = products.sortByPrice(_uiState.value.priceSortOrder)

                    // State moi lam Compose recompose va ve lai danh sach.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allProducts = products,
                            filteredProducts = sortedProducts,
                            products = sortedProducts.firstPage(),
                            currentPage = 1,
                            totalFilteredProducts = sortedProducts.size,
                            totalPages = sortedProducts.totalPages(it.pageSize),
                            filterMessage = "Da tai ${products.size} product tu API"
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        // Collector nhan state loi, sau do UI chuyen tu loading sang ErrorContent.
                        it.copy(
                            isLoading = false,
                            errorMessage = "Khong the tai danh sach product"
                        )
                    }
                }
        }
    }

    fun onRatingInputChanged(value: String) {
        _uiState.update { it.copy(ratingInput = value) }
    }

    fun filterProductsByRating() {
        viewModelScope.launch {
            val state = _uiState.value
            val minRating = state.ratingInput.toDoubleOrNull()

            if (minRating == null) {
                _uiState.update {
                    it.copy(filterMessage = "Rating khong hop le. Hay nhap so, vi du 4.5")
                }
                return@launch
            }

            _uiState.update {
                it.copy(filterMessage = "Dang loc rating bang async/await...")
            }

            val filteredProducts = filterAndSortProducts(
                products = state.allProducts,
                minRating = minRating,
                sortOrder = state.priceSortOrder
            )

            _uiState.update {
                it.copy(
                    filteredProducts = filteredProducts,
                    products = filteredProducts.firstPage(it.pageSize),
                    currentPage = 1,
                    totalFilteredProducts = filteredProducts.size,
                    totalPages = filteredProducts.totalPages(it.pageSize),
                    filterMessage = "Tim thay ${filteredProducts.size} product co rating >= $minRating"
                )
            }
        }
    }

    fun sortPriceAscending() {
        applyPriceSort(PriceSortOrder.ASCENDING)
    }

    fun sortPriceDescending() {
        applyPriceSort(PriceSortOrder.DESCENDING)
    }

    fun clearRatingFilter() {
        viewModelScope.launch {
            val state = _uiState.value
            val products = filterAndSortProducts(
                products = state.allProducts,
                minRating = null,
                sortOrder = state.priceSortOrder
            )

            _uiState.update {
                it.copy(
                    ratingInput = "",
                    filteredProducts = products,
                    products = products.firstPage(it.pageSize),
                    currentPage = 1,
                    totalFilteredProducts = products.size,
                    totalPages = products.totalPages(it.pageSize),
                    filterMessage = "Da xoa bo loc rating, van giu sap xep gia hien tai"
                )
            }
        }
    }

    fun clearPriceSort() {
        viewModelScope.launch {
            val state = _uiState.value
            val minRating = state.ratingInput.toDoubleOrNull()
            val products = filterAndSortProducts(
                products = state.allProducts,
                minRating = minRating,
                sortOrder = PriceSortOrder.NONE
            )

            _uiState.update {
                it.copy(
                    priceSortOrder = PriceSortOrder.NONE,
                    filteredProducts = products,
                    products = products.firstPage(it.pageSize),
                    currentPage = 1,
                    totalFilteredProducts = products.size,
                    totalPages = products.totalPages(it.pageSize),
                    filterMessage = "Da xoa sap xep gia"
                )
            }
        }
    }

    private fun applyPriceSort(sortOrder: PriceSortOrder) {
        viewModelScope.launch {
            val state = _uiState.value
            val minRating = state.ratingInput.toDoubleOrNull()
            val products = filterAndSortProducts(
                products = state.allProducts,
                minRating = minRating,
                sortOrder = sortOrder
            )

            val ratingText = minRating?.let { " va rating >= $it" }.orEmpty()
            val sortText = when (sortOrder) {
                PriceSortOrder.ASCENDING -> "gia thap den cao"
                PriceSortOrder.DESCENDING -> "gia cao den thap"
                PriceSortOrder.NONE -> "mac dinh"
            }

            _uiState.update {
                it.copy(
                    priceSortOrder = sortOrder,
                    filteredProducts = products,
                    products = products.firstPage(it.pageSize),
                    currentPage = 1,
                    totalFilteredProducts = products.size,
                    totalPages = products.totalPages(it.pageSize),
                    filterMessage = "Dang hien thi $sortText$ratingText"
                )
            }
        }
    }

    private suspend fun filterAndSortProducts(
        products: List<ProductDto>,
        minRating: Double?,
        sortOrder: PriceSortOrder
    ): List<ProductDto> {
        // Demo async/await:
        // async chay viec loc rating tren Dispatchers.Default.
        // Neu khong co rating, Deferred tra ve danh sach goc.
        // await lay ket qua loc, sau do moi sap xep theo gia.
        return coroutineScope {
            val ratingFilterDeferred = async(Dispatchers.Default) {
                if (minRating == null) {
                    products
                } else {
                    products.filter { product -> product.rating >= minRating }
                }
            }

            ratingFilterDeferred.await().sortByPrice(sortOrder)
        }
    }

    private fun List<ProductDto>.sortByPrice(sortOrder: PriceSortOrder): List<ProductDto> {
        return when (sortOrder) {
            PriceSortOrder.ASCENDING -> sortedBy { it.price }
            PriceSortOrder.DESCENDING -> sortedByDescending { it.price }
            PriceSortOrder.NONE -> this
        }
    }

    fun goToNextPage() {
        _uiState.update { state ->
            val nextPage = (state.currentPage + 1).coerceAtMost(state.totalPages)
            state.copy(
                currentPage = nextPage,
                products = state.filteredProducts.page(nextPage, state.pageSize)
            )
        }
    }

    fun goToPreviousPage() {
        _uiState.update { state ->
            val previousPage = (state.currentPage - 1).coerceAtLeast(1)
            state.copy(
                currentPage = previousPage,
                products = state.filteredProducts.page(previousPage, state.pageSize)
            )
        }
    }

    private fun List<ProductDto>.firstPage(pageSize: Int = DEFAULT_PAGE_SIZE): List<ProductDto> {
        return page(page = 1, pageSize = pageSize)
    }

    private fun List<ProductDto>.page(page: Int, pageSize: Int): List<ProductDto> {
        val fromIndex = (page - 1) * pageSize
        if (fromIndex >= size) return emptyList()
        val toIndex = (fromIndex + pageSize).coerceAtMost(size)
        return subList(fromIndex, toIndex)
    }

    private fun List<ProductDto>.totalPages(pageSize: Int): Int {
        if (isEmpty()) return 1
        return (size + pageSize - 1) / pageSize
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
