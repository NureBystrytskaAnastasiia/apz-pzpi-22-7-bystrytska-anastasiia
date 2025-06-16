package com.example.mobile.data.api

import com.example.mobile.data.model.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("/api/users")
    suspend fun getAllUsers(): Response<List<User>>

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<Unit>
}
