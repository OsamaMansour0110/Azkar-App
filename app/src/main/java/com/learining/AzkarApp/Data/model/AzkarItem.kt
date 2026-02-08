package com.learining.AzkarApp.Data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "azkar", indices = [Index(value = ["text"], unique = true)])
data class AzkarItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    var count: Int
)