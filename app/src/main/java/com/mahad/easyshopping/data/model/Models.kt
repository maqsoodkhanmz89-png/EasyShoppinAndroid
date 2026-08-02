package com.mahad.easyshopping.data.model

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String? = null,
    val message: String? = null
)

data class ForgotPasswordRequest(
    val email: String,
    val newPassword: String
)

data class SocialLoginRequest(
    val provider: String,
    val providerUserId: String,
    val email: String,
    val userName: String
)
