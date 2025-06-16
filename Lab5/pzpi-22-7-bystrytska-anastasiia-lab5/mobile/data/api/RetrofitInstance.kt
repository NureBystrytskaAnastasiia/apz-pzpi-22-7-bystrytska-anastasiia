package com.example.mobile.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:5000"
    private const val MEDICINE_BASE_URL = "http://10.0.2.2:5000/api/"

    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    val medicineApi: MedicineApi by lazy {
        Retrofit.Builder()
            .baseUrl(MEDICINE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedicineApi::class.java)
    }
    val orderApi: OrderApi by lazy {
        Retrofit.Builder()
            .baseUrl(MEDICINE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApi::class.java)
    }
    val roomApi: RoomApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoomApi::class.java)
    }
    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
}
