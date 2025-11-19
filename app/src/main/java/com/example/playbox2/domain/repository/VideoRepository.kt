package com.example.playbox2.domain.repository

import com.example.playbox2.domain.model.Video


interface VideoRepository {
    suspend fun getVideos(): List<Video>
}
