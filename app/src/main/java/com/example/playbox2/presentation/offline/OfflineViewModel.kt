package com.example.playbox2.presentation.offline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OfflineViewModel(
    private val repository: VideoRepository
) : ViewModel() {

    val offlineVideos: StateFlow<List<OfflineVideo>> =
        repository.getOfflineVideos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
