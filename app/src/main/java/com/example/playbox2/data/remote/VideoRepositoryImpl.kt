package com.example.playbox2.data.remote





import com.example.playbox2.data.local.dao.VideoDao
import com.example.playbox2.data.mapper.toDomain
import com.example.playbox2.data.mapper.toEntity
import com.example.playbox2.data.mapper.toVideo
import com.example.playbox2.data.remote.dto.VideoDto
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoRepositoryImpl(
    private val api: VideoApi,
    private val dao: VideoDao
) : VideoRepository {

    override suspend fun getVideos(): List<Video> {
        return api.getVideos().map { it.toVideo() }
    }


    override fun getOfflineVideos(): Flow<List<OfflineVideo>> =
        dao.getAllOfflineVideos().map { it.map { v -> v.toDomain() } }

    override fun getOfflineVideosByCategory(category: String): Flow<List<OfflineVideo>> =
        dao.getVideosByCategory(category).map { it.map { v -> v.toDomain() } }

    override suspend fun saveOfflineVideo(video: OfflineVideo) {
        dao.insertVideo(video.toEntity())
    }

    override suspend fun deleteOfflineVideo(videoId: String) {
        dao.deleteVideo(videoId)
    }

    override suspend fun isDownloaded(videoId: String): Boolean =
        dao.isVideoDownloaded(videoId)


}
