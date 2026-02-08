package com.learining.AzkarApp.Data.model

import com.google.gson.annotations.SerializedName

// Response from Aladhan API
data class PrayerTimesResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: PrayerData
)

data class PrayerData(
    @SerializedName("timings")
    val timings: PrayerTimings,
    @SerializedName("date")
    val date: DateInfo
)

data class PrayerTimings(
    @SerializedName("Fajr")
    val fajr: String,
    @SerializedName("Sunrise")
    val sunrise: String,
    @SerializedName("Dhuhr")
    val dhuhr: String,
    @SerializedName("Asr")
    val asr: String,
    @SerializedName("Sunset")
    val sunset: String,
    @SerializedName("Maghrib")
    val maghrib: String,
    @SerializedName("Isha")
    val isha: String,
    @SerializedName("Imsak")
    val imsak: String = "",
    @SerializedName("Midnight")
    val midnight: String = ""
)

data class DateInfo(
    @SerializedName("readable")
    val readable: String,
    @SerializedName("hijri")
    val hijri: HijriDate? = null
)

data class HijriDate(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("month")
    val month: HijriMonth? = null
)

data class HijriMonth(
    @SerializedName("ar")
    val ar: String = "",
    @SerializedName("en")
    val en: String = ""
)
