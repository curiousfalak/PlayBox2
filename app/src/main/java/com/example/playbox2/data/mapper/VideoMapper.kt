package com.example.playbox2.data.mapper

import com.example.playbox2.domain.model.Video
import com.example.playbox2.data.remote.dto.VideoDto


fun VideoDto.toVideo(): Video {
    val baseUrl = "https://playbox2.onrender.com"
    return Video(
        id = filename,
        title = filename,
        streamUrl = "$baseUrl$url",
        thumbnailUrl = thumbnailUrl,
        category = category
    )
}


