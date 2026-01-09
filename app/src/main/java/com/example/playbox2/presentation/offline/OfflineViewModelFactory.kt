package com.example.playbox2.presentation.offline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playbox2.domain.repository.VideoRepository



class OfflineViewModelFactory(
    private val repository: VideoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(OfflineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OfflineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
