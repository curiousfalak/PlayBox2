package com.example.playbox2.presentation.videoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@OptIn(UnstableApi::class)
fun offlinePlayer(context: Context): ExoPlayer {

    val dataSourceFactory = DefaultDataSource.Factory(context)

    return ExoPlayer.Builder(context)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(dataSourceFactory)
        )
        .build()
}
