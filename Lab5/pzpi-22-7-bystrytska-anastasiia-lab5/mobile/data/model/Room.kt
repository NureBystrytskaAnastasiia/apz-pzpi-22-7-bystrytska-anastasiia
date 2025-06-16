package com.example.mobile.data.model

import java.util.Date

data class Room(
    val _id: String,
    val name: String,
    val description: String? = null,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val updatedAt: String? = null // Змінюємо на String для простоти
)