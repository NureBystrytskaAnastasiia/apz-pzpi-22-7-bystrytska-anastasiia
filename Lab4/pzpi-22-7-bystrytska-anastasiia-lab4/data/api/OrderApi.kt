package com.example.mobile.data.api

import com.example.mobile.data.model.Order
import com.example.mobile.data.model.InvoiceResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {
    @GET("orders")
    suspend fun getAllOrders(): Response<List<Order>>

    @POST("orders/{id}/complete")
    suspend fun completeOrder(@Path("id") id: String): Response<Order>

    @POST("orders/{id}/invoice")
    suspend fun generateInvoice(@Path("id") id: String): Response<InvoiceResponse>
}