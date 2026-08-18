package com.mahad.easyshopping.ui.checkout

import android.util.Log
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

class CheckoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

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
                    val addresses = response.body()?.addresses ?: emptyList()
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            addresses = addresses,
                            selectedAddress = addresses.find { addr -> addr.isDefault } ?: addresses.firstOrNull()
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

    fun selectAddress(address: Address) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun updateCardDetails(details: CardDetails) {
        _uiState.update { it.copy(cardDetails = details) }
    }

    fun updateCouponCode(code: String) {
        _uiState.update { it.copy(couponCode = code) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun placeOrder(onSuccess: (Order) -> Unit) {
        val state = _uiState.value
        
        // Validation
        if (state.selectedAddress == null) {
            _uiState.update { it.copy(errorMessage = "Please select a shipping address") }
            return
        }
        
        if (state.paymentMethod.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please select a payment method") }
            return
        }
        
        if (state.paymentMethod in listOf("credit_card", "debit_card")) {
            val card = state.cardDetails
            if (card == null || card.cardNumber.length != 16 || card.holderName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Invalid card details. Card number must be 16 digits.") }
                return
            }
        }
        
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val request = PlaceOrderRequest(
                    shippingAddressId = state.selectedAddress.id,
                    paymentMethod = state.paymentMethod,
                    cardDetails = if (state.paymentMethod in listOf("credit_card", "debit_card")) state.cardDetails else null,
                    couponCode = state.couponCode.ifBlank { null },
                    notes = state.notes.ifBlank { null }
                )
                
                val response = RetrofitClient.apiService.placeOrder(token, request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        val order = body.details ?: body.order
                        _uiState.update { it.copy(isLoading = false, orderSuccess = order) }
                        onSuccess(order)
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = body.message) }
                    }
                } else if (response.code() == 401) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Session expired. Please login again.") }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to place order") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class CheckoutUiState(
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val paymentMethod: String = "",
    val cardDetails: CardDetails? = null,
    val couponCode: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val orderSuccess: Order? = null
)
