package com.example.playbox2.presentation.videoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.playbox2.data.local.VideoCache

@OptIn(UnstableApi::class)
fun offlinePlayer(context: Context): ExoPlayer {

    val appContext = context.applicationContext

    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(VideoCache.get(appContext))
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(appContext))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    return ExoPlayer.Builder(appContext)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(cacheDataSourceFactory)
        )
        .build()
}