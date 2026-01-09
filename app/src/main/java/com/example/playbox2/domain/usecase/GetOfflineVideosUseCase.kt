package com.example.playbox2.domain.usecase

import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow

class GetOfflineVideoListUseCase(
    private val repository: VideoRepository
) {
    operator fun invoke(): Flow<List<OfflineVideo>> {
        return repository.getOfflineVideos()
    }
}
