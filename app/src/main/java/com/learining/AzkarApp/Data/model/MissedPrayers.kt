package com.learining.AzkarApp.Data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missed_prayers")
data class MissedPrayers(
    @PrimaryKey val id: Int = 1,
    var fajr: Int,
    var dhuhr: Int,
    var asr: Int,
    var maghrib: Int,
    var isha: Int
)