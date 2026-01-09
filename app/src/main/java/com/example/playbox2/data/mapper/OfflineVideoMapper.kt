package com.example.playbox2.data.mapper

import com.example.playbox2.data.local.entity.OfflineVideoEntity
import com.example.playbox2.domain.model.OfflineVideo

fun OfflineVideo.toEntity() = OfflineVideoEntity(

    id = id,
    title = title,
   filePath = localPath,
    category = category
)

fun OfflineVideoEntity.toDomain() = OfflineVideo(
    id = id,
    title = title,
    localPath = filePath,
    category = category
)

