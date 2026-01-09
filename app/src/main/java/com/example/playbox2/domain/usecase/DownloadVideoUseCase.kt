package com.example.playbox2.domain.usecase

import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository

class DownloadVideoUseCase(
    private val repository: VideoRepository
) {
    operator fun invoke(video: Video) {
        repository.downloadVideo(video)
    }
}
