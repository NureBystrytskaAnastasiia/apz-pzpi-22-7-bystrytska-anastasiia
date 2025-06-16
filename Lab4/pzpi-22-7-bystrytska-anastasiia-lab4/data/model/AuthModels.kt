package com.example.mobile.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserData
)

data class UserData(
    val email: String,
    val name: String,
    val role: String
)

data class SimpleResponse(
    val message: String
)
