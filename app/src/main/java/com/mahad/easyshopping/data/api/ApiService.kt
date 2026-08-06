package com.mahad.easyshopping.data.api

import com.mahad.easyshopping.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
}
