package com.example.mobile.data.api

import com.example.mobile.data.model.Room
import com.example.mobile.data.model.RoomResponse
import retrofit2.Response
import retrofit2.http.GET

interface RoomApi {
    @GET("/api/rooms")
    suspend fun getRooms(): Response<RoomResponse>
}