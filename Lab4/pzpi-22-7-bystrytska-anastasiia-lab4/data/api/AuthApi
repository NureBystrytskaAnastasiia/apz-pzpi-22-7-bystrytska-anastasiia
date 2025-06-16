package com.example.mobile.data.api

import com.example.mobile.data.model.LoginRequest
import com.example.mobile.data.model.LoginResponse
import com.example.mobile.data.model.RegisterRequest
import com.example.mobile.data.model.SimpleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<SimpleResponse>

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Body emailRequest: Map<String, String>): Response<SimpleResponse>

    @POST("/api/logout")
    suspend fun logout(): Response<SimpleResponse>
}
