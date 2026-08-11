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
    val message: String? = null,
    val userName: String? = null,
    val email: String? = null
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

@Parcelize
data class CardDetails(
    val cardNumber: String,
    val cardType: String,
    val holderName: String
) : Parcelable

data class PlaceOrderRequest(
    val shippingAddressId: String? = null,
    val paymentMethod: String,
    val cardDetails: CardDetails? = null,
    val couponCode: String? = null,
    val notes: String? = null
)

@Parcelize
data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String,
    val itemTotal: Double
) : Parcelable

@Parcelize
data class Pricing(
    val itemsSubtotal: Double,
    val discount: Double,
    val discountPercentage: Int,
    val tax: Double,
    val taxPercentage: Int,
    val shippingCharges: Double,
    val totalAmount: Double
) : Parcelable

@Parcelize
data class Coupon(
    val code: String,
    val description: String,
    val discountPercentage: Int,
    val discountAmount: Double
) : Parcelable

@Parcelize
data class CollectionDetails(
    val expectedCollectionDate: String,
    val collectionStatus: String,
    val collectionAgent: String? = null
) : Parcelable

@Parcelize
data class Payment(
    val transactionId: String,
    val paymentMethod: String,
    val amount: Double,
    val status: String,
    val timestamp: String,
    val reference: String,
    val collectionDetails: CollectionDetails? = null
) : Parcelable

@Parcelize
data class Tracking(
    val currentStatus: String,
    val estimatedDeliveryDate: String,
    val shippingMethod: String,
    val trackingNumber: String?
) : Parcelable

@Parcelize
data class Order(
    val orderId: String,
    val userId: String,
    val userEmail: String,
    val userName: String,
    val status: String,
    val orderDate: String,
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val pricing: Pricing,
    val coupon: Coupon? = null,
    val payment: Payment,
    val tracking: Tracking,
    val notes: String,
    val invoiceGenerated: Boolean,
    val invoiceUrl: String,
    val createdAt: String
) : Parcelable

data class PlaceOrderResponse(
    val success: Boolean,
    val message: String,
    val order: Order,
    val details: Order
)

data class OrderListResponse(
    val success: Boolean,
    val message: String,
    val orders: List<Order>,
    val total: Int
)

data class OrderDetailsResponse(
    val success: Boolean,
    val message: String,
    val order: Order
)

data class OrderTrackingResponse(
    val success: Boolean,
    val message: String,
    val tracking: Tracking
)
