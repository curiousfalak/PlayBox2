package com.example.playbox2.data.remote




import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.playbox2.data.local.dao.VideoDao
import com.example.playbox2.data.mapper.toDomain
import com.example.playbox2.data.mapper.toEntity
import com.example.playbox2.data.mapper.toVideo
import com.example.playbox2.data.remote.dto.VideoDto
import com.example.playbox2.data.worker.VideoDownloadWorker
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoRepositoryImpl(
    private val api: VideoApi,
    private val dao: VideoDao,
    private val context: Context
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

    override fun downloadVideo(video: Video) {


            val data = workDataOf(
                "VIDEO_ID" to video.id,
                "VIDEO_URL" to video.streamUrl,
                "TITLE" to video.title,
                "CATEGORY" to video.category
            )

            val request = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
                .setInputData(data)
                .build()

            WorkManager
                .getInstance(context)
                .enqueue(request)
        }

    }


