package com.example.mobile.data.api

import com.example.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MedicineApi {
    @GET("medicines")
    suspend fun getAllMedicines(): Response<List<MedicineResponse>>

    @GET("medicines/stats")
    suspend fun getMedicineStats(): Response<MedicineStats>

    @GET("medicines/{id}")
    suspend fun getMedicineById(@Path("id") id: String): Response<MedicineResponse>

    @POST("medicines")
    suspend fun createMedicine(@Body medicine: MedicineRequest): Response<MedicineResponse>

    @PUT("medicines/{id}")
    suspend fun updateMedicine(
        @Path("id") id: String,
        @Body medicine: MedicineRequest
    ): Response<MedicineResponse>

    @DELETE("medicines/{id}")
    suspend fun deleteMedicine(@Path("id") id: String): Response<SimpleResponse>

    @PATCH("medicines/{id}")
    suspend fun editMedicineQuantity(
        @Path("id") id: String,
        @Body request: EditQuantityRequest
    ): Response<MedicineResponse>

    @POST("medicines/simulate-sale")
    suspend fun simulateSale(): Response<SaleResponse>

    data class RoomsResponse(
        val rooms: List<StorageRoom>
    )
    @GET("rooms")
    suspend fun getAllRooms(): Response<RoomsResponse>

    @GET("medicines/storage-issues")
    suspend fun getMedicinesWithStorageIssues(): Response<StorageIssuesResponse>
}
