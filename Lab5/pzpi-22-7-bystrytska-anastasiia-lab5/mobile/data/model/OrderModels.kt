package com.example.mobile.data.model

data class OrderItem(
    val medicineId: String,
    val medicineName: String,
    val supplier: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

data class Order(
    val _id: String,
    val items: List<OrderItem>,
    val status: String,
    val createdAt: String,
    val deliveredAt: String?,
    val completedAt: String?,
    val invoiceNumber: String?,
    val totalAmount: Double
)

data class InvoiceResponse(
    val invoiceNumber: String,
    val pdf: String // Base64-encoded PDF data
)