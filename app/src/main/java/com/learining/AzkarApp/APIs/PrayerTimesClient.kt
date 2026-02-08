package com.learining.AzkarApp.APIs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PrayerTimesClient {
    
    private const val BASE_URL = "https://api.aladhan.com/"
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val prayerTimesService: PrayerTimesApiService by lazy {
        retrofit.create(PrayerTimesApiService::class.java)
    }
}
