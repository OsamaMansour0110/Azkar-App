package com.learining.AzkarApp.APIs

import com.learining.AzkarApp.Data.model.CountryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CountryApiService {
    
    @GET("v3.1/all?fields=name")
    suspend fun getAllCountries(): Response<List<CountryResponse>>
}
