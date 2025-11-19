package com.example.playbox2.presentation.videoplayer

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var isBuffering by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {

            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            setMediaItem(mediaItem)

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    isBuffering = (state == Player.STATE_BUFFERING)
                }

                override fun onPlayerError(error: PlaybackException) {
                    hasError = true
                }
            })

            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isBuffering) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (hasError) {
            Text("Error loading video", color = MaterialTheme.colorScheme.error)
        }

        // --- VIDEO PLAYER (Reduced width + rounded)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f) // 92% width (looks cleaner)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(16 / 9f)
        ) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    setControllerShowTimeoutMs(2000)
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CONTROL BUTTONS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { exoPlayer.play() }) { Text("Play") }
            Button(onClick = { exoPlayer.pause() }) { Text("Pause") }
        }
    }
}
