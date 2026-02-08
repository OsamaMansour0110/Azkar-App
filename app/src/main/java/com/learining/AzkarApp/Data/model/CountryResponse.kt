package com.learining.AzkarApp.Data.model

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("name")
    val name: CountryName
)

data class CountryName(
    @SerializedName("common")
    val common: String,
    @SerializedName("official")
    val official: String = ""
)
