package com.learining.AzkarApp.Data.model

data class DawaItem(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var ownerName: String = "عثمان الخميس",
    var episodesCount: Int = 0,
    var playlistLink: String = "",
    var isLoved: Boolean = false,
    var userId: String = ""
)
