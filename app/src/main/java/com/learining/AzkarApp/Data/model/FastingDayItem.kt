package com.learining.AzkarApp.Data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "fastingDay", indices = [Index(value = ["dateDay"], unique = true)])

data class FastingDayItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var status: Boolean = false,
    val dateDay: String,
    var dayNum: String
)