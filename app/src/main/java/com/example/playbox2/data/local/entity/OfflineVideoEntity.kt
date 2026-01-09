package com.example.playbox2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_videos")
data class OfflineVideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val filePath: String,
    val category: String
)
