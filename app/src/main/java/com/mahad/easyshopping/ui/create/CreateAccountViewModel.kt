package com.mahad.easyshopping.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahad.easyshopping.data.api.RetrofitClient
import com.mahad.easyshopping.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onMobileNumberChanged(mobileNumber: String) {
        _uiState.update { it.copy(mobileNumber = mobileNumber) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onRegisterClicked() {
        val name = _uiState.value.name
        val email = _uiState.value.email
        val mobileNumber = _uiState.value.mobileNumber
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        if (name.isBlank() || email.isBlank() || mobileNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(name, email, password, mobileNumber)
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
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

data class CreateAccountUiState(
    val name: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
