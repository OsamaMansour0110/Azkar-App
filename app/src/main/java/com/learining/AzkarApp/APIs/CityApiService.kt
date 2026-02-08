package com.learining.AzkarApp.APIs

import com.learining.AzkarApp.Data.model.CitiesRequest
import com.learining.AzkarApp.Data.model.CitiesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CityApiService {
    
    // POST https://countriesnow.space/api/v0.1/countries/cities
    @POST("api/v0.1/countries/cities")
    suspend fun getCitiesByCountry(@Body request: CitiesRequest): Response<CitiesResponse>
}
