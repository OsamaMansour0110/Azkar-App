package com.learining.AzkarApp.Data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryClient {

    private const val BASE_URL = "https://api.cloudinary.com/"

    // Connect the Url with The interface
    val api: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }

}
