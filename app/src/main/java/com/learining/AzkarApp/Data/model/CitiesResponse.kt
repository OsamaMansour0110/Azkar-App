package com.learining.AzkarApp.Data.model

import com.google.gson.annotations.SerializedName

// Request body for getting cities
data class CitiesRequest(
    @SerializedName("country")
    val country: String
)

// Response from CountriesNow API for cities
data class CitiesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("data")
    val data: List<String>
)
