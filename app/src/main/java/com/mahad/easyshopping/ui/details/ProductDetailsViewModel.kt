package com.mahad.easyshopping.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    fun fetchProductDetails(productId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // The single product endpoint /api/products/:id is returning 404 on the server.
                // Since the list endpoint already contains full product data including images,
                // we use it and filter by ID to ensure the product is found.
                val response = RetrofitClient.apiService.getProducts()
                if (response.isSuccessful) {
                    val product = response.body()?.products?.find { it.id == productId }
                    if (product != null) {
                        _uiState.update { it.copy(isLoading = false, product = product) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Product not found") }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.message()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class ProductDetailsUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
