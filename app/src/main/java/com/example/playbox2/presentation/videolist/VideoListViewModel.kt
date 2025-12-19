package com.example.playbox2.presentation.videolist

import GetVideoListUseCase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoListViewModel(
    private val getVideos: GetVideoListUseCase,
    private val repository: VideoRepository
) : ViewModel() {

    private val _state = MutableStateFlow<List<Video>>(emptyList())
    val state: StateFlow<List<Video>> = _state

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            try {
                val videos = getVideos()
                // Log each video's URL to check correctness
                videos.forEach { video ->
                    Log.d("VideoListViewModel", "Video ID: ${video.id}, Title: ${video.title}, URL: ${video.streamUrl}")
                }
                _state.value = videos
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error fetching videos: ${e.message}", e)
            }
        }
    }

    fun downloadVideo(video: Video) {
        viewModelScope.launch {
            try {
                Log.d("VideoListViewModel", "Downloading video: ${video.id} - ${video.title}")
                repository.saveOfflineVideo(
                    OfflineVideo(
                        id = video.id,
                        title = video.title,
                        streamUrl = video.streamUrl,
                        category = video.category
                    )
                )
                Log.d("VideoListViewModel", "Video downloaded successfully: ${video.id}")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "Error downloading video: ${video.id}", e)
            }
        }
    }
}
