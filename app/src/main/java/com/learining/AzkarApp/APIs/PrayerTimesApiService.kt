package com.learining.AzkarApp.APIs

import com.learining.AzkarApp.Data.model.PrayerTimesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerTimesApiService {
    
    // Example: https://api.aladhan.com/v1/timingsByCity/08-02-2026?city=Cairo&country=Egypt&method=5
    @GET("v1/timingsByCity/{date}")
    suspend fun getPrayerTimesByCity(
        @Path("date") date: String,           // Format: DD-MM-YYYY
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 5      // Egyptian General Authority of Survey
    ): Response<PrayerTimesResponse>
}
