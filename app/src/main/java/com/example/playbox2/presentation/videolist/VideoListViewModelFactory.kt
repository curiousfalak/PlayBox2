package com.example.playbox2.presentation.videolist



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playbox2.data.local.NetworkMonitor
import com.example.playbox2.domain.repository.VideoRepository



class VideoListViewModelFactory(
        private val repository: VideoRepository,
        private val networkMonitor: NetworkMonitor
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VideoListViewModel(repository, networkMonitor) as T
        }
    }


