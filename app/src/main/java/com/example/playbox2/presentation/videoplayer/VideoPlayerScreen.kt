package com.example.playbox2.presentation.videoplayer

import android.content.res.Configuration
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.playbox2.data.local.VideoCache


/* ---------- ORIENTATION HELPER ---------- */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/* ---------- VIDEO PLAYER SCREEN ---------- */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val landscape = isLandscape()

    var isBuffering by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    Log.setLogLevel(Log.LOG_LEVEL_ALL)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {


            addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {
                    isBuffering = state == Player.STATE_BUFFERING
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlayerError(error: PlaybackException) {
                    hasError = true
                }
            })
        }
    }
    val dataSourceFactory = CacheDataSource.Factory()
        .setCache(VideoCache.get(context))
        .setUpstreamDataSourceFactory(
            DefaultDataSource.Factory(context)
        )
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(
            MediaItem.fromUri(Uri.parse(videoUrl))
        )

    LaunchedEffect(videoUrl) {
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }


    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (landscape) Modifier.verticalScroll(scrollState)
                    else Modifier
                )
        ) {

            /* ---------- TOP BAR ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Now Playing",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            /* ---------- VIDEO VIEW ---------- */
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (landscape) 260.dp else 220.dp)
                    .background(Color.Black),
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer

                        useController = true
                        controllerAutoShow = true
                        controllerShowTimeoutMs = 0

                        setBackgroundColor(android.graphics.Color.BLACK)
                    }
                }
            )




            Spacer(modifier = Modifier.height(16.dp))



            /* ---------- EXTRA SPACE FOR SCROLL ---------- */
            if (landscape) {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        /* ---------- BUFFERING ---------- */
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }

        /* ---------- ERROR ---------- */
        if (hasError) {
            Text(
                text = "Error loading video",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
