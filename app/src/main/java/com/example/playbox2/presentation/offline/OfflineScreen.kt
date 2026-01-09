package com.example.playbox2.presentation.offline


import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.playbox2.domain.model.OfflineVideo
import com.example.playbox2.presentation.videoplayer.offlinePlayer
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun OfflineScreen(viewModel: OfflineViewModel) {
    val videos by viewModel.offlineVideos.collectAsState()
    val context = LocalContext.current

    var selectedVideo by remember { mutableStateOf<OfflineVideo?>(null) }

    val player = remember(selectedVideo) {
        selectedVideo?.let { video ->
            offlinePlayer(context).apply {
                setMediaItem(
                    MediaItem.fromUri(
                        File(video.localPath).toUri()
                    )
                )
                prepare()
                playWhenReady = true
            }
        }
    }


    Column(Modifier.fillMaxSize()) {
        if (videos.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No offline videos downloaded")
            }
        } else {

            // Video Player
            selectedVideo?.let {
                DisposableEffect(player) {
                    onDispose {
                        player?.release()
                    }
                }

                AndroidView(
                    factory = { PlayerView(context).apply { this.player = player } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Video List
            LazyColumn(Modifier.fillMaxSize()) {
                items(videos) { video ->
                    Text(
                        text = video.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVideo = video }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
