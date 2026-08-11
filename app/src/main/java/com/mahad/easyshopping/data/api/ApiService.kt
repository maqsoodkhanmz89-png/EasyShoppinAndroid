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
}
