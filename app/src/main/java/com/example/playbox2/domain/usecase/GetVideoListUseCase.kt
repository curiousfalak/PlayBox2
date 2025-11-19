import com.example.playbox2.domain.model.Video
import com.example.playbox2.domain.repository.VideoRepository


class GetVideoListUseCase(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(): List<Video> {
        return repository.getVideos()
    }
}