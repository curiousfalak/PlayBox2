package com.example.playbox2.domain.repository

import com.example.playbox2.data.remote.dto.VideoDto
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    // ONLINE
    suspend fun getVideos(): List<Video>

    // OFFLINE
    fun getOfflineVideos(): Flow<List<OfflineVideo>>
    fun getOfflineVideosByCategory(category: String): Flow<List<OfflineVideo>>

    suspend fun deleteOfflineVideo(videoId: String)
    suspend fun isDownloaded(videoId: String): Boolean

    // DOWNLOAD
    fun downloadVideo(video: Video)
    suspend fun saveOfflineVideo(video: OfflineVideo)
}




