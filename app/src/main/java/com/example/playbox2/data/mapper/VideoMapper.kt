package com.example.playbox2.data.mapper

import com.example.playbox2.data.remote.dto.VideoDto
import com.example.playbox2.domain.model.Video


fun VideoDto.toVideo(): Video {
    return Video(
        title = filename,
        streamUrl = "http://10.50.157.69:3000${url}"
    )
}