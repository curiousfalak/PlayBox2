package com.example.playbox2.data.mapper

import com.example.playbox2.domain.model.Video
import com.example.playbox2.data.remote.dto.VideoDto


fun VideoDto.toVideo(): Video {
    val baseUrl = "http://10.191.62.69:8000"
    return Video(
        id = filename,
        title = filename,
        streamUrl = "$baseUrl$url",
        thumbnailUrl = thumbnailUrl,
        category = category
    )
}


