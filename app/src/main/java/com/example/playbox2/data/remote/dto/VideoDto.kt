package com.example.playbox2.data.remote.dto



data class VideoDto(
    val filename: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val category: String
)

