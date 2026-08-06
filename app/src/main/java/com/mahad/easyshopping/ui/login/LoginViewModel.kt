package com.mahad.easyshopping.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.SessionManager
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.LoginRequest
import com.mahad.easyshopping.data.model.SocialLoginRequest
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
                    SessionManager.token = response.body()?.token
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

    fun onSocialLoginClicked(provider: String) {
        // In a real app, this would trigger the Social SDK (Google, Facebook, etc.)
        // For this demo, we simulate a successful SDK callback with mock data
        val mockRequest = when (provider) {
            "gmail" -> SocialLoginRequest(
                provider = "gmail",
                providerUserId = "google-123",
                email = "google_user@example.com",
                userName = "Google User"
            )
            "facebook" -> SocialLoginRequest(
                provider = "facebook",
                providerUserId = "fb-456",
                email = "fb_user@example.com",
                userName = "Facebook User"
            )
            "instagram" -> SocialLoginRequest(
                provider = "instagram",
                providerUserId = "ig-789",
                email = "ig_user@example.com",
                userName = "Instagram User"
            )
            else -> null
        }

        mockRequest?.let { request ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val response = RetrofitClient.apiService.socialLogin(request)
                    if (response.isSuccessful) {
                        SessionManager.token = response.body()?.token
                        _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: ""
                        val displayError = if (errorBody.contains("<!DOCTYPE html>")) {
                            "404 Not Found: The server at http://192.168.0.103:3000 does not have a route for POST /api/auth/social-login. Please check your backend route definitions."
                        } else {
                            errorBody.ifBlank { "Social login failed" }
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = displayError) }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "An error occurred") }
                }
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
