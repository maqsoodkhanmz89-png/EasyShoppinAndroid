package com.mahad.easyshopping.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onLoginClicked() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    SessionManager.token = body?.token
                    SessionManager.userName = body?.userName
                    SessionManager.userEmail = body?.email ?: email
                    SessionManager.userPhone = body?.phone
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val displayError = if (errorBody.contains("<!DOCTYPE html>")) {
                        "Server Error: Route not found (404). Please verify the backend endpoint."
                    } else {
                        errorBody.ifBlank { "Login failed" }
                    }
                    _uiState.update { it.copy(isLoading = false, errorMessage = displayError) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "An error occurred") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)
