package com.mahad.easyshopping.ui.order

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

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun fetchOrders(page: Int = 1) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getOrders(token, page)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            orders = response.body()!!.orders,
                            pagination = response.body()!!.pagination
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load orders") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun fetchOrderDetails(orderId: String) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetails = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getOrderDetails(token, orderId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { 
                        it.copy(
                            isLoadingDetails = false,
                            selectedOrder = response.body()!!.order
                        )
                    }
                    fetchTracking(orderId)
                } else {
                    _uiState.update { it.copy(isLoadingDetails = false, errorMessage = "Order not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingDetails = false, errorMessage = e.message) }
            }
        }
    }

    private fun fetchTracking(orderId: String) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.trackOrder(token, orderId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(trackingHistory = response.body()!!.trackingHistory) }
                }
            } catch (e: Exception) {
                // Ignore tracking error
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAction = true) }
            try {
                val response = RetrofitClient.apiService.cancelOrder(token, orderId, CancelOrderRequest(reason))
                if (response.isSuccessful) {
                    fetchOrderDetails(orderId)
                } else {
                    _uiState.update { it.copy(isLoadingAction = false, errorMessage = "Cancellation failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingAction = false, errorMessage = e.message) }
            }
        }
    }

    fun reorder(orderId: String, onReorderSuccess: () -> Unit) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAction = true) }
            try {
                val response = RetrofitClient.apiService.reorder(token, orderId, ReorderRequest())
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoadingAction = false) }
                    onReorderSuccess()
                } else {
                    _uiState.update { it.copy(isLoadingAction = false, errorMessage = "Reorder failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingAction = false, errorMessage = e.message) }
            }
        }
    }

    fun rateItem(orderId: String, itemId: String, rating: Int, review: String, onSuccess: () -> Unit) {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAction = true) }
            try {
                val response = RetrofitClient.apiService.rateItem(token, orderId, itemId, RateItemRequest(rating, review))
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoadingAction = false) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoadingAction = false, errorMessage = "Review submission failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingAction = false, errorMessage = e.message) }
            }
        }
    }

    fun fetchStats() {
        val token = SessionManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getOrderStats(token)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(stats = response.body()!!.stats) }
                }
            } catch (e: Exception) {
                // Ignore stats error
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val pagination: Pagination? = null,
    val selectedOrder: Order? = null,
    val trackingHistory: List<TrackingStage>? = null,
    val stats: OrderStats? = null,
    val isLoading: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val isLoadingAction: Boolean = false,
    val errorMessage: String? = null
)
