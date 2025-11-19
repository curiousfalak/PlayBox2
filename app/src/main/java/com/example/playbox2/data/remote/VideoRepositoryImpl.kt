package com.example.playbox2.data.remote



import com.example.playbox2.data.mapper.toVideo
import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository


class VideoRepositoryImpl(
    private val api: VideoApi
) : VideoRepository {

    override suspend fun getVideos(): List<Video> {
        return api.getVideos().map { it.toVideo() }
    }
}
