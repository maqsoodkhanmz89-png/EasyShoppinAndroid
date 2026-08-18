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
            val token = SessionManager.getBearerToken()
            Log.d("AddressViewModel", "Fetching addresses with token: ${token?.take(15)}...")
            if (token == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getAddresses(token)
                Log.d("AddressViewModel", "Fetch response: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            addresses = response.body()?.addresses ?: emptyList()
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
    }

    fun addAddress(request: AddressRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val token = SessionManager.getBearerToken()
            Log.d("AddressViewModel", "Adding address with token: ${token?.take(15)}...")
            if (token == null) {
                _uiState.update { it.copy(errorMessage = "User not logged in") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.addAddress(token, request)
                Log.d("AddressViewModel", "Add address response: ${response.code()}")
                if (response.isSuccessful) {
                    // Update state immediately with the new address if returned, 
                    // or just refresh the full list.
                    val newAddress = response.body()?.address
                    if (newAddress != null) {
                        _uiState.update { current ->
                            current.copy(
                                addresses = current.addresses + newAddress,
                                isLoading = false
                            )
                        }
                    }
                    // Also refresh the full list to be sure and sync everything
                    fetchAddresses()
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
            val token = SessionManager.getBearerToken()
            Log.d("AddressViewModel", "Updating address with token: ${token?.take(15)}...")
            if (token == null) {
                _uiState.update { it.copy(errorMessage = "User not logged in") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.updateAddress(token, addressId, request)
                Log.d("AddressViewModel", "Update response: ${response.code()}")
                if (response.isSuccessful) {
                    val updatedAddress = response.body()?.address
                    if (updatedAddress != null) {
                        _uiState.update { current ->
                            current.copy(
                                addresses = current.addresses.map { if (it.id == addressId) updatedAddress else it },
                                isLoading = false
                            )
                        }
                    }
                    fetchAddresses()
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
