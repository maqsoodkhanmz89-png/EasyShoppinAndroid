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

data class ProductResponse(
    val success: Boolean,
    val message: String,
    val products: List<Product>,
    val total: Int
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String,
    val rating: Rating,
    val isNew: Boolean
)

data class Rating(
    val rate: Double,
    val count: Int
)

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String
)

data class CartResponse(
    val success: Boolean,
    val message: String,
    val cartItems: List<CartItem>,
    val itemCount: Int,
    val subtotal: Double
)

data class AddToCartRequest(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String
)

data class UpdateCartRequest(
    val productId: String,
    val quantity: Int
)
