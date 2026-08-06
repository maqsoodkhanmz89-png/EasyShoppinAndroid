package com.mahad.easyshopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        fetchCart()
    }

    fun fetchCart() {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = RetrofitClient.apiService.getCart(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            cartItems = body.cartItems,
                            itemCount = body.itemCount,
                            subtotal = body.subtotal
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to fetch cart") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val token = SessionManager.getBearerToken() ?: return
        if (quantity < 1) {
            deleteItem(productId)
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateCart(token, UpdateCartRequest(productId, quantity))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update { 
                        it.copy(
                            cartItems = body.cartItems,
                            itemCount = body.itemCount,
                            subtotal = body.subtotal
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteItem(productId: String) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteCartItem(token, productId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update { 
                        it.copy(
                            cartItems = body.cartItems,
                            itemCount = body.itemCount,
                            subtotal = body.subtotal
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun addToCart(product: Product) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val request = AddToCartRequest(
                    productId = product.id.toString(),
                    name = product.name,
                    price = product.price,
                    quantity = 1,
                    image = product.image
                )
                val response = RetrofitClient.apiService.addToCart(token, request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update { 
                        it.copy(
                            cartItems = body.cartItems,
                            itemCount = body.itemCount,
                            subtotal = body.subtotal
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val itemCount: Int = 0,
    val subtotal: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
