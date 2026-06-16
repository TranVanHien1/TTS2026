package com.example.tts2026.ui.movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tts2026.R
import com.example.tts2026.data.remote.ProductDto

@Composable
fun MovieRoute(
    // Lay MovieViewModel tu Hilt theo lifecycle cua man hinh.
    viewModel: MovieViewModel = hiltViewModel()
) {
    // Chuyen StateFlow cua ViewModel thanh Compose State.
    // Khi lifecycle STARTED/RESUMED: bat dau collect cac gia tri moi.
    // Khi lifecycle STOPPED: tam dung collect de tranh cap nhat UI khong can thiet.
    // Moi lan StateFlow emit, uiState thay doi va MovieRoute duoc recompose.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // MVVM: state di xuong View, event onRetry di nguoc len ViewModel.
    ProductScreen(
        uiState = uiState,
        onRetry = viewModel::loadMovies,
        onRatingInputChanged = viewModel::onRatingInputChanged,
        onFilterByRating = viewModel::filterProductsByRating,
        onClearFilter = viewModel::clearRatingFilter,
        onSortPriceAscending = viewModel::sortPriceAscending,
        onSortPriceDescending = viewModel::sortPriceDescending,
        onClearPriceSort = viewModel::clearPriceSort,
        onPreviousPage = viewModel::goToPreviousPage,
        onNextPage = viewModel::goToNextPage
    )
}

@Composable
fun ProductScreen(
    uiState: MovieUiState,
    onRetry: () -> Unit,
    onRatingInputChanged: (String) -> Unit,
    onFilterByRating: () -> Unit,
    onClearFilter: () -> Unit,
    onSortPriceAscending: () -> Unit,
    onSortPriceDescending: () -> Unit,
    onClearPriceSort: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Danh sach san pham",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            ProductFilterBar(
                uiState = uiState,
                onRatingInputChanged = onRatingInputChanged,
                onFilterByRating = onFilterByRating,
                onClearFilter = onClearFilter,
                onSortPriceAscending = onSortPriceAscending,
                onSortPriceDescending = onSortPriceDescending,
                onClearPriceSort = onClearPriceSort,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage
            )

            // ProductScreen doc snapshot uiState hien tai do collector cung cap.
            // Chi Composable doc cac field thay doi moi can duoc Compose ve lai.
            when {
                uiState.isLoading -> LoadingContent()
                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage,
                    onRetry = onRetry
                )
                else -> ProductList(products = uiState.products)
            }
        }
    }
}

@Composable
private fun ProductFilterBar(
    uiState: MovieUiState,
    onRatingInputChanged: (String) -> Unit,
    onFilterByRating: () -> Unit,
    onClearFilter: () -> Unit,
    onSortPriceAscending: () -> Unit,
    onSortPriceDescending: () -> Unit,
    onClearPriceSort: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Loc san pham",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(text = uiState.filterMessage, fontSize = 10.sp)
            Text(text = "tong san pham: ${uiState.totalFilteredProducts}", fontSize = 10.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.ratingInput,
                    onValueChange = onRatingInputChanged,
                    label = { Text("Rating >=",
                        fontSize = 10.sp)},
                    textStyle = TextStyle(fontSize = 10.sp),
                    modifier = Modifier
                        .height(48.dp)
                        .width(150.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Button(onClick = onFilterByRating) {
                    Text("Tim", fontSize = 10.sp)
                }
                Button(onClick = onClearFilter) {
                    Text("Xoa", fontSize = 10.sp)
                }
            }

            Text(
                text = when (uiState.priceSortOrder) {
                    PriceSortOrder.NONE -> "Sap xep gia: mac dinh"
                    PriceSortOrder.ASCENDING -> "Sap xep gia: thap den cao"
                    PriceSortOrder.DESCENDING -> "Sap xep gia: cao den thap"
                },
                fontSize = 10.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSortPriceAscending,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Gia tang",fontSize = 10.sp)
                }
                Button(
                    onClick = onSortPriceDescending,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gia giam", fontSize = 10.sp)
                }
                Button(
                    onClick = onClearPriceSort,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mac dinh", fontSize = 10.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onPreviousPage,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.currentPage > 1
                ) {
                    Text("Trang truoc", fontSize = 10.sp)
                }
                Text("Trang ${uiState.currentPage}/${uiState.totalPages}", fontSize = 10.sp)
                Button(
                    onClick = onNextPage,
                    enabled = uiState.currentPage < uiState.totalPages
                ) {
                    Text("Trang sau", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Thu lai")
        }
    }
}

@Composable
private fun ProductList(products: List<ProductDto>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductItem(product = product)
        }
    }
}

@Composable
private fun ProductItem(product: ProductDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hien thi hinh anh bang Coil.
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = product.title,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_error),
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Rating: ${product.rating}/5")
                Text(text = "Price: $${product.price}")
            }
        }
    }
}
