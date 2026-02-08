package com.learining.AzkarApp.Data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "fortune", indices = [Index(value = ["zikr"], unique = true)])
data class FortuneItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val zikr: String,
    val summary: String,
    val hadith: String,
    val source: String,
    val score: Int
)
