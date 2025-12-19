package com.example.playbox2.presentation.videolist



import GetVideoListUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playbox2.domain.repository.VideoRepository


class VideoListViewModelFactory(
    private val getVideos: GetVideoListUseCase,
    private val repository: VideoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoListViewModel::class.java)) {
            return VideoListViewModel(
                getVideos = getVideos,
                repository = repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
