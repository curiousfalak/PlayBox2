package com.example.playbox2.data.remote



import com.example.playbox2.data.remote.dto.VideoDto
import retrofit2.http.GET

interface VideoApi {

    @GET("api/videos")
    suspend fun getVideos(): List<VideoDto>
}
