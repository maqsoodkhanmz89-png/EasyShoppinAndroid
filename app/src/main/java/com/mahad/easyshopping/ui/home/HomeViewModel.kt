package com.mahad.easyshopping.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            products = response.body()?.products ?: emptyList()
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load products" 
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "An error occurred" 
                    ) 
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        val token = SessionManager.getBearerToken()
        
        // Always clear local data and call success to ensure UI updates,
        // even if the network call fails or is not needed.
        val performLocalLogout = {
            SessionManager.logout()
            onSuccess()
        }

        if (token == null) {
            performLocalLogout()
            return
        }

        viewModelScope.launch {
            try {
                // We attempt to notify the server, but we logout locally regardless of result
                RetrofitClient.apiService.logout(token, emptyMap())
                performLocalLogout()
            } catch (e: Exception) {
                // If network fails, we still want to log out the user locally
                performLocalLogout()
            }
        }
    }
}

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
