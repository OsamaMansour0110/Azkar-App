package com.learining.AzkarApp.APIs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CountryClient {
    
    private const val BASE_URL = "https://restcountries.com/"
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val countryService: CountryApiService by lazy {
        retrofit.create(CountryApiService::class.java)
    }
}
