package com.mahad.easyshopping.data.api

import com.mahad.easyshopping.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/accounts/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/auth/social-login")
    suspend fun socialLogin(@Body request: SocialLoginRequest): Response<LoginResponse>

    @GET("api/dashboard/home/products")
    suspend fun getProducts(): Response<ProductResponse>

    @GET("api/cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<CartResponse>

    @POST("api/cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<CartResponse>

    @PUT("api/cart/update")
    suspend fun updateCart(
        @Header("Authorization") token: String,
        @Body request: UpdateCartRequest
    ): Response<CartResponse>

    @DELETE("api/cart/delete/{productId}")
    suspend fun deleteCartItem(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Response<CartResponse>

    @GET("api/addresses")
    suspend fun getAddresses(
        @Header("Authorization") token: String
    ): Response<AddressListResponse>

    @POST("api/addresses")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body request: AddressRequest
    ): Response<AddressActionResponse>

    @PUT("api/addresses/{id}")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Path("id") addressId: String,
        @Body request: AddressRequest
    ): Response<AddressActionResponse>

    @DELETE("api/addresses/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") addressId: String
    ): Response<AddressActionResponse>

    @POST("api/orders/place")
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body request: PlaceOrderRequest
    ): Response<PlaceOrderResponse>

    @GET("api/orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<OrderListResponse>

    @GET("api/orders/{orderId}")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderDetailsResponse>

    @GET("api/orders/{orderId}/track")
    suspend fun trackOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderTrackingResponse>

    @POST("api/orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: CancelOrderRequest
    ): Response<OrderDetailsResponse>

    @GET("api/orders/{orderId}/invoice")
    suspend fun getInvoice(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<InvoiceResponse>

    @GET("api/invoices/{orderId}/download")
    suspend fun downloadInvoice(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<InvoiceResponse>

    @POST("api/orders/{orderId}/return")
    suspend fun initiateReturn(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: InitiateReturnRequest
    ): Response<ReturnResponse>

    @GET("api/orders/{orderId}/return")
    suspend fun getReturnStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<ReturnResponse>

    @POST("api/orders/{orderId}/items/{itemId}/rate")
    suspend fun rateItem(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Path("itemId") itemId: String,
        @Body request: RateItemRequest
    ): Response<ReviewResponse>

    @GET("api/orders/{orderId}/items/{itemId}/reviews")
    suspend fun getItemReviews(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Path("itemId") itemId: String
    ): Response<ReviewResponse>

    @POST("api/orders/{orderId}/reorder")
    suspend fun reorder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: ReorderRequest
    ): Response<ReorderResponse>

    @GET("api/orders/stats")
    suspend fun getOrderStats(
        @Header("Authorization") token: String
    ): Response<OrderStatsResponse>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body body: Map<String, String> = emptyMap()
    ): Response<LogoutResponse>
}
