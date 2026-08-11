package com.mahad.easyshopping.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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

@Parcelize
data class Address(
    val id: String,
    val type: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val phoneNumber: String,
    val isDefault: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null
) : Parcelable

data class AddressRequest(
    val type: String,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val phoneNumber: String,
    val isDefault: Boolean
)

data class AddressListResponse(
    val success: Boolean,
    val message: String,
    val addresses: List<Address>,
    val total: Int
)

data class AddressActionResponse(
    val success: Boolean,
    val message: String,
    val address: Address? = null
)
