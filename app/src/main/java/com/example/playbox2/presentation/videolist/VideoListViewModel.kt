package com.example.playbox2.presentation.videolist

import GetVideoListUseCase
import androidx.compose.runtime.Composable



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playbox2.domain.model.Video

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoListViewModel(
    private val getVideos: GetVideoListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<List<Video>>(emptyList())
    val state: StateFlow<List<Video>> = _state

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _state.value = getVideos()
        }
    }
}
