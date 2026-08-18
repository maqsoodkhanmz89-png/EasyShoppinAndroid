package com.mahad.easyshopping.ui.address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.Address
import com.mahad.easyshopping.data.model.AddressRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    init {
        fetchAddresses()
    }

    fun fetchAddresses() {
        viewModelScope.launch {
            _fetchAddressesInternal()
        }
    }

    private suspend fun _fetchAddressesInternal() {
        val token = SessionManager.token
        Log.d("AddressViewModel", "Fetching addresses. Token present: ${!token.isNullOrBlank()}")
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val response = RetrofitClient.apiService.getAddresses()
            Log.d("AddressViewModel", "Fetch response: ${response.code()}")
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        addresses = body?.addresses?.toList() ?: emptyList()
                    )
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to load addresses"
                Log.e("AddressViewModel", "Fetch error: $errorMsg")
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        } catch (e: Exception) {
            Log.e("AddressViewModel", "Fetch exception", e)
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    fun addAddress(request: AddressRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.addAddress(request)
                Log.d("AddressViewModel", "Add address response: ${response.code()}")
                if (response.isSuccessful) {
                    // Update list BEFORE calling onSuccess to ensure UI is ready
                    _fetchAddressesInternal()
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to add address"
                    Log.e("AddressViewModel", "Add address error: $errorMsg")
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("AddressViewModel", "Add address exception", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateAddress(addressId: String, request: AddressRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.updateAddress(addressId, request)
                Log.d("AddressViewModel", "Update response: ${response.code()}")
                if (response.isSuccessful) {
                    _fetchAddressesInternal()
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to update address"
                    Log.e("AddressViewModel", "Update error: $errorMsg")
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("AddressViewModel", "Update exception", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteAddress(addressId)
                if (response.isSuccessful) {
                    _fetchAddressesInternal()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete address") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}

data class AddressUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
