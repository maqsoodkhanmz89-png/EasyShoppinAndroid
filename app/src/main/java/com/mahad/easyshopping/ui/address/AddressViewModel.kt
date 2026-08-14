package com.mahad.easyshopping.ui.address

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
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getAddresses(token)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            addresses = response.body()?.addresses ?: emptyList()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load addresses") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun addAddress(request: AddressRequest, onSuccess: () -> Unit) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = RetrofitClient.apiService.addAddress(token, request)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchAddresses()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to add address"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateAddress(addressId: String, request: AddressRequest, onSuccess: () -> Unit) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = RetrofitClient.apiService.updateAddress(token, addressId, request)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchAddresses()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to update address"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteAddress(addressId: String) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteAddress(token, addressId)
                if (response.isSuccessful) {
                    fetchAddresses()
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
