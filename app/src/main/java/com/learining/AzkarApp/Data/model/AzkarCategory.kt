package com.learining.AzkarApp.Data.model

data class AzkarCategory(
    val id: Int,
    val name: String,
    val items: List<AzkarItem>
)

