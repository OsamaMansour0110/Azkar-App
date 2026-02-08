package com.learining.AzkarApp.APIs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CityClient {
    
    private const val BASE_URL = "https://countriesnow.space/"
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val cityService: CityApiService by lazy {
        retrofit.create(CityApiService::class.java)
    }
}
