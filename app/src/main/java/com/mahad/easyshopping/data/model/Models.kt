package com.mahad.easyshopping.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String,
    val phone: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String? = null,
    val message: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val phone: String? = null
)

data class ForgotPasswordRequest(
    val email: String,
    val newPassword: String
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
    val images: List<String>? = null,
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
    @SerializedName("type") val type: String = "",
    @SerializedName("addressLine1") val addressLine1: String = "",
    @SerializedName("addressLine2") val addressLine2: String? = null,
    @SerializedName("city") val city: String = "",
    @SerializedName("state") val state: String = "",
    @SerializedName("country") val country: String = "",
    @SerializedName("zipCode") val zipCode: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("isDefault") val isDefault: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    // Add multiple possible mappings for Name to ensure persistence
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("full_name") val fullNameSnake: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("street") val street: String? = null,
    @SerializedName("phone") val phone: String? = null
) : Parcelable

data class AddressRequest(
    @SerializedName("type") val type: String,
    @SerializedName("addressLine1") val addressLine1: String,
    @SerializedName("addressLine2") val addressLine2: String? = null,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("isDefault") val isDefault: Boolean,
    // Send Name in multiple fields to ensure backend persistence
    @SerializedName("fullName") val fullName: String,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullNameSnake: String,
    @SerializedName("street") val street: String,
    @SerializedName("phone") val phone: String
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
    val cardType: String = "",
    val holderName: String = "",
    // New fields from latest spec
    val cardHolder: String? = null,
    val expiryMonth: String? = null,
    val expiryYear: String? = null,
    val cvv: String? = null
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
    val name: String = "",
    val price: Double,
    val quantity: Int,
    val image: String,
    val itemTotal: Double = 0.0,
    // New fields from latest spec
    val productTitle: String? = null,
    val reviews: List<Review>? = null
) : Parcelable

@Parcelize
data class Pricing(
    val itemsSubtotal: Double,
    val discount: Double,
    val discountPercentage: Int = 0,
    val tax: Double,
    val taxPercentage: Int = 0,
    val shippingCharges: Double,
    val totalAmount: Double = 0.0,
    val total: Double = 0.0
) : Parcelable

@Parcelize
data class Coupon(
    val code: String,
    val description: String? = null,
    val discountPercentage: Int = 0,
    val discountAmount: Double = 0.0,
    val isValid: Boolean = true,
    val appliedAt: String? = null
) : Parcelable

@Parcelize
data class CollectionDetails(
    val expectedCollectionDate: String,
    val collectionStatus: String,
    val collectionAgent: String? = null
) : Parcelable

@Parcelize
data class Payment(
    val transactionId: String? = null,
    val paymentMethod: String,
    val amount: Double = 0.0,
    val status: String = "",
    val timestamp: String? = null,
    val reference: String? = null,
    val collectionDetails: CollectionDetails? = null
) : Parcelable

@Parcelize
data class Tracking(
    val currentStatus: String,
    val estimatedDeliveryDate: String = "",
    val shippingMethod: String = "",
    val trackingNumber: String? = null,
    val trackingHistory: List<TrackingStage>? = null
) : Parcelable

@Parcelize
data class TrackingStage(
    val stage: String,
    val status: String,
    val timestamp: String
) : Parcelable

@Parcelize
data class Order(
    val orderId: String,
    val userId: String,
    val userEmail: String? = null,
    val userName: String? = null,
    val status: String,
    val orderDate: String? = null,
    val items: List<OrderItem>,
    val shippingAddress: Address,
    val pricing: Pricing? = null,
    val coupon: Coupon? = null,
    val appliedCoupon: Coupon? = null,
    val payment: Payment? = null,
    val tracking: Tracking? = null,
    val notes: String? = null,
    val invoiceGenerated: Boolean = false,
    val invoiceUrl: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    // New fields from latest spec
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val shippingCharges: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String? = null,
    val paymentStatus: String? = null
) : Parcelable

data class PlaceOrderResponse(
    val success: Boolean,
    val message: String,
    val order: Order,
    val details: Order? = null
)

data class OrderListResponse(
    val success: Boolean,
    val message: String,
    val orders: List<Order>,
    val pagination: Pagination? = null,
    val total: Int = 0
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

data class OrderDetailsResponse(
    val success: Boolean,
    val message: String,
    val order: Order
)

data class OrderTrackingResponse(
    val success: Boolean,
    val message: String,
    val orderId: String? = null,
    val currentStatus: String? = null,
    val trackingHistory: List<TrackingStage>? = null,
    val tracking: Tracking? = null
)

data class LogoutResponse(
    val success: Boolean,
    val message: String,
    val loggedOutAt: String? = null
)

data class CancelOrderRequest(
    val reason: String? = null
)

data class InitiateReturnRequest(
    val itemIds: List<String>,
    val reason: String,
    val comments: String? = null
)

@Parcelize
data class ReturnRequest(
    val returnId: String,
    val orderId: String,
    val items: List<OrderItem>,
    val reason: String,
    val comments: String? = null,
    val returnAmount: Double,
    val status: String,
    val requestedAt: String,
    val windowExpiresAt: String? = null
) : Parcelable

data class ReturnResponse(
    val success: Boolean,
    val message: String,
    val returnRequest: ReturnRequest? = null,
    val returns: List<ReturnRequest>? = null
)

data class RateItemRequest(
    val rating: Int,
    val review: String? = null
)

@Parcelize
data class Review(
    val reviewId: String,
    val rating: Int,
    val review: String? = null,
    val ratedBy: String? = null,
    val ratedAt: String? = null,
    val verified: Boolean = false
) : Parcelable

data class ReviewResponse(
    val success: Boolean,
    val message: String,
    val review: Review? = null,
    val reviews: List<Review>? = null,
    val itemId: String? = null,
    val productName: String? = null,
    val averageRating: Double? = null,
    val totalReviews: Int? = null
)

data class ReorderRequest(
    val shippingAddressId: String? = null,
    val paymentMethod: String? = null,
    val couponCode: String? = null
)

data class ReorderResponse(
    val success: Boolean,
    val message: String,
    val cartItems: List<CartItem>? = null,
    val cartSummary: CartSummary? = null
)

data class CartSummary(
    val itemCount: Int,
    val subtotal: Double,
    val totalPrice: Double
)

data class OrderStatsResponse(
    val success: Boolean,
    val message: String,
    val stats: OrderStats
)

data class OrderStats(
    val totalOrders: Int,
    val totalSpent: Double,
    val averageOrderValue: Double,
    val ordersByStatus: Map<String, Int>,
    val lastOrderDate: String?,
    val recentOrders: List<RecentOrder>
)

data class RecentOrder(
    val orderId: String,
    val total: Double,
    val status: String,
    val createdAt: String
)

data class InvoiceResponse(
    val success: Boolean,
    val message: String,
    val invoice: Invoice? = null,
    val downloadUrl: String? = null,
    val expiresIn: String? = null
)

data class Invoice(
    val invoiceNumber: String,
    val invoiceDate: String,
    val orderId: String,
    val user: InvoiceUser,
    val items: List<OrderItem>,
    val subtotal: Double,
    val tax: Double,
    val shippingCharges: Double,
    val discount: Double,
    val total: Double,
    val paymentMethod: String,
    val orderStatus: String,
    val invoiceUrl: String,
    val downloadUrl: String
)

data class InvoiceUser(
    val name: String,
    val email: String,
    val address: Address
)
