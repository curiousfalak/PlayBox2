package com.example.playbox2.presentation.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playbox2.data.local.NetworkMonitor
import com.example.playbox2.domain.model.AppMode
import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoListViewModel(
    val repository: VideoRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {


    val appMode: StateFlow<AppMode> =
        networkMonitor.isConnected
            .map { connected ->
                if (connected) AppMode.ONLINE else AppMode.OFFLINE
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AppMode.ONLINE
            )

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos

    init {
        viewModelScope.launch {
            loadVideos()
        }
    }

    private suspend fun loadVideos() {
        _videos.value = repository.getVideos()
    }

    fun download(video: Video) {
        repository.downloadVideo(video)
    }
}
